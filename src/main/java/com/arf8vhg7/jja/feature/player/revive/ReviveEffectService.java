package com.arf8vhg7.jja.feature.player.revive;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.compat.pehkui.JjaPehkuiCompat;
import com.arf8vhg7.jja.feature.jja.technique.family.mahoraga.MahoragaAdaptation;
import com.arf8vhg7.jja.feature.player.state.model.PlayerReviveState;
import com.arf8vhg7.jja.feature.jja.rct.RctAdvancementHelper;
import com.arf8vhg7.jja.feature.jja.rct.RctHealGate;
import com.arf8vhg7.jja.feature.jja.rct.RctRuntimeStateAccess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.StunEffectStartedappliedProcedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public final class ReviveEffectService {
    private static final List<MobEffect> PERSISTED_WAITING_EFFECTS = List.of(
        MobEffects.HEALTH_BOOST,
        JujutsucraftModMobEffects.SUKUNA_EFFECT.get()
    );
    private static final int CORE_GRASPED_START_TICKS = 80;
    private static final int CORE_READY_START_TICKS = 120;
    private static final double CORE_CHANCE = 0.05D;
    private static final double CORE_SIX_EYES_CHANCE = 0.5D;
    private static final int STUN_DURATION_TICKS = 5;
    private static final int STUN_AMPLIFIER = 1;
    private static final int SLOWNESS_DURATION_TICKS = 5;
    private static final int SLOWNESS_AMPLIFIER = 6;

    private ReviveEffectService() {
    }

    public static float getWaitingHealth(ServerPlayer player) {
        return Math.min(1.0F, player.getMaxHealth());
    }

    public static void prepareWaitingEntry(ServerPlayer player, PlayerReviveState reviveState) {
        clearEffectsExceptHealthBoost(player);
        initializeSpecialStage(player, reviveState);
        MahoragaAdaptation.clearJjaWaitingTransientState(player);
        applyWaitingHealth(player);
        startDownedAnimation(player);
        applyWaitingEffects(player);
        clearCurrentTargets(player);
    }

    public static void refreshWaitingTick(ServerPlayer player, PlayerReviveState reviveState) {
        refreshWaitingHealth(player);
        applyWaitingEffects(player);
        reapplyCoreRctIfTriggered(player, reviveState);
        RctRuntimeStateAccess.setManualPressActive(player, false);
    }

    public static void clearWaitingEffects(ServerPlayer player) {
        player.removeEffect(JujutsucraftModMobEffects.STUN.get());
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    public static boolean hasCompletedRecovery(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        if (JjaReviveFirstAidCompat.isRecoveryComplete(player)) {
            return true;
        }
        return RctHealGate.isRctFullyHealed(player);
    }

    public static void clearWaitingScale(ServerPlayer player) {
        if (player == null || !JjaPehkuiCompat.isPehkuiLoaded()) {
            return;
        }
        JjaPehkuiCompat.resetBaseScale(player);
        JjaPehkuiCompat.resetHeightScale(player);
    }

    public static void tickSpecialStage(ServerPlayer player, PlayerReviveState reviveState) {
        ReviveStateTransitions.SpecialStageUpdate update = ReviveStateTransitions.advanceSpecialStage(
            reviveState,
            CORE_GRASPED_START_TICKS,
            CORE_READY_START_TICKS
        );
        if (!update.stageChanged()) {
            return;
        }
        if (update.currentStage() == JjaReviveSpecialStage.ESSENCE_READY) {
            RctAdvancementHelper.award(player, RctAdvancementHelper.RCT_1_ID);
        }
        ReviveSyncService.syncWaitingState(player);
    }

    public static void clearSpecialStage(PlayerReviveState reviveState) {
        ReviveStateTransitions.clearSpecialStage(reviveState);
    }

    private static void clearEffectsExceptHealthBoost(ServerPlayer player) {
        List<MobEffectInstance> preservedEffects = new ArrayList<>();
        for (MobEffect preservedEffect : PERSISTED_WAITING_EFFECTS) {
            MobEffectInstance effectInstance = player.getEffect(preservedEffect);
            if (effectInstance != null) {
                preservedEffects.add(new MobEffectInstance(effectInstance));
            }
        }
        Collection<MobEffectInstance> activeEffects = new ArrayList<>(player.getActiveEffects());
        for (MobEffectInstance activeEffect : activeEffects) {
            player.removeEffect(activeEffect.getEffect());
        }
        for (MobEffectInstance preservedEffect : preservedEffects) {
            player.addEffect(preservedEffect);
        }
    }

    private static void applyWaitingEffects(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.STUN.get(), STUN_DURATION_TICKS, STUN_AMPLIFIER, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION_TICKS, SLOWNESS_AMPLIFIER, false, false, false));
    }

    private static void applyCoreRctEffect(ServerPlayer player) {
        player.addEffect(
            new MobEffectInstance(
                JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get(),
                MobEffectInstance.INFINITE_DURATION,
                0,
                true,
                false,
                false
            )
        );
    }

    private static void reapplyCoreRctIfTriggered(ServerPlayer player, PlayerReviveState reviveState) {
        if (JjaReviveSpecialStage.fromId(reviveState.getReviveSpecialStage()) != JjaReviveSpecialStage.ESSENCE_TRIGGERED) {
            return;
        }
        applyCoreRctEffect(player);
    }

    private static void startDownedAnimation(ServerPlayer player) {
        JjaJujutsucraftDataAccess.jjaSetCurrentSkillValue(player, -1000.0D);
        StunEffectStartedappliedProcedure.execute(player.level(), player.getX(), player.getY(), player.getZ(), player, 0.0D);
    }

    private static void clearCurrentTargets(ServerPlayer target) {
        if (!(target.level() instanceof ServerLevel level)) {
            return;
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof Mob mob && mob.getTarget() == target) {
                mob.setTarget(null);
            }
        }
    }

    private static void applyWaitingHealth(ServerPlayer player) {
        float waitingHealth = getWaitingHealth(player);
        if (JjaReviveFirstAidCompat.applyWaitingHealth(player, waitingHealth)) {
            return;
        }
        player.setHealth(waitingHealth);
    }

    private static void refreshWaitingHealth(ServerPlayer player) {
        float waitingHealth = getWaitingHealth(player);
        if (JjaReviveFirstAidCompat.refreshWaitingHealth(player, waitingHealth)) {
            return;
        }
        if (player.getHealth() <= 0.0F) {
            applyWaitingHealth(player);
        }
    }

    private static void initializeSpecialStage(ServerPlayer player, PlayerReviveState reviveState) {
        clearSpecialStage(reviveState);
        if (shouldStartSpecialStage(player)) {
            reviveState.setReviveSpecialStage(JjaReviveSpecialStage.ELLIPSIS.id());
        }
    }

    private static boolean shouldStartSpecialStage(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return false;
        }
        if (player.getPersistentData().getBoolean("CursedSpirit")) {
            return false;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVariables == null) {
            return false;
        }
        if (playerVariables.PlayerCurseTechnique == -1.0 || playerVariables.PlayerCurseTechnique2 == -1.0) {
            return false;
        }
        if (RctAdvancementHelper.hasAdvancement(player, RctAdvancementHelper.RCT_1_ID)
            || RctAdvancementHelper.hasAdvancement(player, RctAdvancementHelper.RCT_2_ID)) {
            return false;
        }
        double chance = hasSixEyes(player, playerVariables) ? CORE_SIX_EYES_CHANCE : CORE_CHANCE;
        return player.level().random.nextDouble() < chance;
    }

    private static boolean hasSixEyes(ServerPlayer player, JujutsucraftModVariables.PlayerVariables playerVariables) {
        if (playerVariables != null && playerVariables.FlagSixEyes) {
            return true;
        }
        return player.hasEffect(JujutsucraftModMobEffects.SIX_EYES.get());
    }
}
