package com.arf8vhg7.jja.feature.jja.resource.ce;

import com.arf8vhg7.jja.feature.jja.technique.family.hakari.HakariJackpotCeRecoveryBoost;
import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.RikaManifestationRecoveryRules;
import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class PlayerTickCursePowerNormalizationService {
    private static final double UPSTREAM_SUKUNA_COST_MULTIPLIER = 0.5D;
    private static final double UPSTREAM_SIX_EYES_COST_MULTIPLIER_BASE = 0.1D;

    private PlayerTickCursePowerNormalizationService() {
    }

    public static double normalize(
        Entity entity,
        JujutsucraftModVariables.PlayerVariables playerVariables,
        double currentCursePowerChangeAfterHealRound,
        double healCursePower
    ) {
        if (playerVariables == null) {
            return currentCursePowerChangeAfterHealRound;
        }
        double rawCursePowerChange = currentCursePowerChangeAfterHealRound - Math.round(healCursePower);
        rawCursePowerChange = reverseLegacyEfficiencyScaling(entity, rawCursePowerChange, CeScalingConfig.isCeEfficiencyScalingEnabled());
        double adjustedHealCursePower = HakariJackpotCeRecoveryBoost.modifyHealCursePower(entity, healCursePower);
        adjustedHealCursePower = RikaManifestationRecoveryRules.modifyHealCursePower(entity, adjustedHealCursePower);
        if (rawCursePowerChange > 0.0D) {
            return rawCursePowerChange + normalizeHealCursePower(entity, playerVariables, adjustedHealCursePower);
        }
        if (ReviveFlowService.isWaiting(entity)) {
            return CePowerPreservation.normalizeCursePowerChange(entity, playerVariables, rawCursePowerChange, 0.0);
        }
        return CePowerPreservation.normalizeCursePowerChange(entity, playerVariables, rawCursePowerChange, adjustedHealCursePower);
    }

    static double reverseLegacyEfficiencyScaling(Entity entity, double rawCursePowerChange, boolean ceEfficiencyScalingEnabled) {
        if (!ceEfficiencyScalingEnabled || rawCursePowerChange >= 0.0D || !(entity instanceof LivingEntity livingEntity)) {
            return rawCursePowerChange;
        }

        double restoredCursePowerChange = rawCursePowerChange;
        if (livingEntity.hasEffect(JujutsucraftModMobEffects.SIX_EYES.get())) {
            int amplifier = Math.max(livingEntity.getEffect(JujutsucraftModMobEffects.SIX_EYES.get()).getAmplifier(), 0);
            restoredCursePowerChange /= resolveUpstreamSixEyesCostMultiplier(amplifier);
        }
        if (livingEntity.hasEffect(JujutsucraftModMobEffects.SUKUNA_EFFECT.get())) {
            restoredCursePowerChange /= UPSTREAM_SUKUNA_COST_MULTIPLIER;
        }
        return restoredCursePowerChange;
    }

    static double reverseLegacyEfficiencyScaling(
        double rawCursePowerChange,
        boolean hasSukunaEffect,
        boolean hasSixEyes,
        int sixEyesAmplifier,
        boolean ceEfficiencyScalingEnabled
    ) {
        if (!ceEfficiencyScalingEnabled || rawCursePowerChange >= 0.0D) {
            return rawCursePowerChange;
        }

        double restoredCursePowerChange = rawCursePowerChange;
        if (hasSixEyes) {
            restoredCursePowerChange /= resolveUpstreamSixEyesCostMultiplier(sixEyesAmplifier);
        }
        if (hasSukunaEffect) {
            restoredCursePowerChange /= UPSTREAM_SUKUNA_COST_MULTIPLIER;
        }
        return restoredCursePowerChange;
    }

    private static double resolveUpstreamSixEyesCostMultiplier(int sixEyesAmplifier) {
        return UPSTREAM_SIX_EYES_COST_MULTIPLIER_BASE / (Math.max(sixEyesAmplifier, 0) + 1.0D);
    }

    private static double normalizeHealCursePower(
        Entity entity,
        JujutsucraftModVariables.PlayerVariables playerVariables,
        double healCursePower
    ) {
        if (ReviveFlowService.isWaiting(entity)) {
            return CePowerPreservation.normalizeCursePowerChange(entity, playerVariables, 0.0, 0.0);
        }
        return CePowerPreservation.normalizeCursePowerChange(entity, playerVariables, 0.0, healCursePower);
    }
}
