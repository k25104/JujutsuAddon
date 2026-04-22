package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSync;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;

public final class RctAutoService {
    private RctAutoService() {
    }

    public static void tick(ServerPlayer player) {
        if (player == null) {
            return;
        }
        if (!JjaCommonConfig.AUTO_RCT_ENABLED.get()) {
            stopAutoRct(player, true);
            return;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        if (rctState == null) {
            return;
        }
        if (RctContextService.isReviveWaiting(player)) {
            stopAutoRct(player, true);
            return;
        }
        if (rctState.isAutoRctEnabled()) {
            RctRuntimeStateAccess.setManualPressActive(player, false);
        }
        if (!rctState.isAutoRctEnabled()) {
            if (RctStateService.isAutoRctRunning(player)) {
                stopAutoRct(player, true);
            }
            return;
        }
        boolean autoUnlocked = RctAdvancementHelper.hasAdvancement(player, RctAdvancementHelper.MASTERY_RCT_AUTO_ID);
        boolean hasCurseEnergy = RctContextService.hasCurseEnergy(player);
        if (RctChannelTransitionResolver.shouldDisableAutoRct(autoUnlocked, hasCurseEnergy)) {
            rctState.setAutoRctEnabled(false);
            stopAutoRct(player, true);
            JjaPlayerStateSync.sync(player);
            return;
        }
        boolean autoRunning = RctStateService.isAutoRctRunning(player);
        boolean hasRctEffect = player.hasEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get());
        if (RctContextService.isSelfHealComplete(player)) {
            if (!RctContextService.canKeepRctChannelWithoutEffect(player)) {
                if (autoRunning) {
                    stopAutoRct(player, true);
                } else if (hasRctEffect) {
                    RctRuntimeStateAccess.setManualPressActive(player, false);
                    player.removeEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get());
                }
            }
            return;
        }
        if (RctChannelTransitionResolver.shouldStopAutoRctWithoutEffect(autoRunning, hasRctEffect, RctContextService.canKeepRctChannelWithoutEffect(player))) {
            stopAutoRct(player, false);
            return;
        }
        if (RctChannelTransitionResolver.shouldStartAutoRct(hasRctEffect, canStartNow(player))) {
            startAutoRct(player);
        }
    }

    public static void handleActiveTickFailure(ServerPlayer player) {
        if (!RctStateService.isAutoRctRunning(player)) {
            return;
        }
        if (!JjaCommonConfig.AUTO_RCT_ENABLED.get()) {
            stopAutoRct(player, true);
            return;
        }
        if (RctContextService.hasCurseEnergy(player)) {
            stopAutoRct(player, true);
            return;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        if (rctState != null) {
            rctState.setAutoRctEnabled(false);
            JjaPlayerStateSync.sync(player);
        }
        stopAutoRct(player, true);
    }

    public static void stopAutoRct(ServerPlayer player, boolean removeEffect) {
        boolean wasRunning = RctStateService.isAutoRctRunning(player);
        RctStateService.setAutoRctRunning(player, false);
        RctRuntimeStateAccess.setManualPressActive(player, false);
        if (removeEffect && wasRunning) {
            player.removeEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get());
        }
    }

    private static boolean canStartNow(ServerPlayer player) {
        if (RctContextService.isReviveWaiting(player)) {
            return false;
        }
        if (player.hasEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get())) {
            return false;
        }
        if (RctContextService.isSelfHealComplete(player)) {
            return false;
        }
        return resolveStartLevel(player) >= 0.0;
    }

    private static void startAutoRct(ServerPlayer player) {
        double level = resolveStartLevel(player);
        if (level < 0.0) {
            return;
        }
        int amplifier = RctMath.isCursedSpirit(player) ? (int) -Math.round(level) : (int) Math.round(level);
        player.addEffect(
            new MobEffectInstance(
                (MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get(),
                Integer.MAX_VALUE,
                amplifier,
                true,
                false
            )
        );
        RctStateService.rememberRctEffectLevel(player, amplifier);
        RctStateService.setAutoRctRunning(player, true);
        RctRuntimeStateAccess.setManualPressActive(player, false);
    }

    private static double resolveStartLevel(ServerPlayer player) {
        if (RctMath.isCursedSpirit(player)) {
            return applyZoneBonus(player, 1.0);
        }
        JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (variables == null || !JjaCursePowerAccountingService.hasFormerPowerAtLeast(variables, 150.0D) || variables.PlayerCursePower < 10.0) {
            return -1.0;
        }
        double level;
        if (RctAdvancementHelper.hasAdvancement(player, RctAdvancementHelper.RCT_2_ID)
            || player.hasEffect((MobEffect) JujutsucraftModMobEffects.SUKUNA_EFFECT.get())) {
            level = 1.0;
        } else if (RctAdvancementHelper.hasAdvancement(player, RctAdvancementHelper.RCT_1_ID)) {
            level = 0.0;
        } else {
            level = -1.0;
        }
        return applyZoneBonus(player, level);
    }

    private static double applyZoneBonus(ServerPlayer player, double baseLevel) {
        if (baseLevel < 0.0 || !player.hasEffect((MobEffect) JujutsucraftModMobEffects.ZONE.get())) {
            return baseLevel;
        }
        return baseLevel + 1.0 + player.getEffect((MobEffect) JujutsucraftModMobEffects.ZONE.get()).getAmplifier();
    }
}
