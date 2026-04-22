package com.arf8vhg7.jja.feature.player.revive;

import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSync;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerReviveState;
import com.arf8vhg7.jja.feature.jja.rct.RctAutoService;
import com.arf8vhg7.jja.feature.jja.rct.RctPlayerTick;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class ReviveFlowService {
    public static final int INITIAL_REVIVES = 3;
    public static final int WAIT_TICKS = 2400;
    public static final int HOLD_REQUIRED_TICKS = 60;
    private static final int STUN_INTERVAL_TICKS = 2;
    private static final String KEY_SUPPRESS_NEXT_DEATH_MESSAGE = "jjaReviveSuppressNextDeathMessage";

    private ReviveFlowService() {
    }

    public static boolean isWaiting(@Nullable Entity entity) {
        if (entity == null) {
            return false;
        }
        PlayerReviveState reviveState = PlayerStateAccess.revive(entity);
        return reviveState != null && reviveState.getReviveRemainingTicks() > 0;
    }

    public static int getRemainingRevives(Entity entity) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(entity);
        return reviveState == null ? 0 : reviveState.getRemainingRevives();
    }

    public static JjaReviveSpecialStage getSpecialStage(Entity entity) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(entity);
        return reviveState == null ? JjaReviveSpecialStage.NONE : JjaReviveSpecialStage.fromId(reviveState.getReviveSpecialStage());
    }

    public static boolean shouldSuppressSpecialBranchRctKeyRelease(Entity entity) {
        return entity != null && isWaiting(entity) && getSpecialStage(entity) == JjaReviveSpecialStage.ESSENCE_TRIGGERED;
    }

    public static boolean shouldKeepCoreBranchRctOnStart(@Nullable Entity entity) {
        return entity != null && isWaiting(entity) && shouldKeepCoreBranchRctOnStart(getSpecialStage(entity));
    }

    static boolean shouldKeepCoreBranchRctOnStart(JjaReviveSpecialStage specialStage) {
        return specialStage == JjaReviveSpecialStage.ESSENCE_READY || specialStage == JjaReviveSpecialStage.ESSENCE_TRIGGERED;
    }

    public static boolean canEnterWaiting(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return false;
        }
        if (player.isCreative() || player.isSpectator()) {
            return false;
        }
        if (!player.level().getGameRules().getBoolean(Objects.requireNonNull(JjaReviveGameRules.JJA_REVIVE))) {
            return false;
        }
        PlayerReviveState reviveState = PlayerStateAccess.revive(player);
        return reviveState != null && reviveState.getReviveRemainingTicks() == 0 && reviveState.getRemainingRevives() > 0;
    }

    public static boolean consumeForceDeathBypass(ServerPlayer player) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(player);
        if (reviveState == null || reviveState.getReviveRemainingTicks() >= 0) {
            return false;
        }
        reviveState.setReviveRemainingTicks(0);
        return true;
    }

    public static boolean enterWaiting(ServerPlayer player) {
        if (!canEnterWaiting(player)) {
            return false;
        }
        PlayerReviveState reviveState = PlayerStateAccess.revive(player);
        if (reviveState == null) {
            return false;
        }

        ReviveHoldService.clearOutgoingHold(player, true);
        ReviveHoldService.clearIncomingRescuers(player);
        RctAutoService.stopAutoRct(player, true);
        ReviveEffectService.prepareWaitingEntry(player, reviveState);

        ReviveStateTransitions.enterWaiting(reviveState, WAIT_TICKS);
        markSuppressNextDeathMessage(player);

        JjaPlayerStateSync.sync(player);
        ReviveSyncService.syncWaitingState(player);
        return true;
    }

    public static boolean tryEnterWaitingAfterDeathMessage(ServerPlayer player, DamageSource damageSource) {
        if (player == null || player.level().isClientSide()) {
            return false;
        }
        if (consumeForceDeathBypass(player)) {
            return false;
        }
        boolean shouldDeferToTotemProtection = ReviveTotemProtectionService.shouldDeferToTotemProtection(player, damageSource);
        if (!ReviveTotemProtectionService.shouldStartWaiting(canEnterWaiting(player), shouldDeferToTotemProtection)) {
            return false;
        }
        return enterWaiting(player);
    }

    public static boolean resolveShowDeathMessages(ServerPlayer player, boolean original) {
        if (!original || player == null || !player.getPersistentData().getBoolean(KEY_SUPPRESS_NEXT_DEATH_MESSAGE)) {
            return original;
        }
        clearSuppressNextDeathMessage(player);
        return false;
    }

    public static void tick(ServerPlayer player) {
        ReviveHoldService.tickRescueHold(player);
        if (!isWaiting(player)) {
            return;
        }

        PlayerReviveState reviveState = PlayerStateAccess.revive(player);
        if (reviveState == null) {
            return;
        }
        if ((reviveState.getReviveRemainingTicks() % STUN_INTERVAL_TICKS) == 0) {
            ReviveEffectService.refreshWaitingTick(player, reviveState);
        }
        ReviveEffectService.tickSpecialStage(player, reviveState);
        if (ReviveEffectService.hasCompletedRecovery(player)) {
            clearWaiting(player, false);
            return;
        }

        int remainingTicks = reviveState.getReviveRemainingTicks() - 1;
        reviveState.setReviveRemainingTicks(remainingTicks);
        if (remainingTicks <= 0) {
            forceDeath(player);
            return;
        }
        if (remainingTicks % 20 == 0) {
            ReviveSyncService.syncWaitingState(player);
        }
    }

    public static void handleGiveUp(ServerPlayer player) {
        if (isWaiting(player)) {
            forceDeath(player);
        }
    }

    public static void handleCoreClick(ServerPlayer player) {
        if (!isWaiting(player)) {
            return;
        }
        PlayerReviveState reviveState = PlayerStateAccess.revive(player);
        if (reviveState == null || getSpecialStage(player) != JjaReviveSpecialStage.ESSENCE_READY) {
            return;
        }
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            java.util.Objects.requireNonNull(net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get()),
            net.minecraft.world.effect.MobEffectInstance.INFINITE_DURATION,
            0,
            true,
            false,
            false
        ));
        reviveState.setReviveSpecialStage(JjaReviveSpecialStage.ESSENCE_TRIGGERED.id());
        RctPlayerTick.execute(player.level(), player.getX(), player.getY(), player.getZ(), player);
        ReviveSyncService.syncWaitingState(player);
    }

    public static void markForceDeathBypass(Entity entity) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(entity);
        if (reviveState != null) {
            reviveState.setReviveRemainingTicks(-1);
        }
    }

    public static void clearWaiting(ServerPlayer player, boolean consumeRevive) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(player);
        if (reviveState == null) {
            return;
        }
        ReviveStateTransitions.clearWaiting(reviveState, consumeRevive);
        clearSuppressNextDeathMessage(player);
        clearWaitingRuntime(player);
        JjaReviveFirstAidCompat.onReviveComplete(player);
        syncWaitingExit(player, reviveState);
    }

    public static void forceDeath(ServerPlayer player) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(player);
        if (reviveState == null) {
            return;
        }
        ReviveStateTransitions.forceDeath(reviveState);
        clearWaitingRuntime(player);
        JjaReviveFirstAidCompat.onForceDeath(player);
        syncWaitingExit(player, reviveState);
        player.kill();
    }

    public static float getWaitingHealth(ServerPlayer player) {
        return ReviveEffectService.getWaitingHealth(player);
    }

    private static void clearWaitingRuntime(ServerPlayer player) {
        ReviveEffectService.clearWaitingEffects(player);
        ReviveEffectService.clearWaitingScale(player);
        ReviveHoldService.clearOutgoingHold(player, false);
        ReviveHoldService.clearIncomingRescuers(player);
    }

    private static void markSuppressNextDeathMessage(ServerPlayer player) {
        if (player != null) {
            player.getPersistentData().putBoolean(KEY_SUPPRESS_NEXT_DEATH_MESSAGE, true);
        }
    }

    private static void clearSuppressNextDeathMessage(ServerPlayer player) {
        if (player != null) {
            player.getPersistentData().remove(KEY_SUPPRESS_NEXT_DEATH_MESSAGE);
        }
    }

    private static void syncWaitingExit(ServerPlayer player, PlayerReviveState reviveState) {
        JjaPlayerStateSync.sync(player);
        ReviveSyncService.sendState(player, false, 0, reviveState.getRemainingRevives(), JjaReviveSpecialStage.NONE);
    }
}
