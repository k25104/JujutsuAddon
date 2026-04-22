package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.jja.rct.network.JjaBrainDestructionHoldStateMessage;
import com.arf8vhg7.jja.network.JjaPacketSenders;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public final class RctBrainService {
    public static final int HOLD_REQUIRED_TICKS = 20;

    private RctBrainService() {
    }

    public static void handleHoldMessage(ServerPlayer player, boolean holding) {
        if (player == null) {
            return;
        }
        if (!JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get()) {
            clearHold(player);
            return;
        }
        if (!holding) {
            clearHoldAndSync(player);
            return;
        }
        if (!RctTechniqueRestrictionRules.canStartBrainDestruction(
            RctMath.isCursedSpirit(player),
            RctAdvancementHelper.hasAdvancementOrSukunaEffect(player, RctAdvancementHelper.MASTERY_RCT_BRAIN_DESTRUCTION_ID),
            true
        )) {
            RctStateService.showNotMastered(player);
            clearHoldAndSync(player);
            return;
        }
        if (!player.hasEffect((MobEffect) JujutsucraftModMobEffects.UNSTABLE.get())) {
            clearHoldAndSync(player);
            return;
        }
        RctStateService.setBrainDestructionHolding(player, true);
        RctStateService.setBrainDestructionTicks(player, 0);
        syncHoldState(player, true);
    }

    public static void tick(ServerPlayer player) {
        if (!JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get()) {
            if (RctStateService.isBrainDestructionHolding(player)) {
                clearHold(player);
            }
            return;
        }
        if (!RctStateService.isBrainDestructionHolding(player)) {
            return;
        }
        if (!player.isAlive() || !player.hasEffect((MobEffect) JujutsucraftModMobEffects.UNSTABLE.get())) {
            clearHoldAndSync(player);
            return;
        }
        int ticks = RctStateService.getBrainDestructionTicks(player) + 1;
        RctStateService.setBrainDestructionTicks(player, ticks);
        if (ticks >= HOLD_REQUIRED_TICKS) {
            clearHoldAndSync(player);
            completeBrainDestruction(player);
        }
    }

    public static boolean applyBrainRegeneration(ServerPlayer player, double rctMultiplier) {
        if (!JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get()) {
            return false;
        }
        if (!RctContextService.canUseBrainRegeneration(player)) {
            return false;
        }
        MobEffectInstance brainDamage = player.getEffect((MobEffect) JujutsucraftModMobEffects.BRAIN_DAMAGE.get());
        int level = brainDamage.getAmplifier() + 1;
        int reducedTicks = (int) Math.floor(40.0 * rctMultiplier / Math.max(level, 1));
        int remainingDuration = brainDamage.getDuration() - reducedTicks;
        player.removeEffect((MobEffect) JujutsucraftModMobEffects.BRAIN_DAMAGE.get());
        if (remainingDuration > 0) {
            player.addEffect(
                new MobEffectInstance(
                    (MobEffect) JujutsucraftModMobEffects.BRAIN_DAMAGE.get(),
                    remainingDuration,
                    brainDamage.getAmplifier(),
                    false,
                    false
                )
            );
        }
        MobEffectInstance fatigue = player.getEffect((MobEffect) JujutsucraftModMobEffects.FATIGUE.get());
        if (fatigue != null) {
            int currentDuration = fatigue.getDuration();
            player.removeEffect((MobEffect) JujutsucraftModMobEffects.FATIGUE.get());
            int nextDuration = RctFatigueConfig.resolveBrainHealingNextDuration(currentDuration);
            RctFatigueHelper.setOrCreateFatigue(player, nextDuration, fatigue.getAmplifier());
        } else {
            RctFatigueHelper.setOrCreateFatigue(player, RctFatigueConfig.getBrainHealingFatigueAmount(), 0);
        }
        return true;
    }

    private static void completeBrainDestruction(ServerPlayer player) {
        MobEffectInstance fatigue = player.getEffect((MobEffect) JujutsucraftModMobEffects.FATIGUE.get());
        int fatigueDuration = fatigue == null ? 0 : fatigue.getDuration();
        player.removeEffect((MobEffect) JujutsucraftModMobEffects.UNSTABLE.get());
        player.addEffect(
            new MobEffectInstance(
                (MobEffect) JujutsucraftModMobEffects.BRAIN_DAMAGE.get(),
                fatigueDuration >= 6000 ? 3600 : 1200,
                fatigueDuration >= 6000 ? 2 : 0,
                false,
                false
            )
        );
    }

    private static void clearHold(ServerPlayer player) {
        RctStateService.setBrainDestructionHolding(player, false);
        RctStateService.setBrainDestructionTicks(player, 0);
    }

    private static void clearHoldAndSync(ServerPlayer player) {
        clearHold(player);
        syncHoldState(player, false);
    }

    private static void syncHoldState(ServerPlayer player, boolean holding) {
        JjaPacketSenders.sendToPlayer(player, new JjaBrainDestructionHoldStateMessage(holding));
    }
}
