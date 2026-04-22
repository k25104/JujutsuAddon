package com.arf8vhg7.jja.feature.player.revive;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.compat.firstaid.FirstAidCompatRuntime;
import com.arf8vhg7.jja.compat.firstaid.FirstAidDamageModelCompat;
import com.arf8vhg7.jja.compat.firstaid.FirstAidHealthCompat;
import com.arf8vhg7.jja.compat.firstaid.FirstAidReviveCompat;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD)
public final class JjaReviveFirstAidCompat {
    private JjaReviveFirstAidCompat() {
    }

    enum WaitingHealthSyncMode {
        NONE,
        TRACKED_ONLY,
        FULL_REAPPLY
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(JjaReviveFirstAidCompat::installCompatIfNeeded);
    }

    public static void installCompatIfNeeded() {
        if (!FirstAidCompatRuntime.isFirstAidLoaded()) {
            return;
        }
        FirstAidReviveCompat.installReviveCompat(Bridge.INSTANCE);
    }

    public static boolean applyWaitingHealth(@Nullable ServerPlayer player, float waitingHealth) {
        if (!usesFirstAid(player)) {
            return false;
        }
        installCompatIfNeeded();
        FirstAidReviveCompat.setBeingRevived(player, true);
        setWaitingHealthFloor(player, waitingHealth, true);
        return true;
    }

    public static boolean isRecoveryComplete(@Nullable ServerPlayer player) {
        return player != null && FirstAidHealthCompat.isEffectivelyAtFullHealth(player);
    }

    public static float getEffectiveHealth(@Nullable ServerPlayer player) {
        return player == null ? 0.0F : FirstAidHealthCompat.getEffectiveHealth(player);
    }

    public static boolean refreshWaitingHealth(@Nullable ServerPlayer player, float waitingHealth) {
        if (!usesFirstAid(player)) {
            return false;
        }
        installCompatIfNeeded();
        FirstAidReviveCompat.setBeingRevived(player, true);
        switch (resolveWaitingHealthSyncMode(player, waitingHealth)) {
            case FULL_REAPPLY -> setWaitingHealthFloor(player, waitingHealth, true);
            case TRACKED_ONLY -> FirstAidDamageModelCompat.setTrackedHealthDirect(player, waitingHealth);
            case NONE -> {
            }
        }
        return true;
    }

    private static void setWaitingHealthFloor(@Nullable ServerPlayer player, float waitingHealth, boolean syncTrackedHealth) {
        if (player == null) {
            return;
        }
        float maxHealth = Math.max(player.getMaxHealth(), 1.0F);
        FirstAidDamageModelCompat.setUniformHealthRatio(player, waitingHealth / maxHealth);
        if (syncTrackedHealth) {
            FirstAidDamageModelCompat.setTrackedHealthDirect(player, waitingHealth);
        }
        if (player.getHealth() < waitingHealth) {
            player.setHealth(waitingHealth);
        }
    }

    public static void onReviveComplete(@Nullable ServerPlayer player) {
        if (!usesFirstAid(player)) {
            return;
        }
        FirstAidReviveCompat.setBeingRevived(player, false);
        FirstAidReviveCompat.reviveDamageModel(player);
        FirstAidHealthCompat.syncVanillaHealth(player);
    }

    public static void onForceDeath(@Nullable ServerPlayer player) {
        if (!usesFirstAid(player)) {
            return;
        }
        FirstAidReviveCompat.setBeingRevived(player, false);
    }

    public static void reapplyWaitingState(@Nullable ServerPlayer player) {
        if (player == null || !ReviveFlowService.isWaiting(player)) {
            return;
        }
        refreshWaitingHealth(player, ReviveFlowService.getWaitingHealth(player));
    }

    private static boolean usesFirstAid(@Nullable ServerPlayer player) {
        return player != null && !player.level().isClientSide() && FirstAidCompatRuntime.isFirstAidLoaded();
    }

    static WaitingHealthSyncMode resolveWaitingHealthSyncMode(boolean damageModelFloorSatisfied, boolean trackedHealthFloorSatisfied) {
        if (!damageModelFloorSatisfied) {
            return WaitingHealthSyncMode.FULL_REAPPLY;
        }
        if (!trackedHealthFloorSatisfied) {
            return WaitingHealthSyncMode.TRACKED_ONLY;
        }
        return WaitingHealthSyncMode.NONE;
    }

    private static WaitingHealthSyncMode resolveWaitingHealthSyncMode(@Nullable ServerPlayer player, float waitingHealth) {
        if (player == null) {
            return WaitingHealthSyncMode.NONE;
        }
        float maxHealth = Math.max(player.getMaxHealth(), 1.0F);
        boolean damageModelFloorSatisfied = FirstAidDamageModelCompat.areAllPartHealthRatiosAtLeast(player, waitingHealth / maxHealth);
        boolean trackedHealthFloorSatisfied = player.getHealth() >= waitingHealth;
        return resolveWaitingHealthSyncMode(damageModelFloorSatisfied, trackedHealthFloorSatisfied);
    }

    private enum Bridge implements FirstAidReviveCompat.FallbackKnockoutBridge {
        INSTANCE;

        @Override
        public boolean tryKnockOut(Player player, @Nullable Object source) {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return false;
            }
            boolean shouldDeferToTotemProtection = ReviveTotemProtectionService.shouldDeferToTotemProtection(serverPlayer, source);
            if (!ReviveTotemProtectionService.shouldStartWaiting(
                ReviveFlowService.canEnterWaiting(serverPlayer),
                shouldDeferToTotemProtection
            )) {
                return false;
            }
            return ReviveFlowService.enterWaiting(serverPlayer);
        }

        @Override
        public boolean isWaiting(Player player) {
            return ReviveFlowService.isWaiting(player);
        }
    }
}
