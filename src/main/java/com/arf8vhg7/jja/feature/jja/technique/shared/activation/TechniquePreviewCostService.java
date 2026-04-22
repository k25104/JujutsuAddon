package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import com.arf8vhg7.jja.feature.combat.zone.ZoneChargeScalingService;
import com.arf8vhg7.jja.feature.jja.resource.ce.CeScalingConfig;
import com.arf8vhg7.jja.feature.jja.resource.ce.CursePowerScalingRules;
import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.OkkotsuCopiedTechniqueRules;
import net.mcreator.jujutsucraft.init.JujutsucraftModGameRules;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class TechniquePreviewCostService {
    private static final double STAR_RAGE_BASE_COST_INCREASE = 10.0D;
    private static final double STAR_RAGE_AMPLIFIER_COST_MULTIPLIER = 9.0D;

    private TechniquePreviewCostService() {
    }

    public static double resolveDisplayedCost(ServerPlayer player, JujutsucraftModVariables.PlayerVariables playerVariables, double baseCost) {
        double resolvedCost = resolveTechniqueCost(player, playerVariables, baseCost);
        return ZoneChargeScalingService.scaleResolvedTechniqueCost(player, resolvedCost);
    }

    public static double resolveTechniqueCost(ServerPlayer player, JujutsucraftModVariables.PlayerVariables playerVariables, double baseCost) {
        LivingEntity livingEntity = player;
        MobEffectInstance starRage = livingEntity.getEffect(JujutsucraftModMobEffects.STAR_RAGE.get());
        MobEffectInstance sixEyes = livingEntity.getEffect(JujutsucraftModMobEffects.SIX_EYES.get());
        boolean appliesStarRage = starRage != null
            && playerVariables.PhysicalAttack
            && (!livingEntity.hasEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get()) || player.getPersistentData().getBoolean("Failed"));
        boolean hasSukunaEffect = livingEntity.hasEffect(JujutsucraftModMobEffects.SUKUNA_EFFECT.get());
        boolean hasSixEyes = sixEyes != null;
        int sixEyesAmplifier = hasSixEyes ? sixEyes.getAmplifier() : -1;
        int jujutsuUpgradeDifficulty = player.level().getGameRules().getInt(JujutsucraftModGameRules.JUJUTSUUPGRADEDIFFICULTY);

        return resolveTechniqueCost(
            baseCost,
            appliesStarRage,
            starRage != null ? starRage.getAmplifier() : -1,
            hasSukunaEffect,
            hasSixEyes,
            sixEyesAmplifier,
            playerVariables.PlayerTechniqueUsedNumber,
            jujutsuUpgradeDifficulty,
            isExplicitlyFreeTechnique(livingEntity),
            CeScalingConfig.isCeEfficiencyScalingEnabled()
        );
    }

    public static double resolveTechniqueCost(
        double baseCost,
        boolean appliesStarRage,
        int starRageAmplifier,
        boolean hasSukunaEffect,
        boolean hasSixEyes,
        int sixEyesAmplifier,
        double playerTechniqueUsedNumber,
        int jujutsuUpgradeDifficulty,
        boolean isExplicitlyFreeTechnique
    ) {
        return resolveTechniqueCost(
            baseCost,
            appliesStarRage,
            starRageAmplifier,
            hasSukunaEffect,
            hasSixEyes,
            sixEyesAmplifier,
            playerTechniqueUsedNumber,
            jujutsuUpgradeDifficulty,
            isExplicitlyFreeTechnique,
            CeScalingConfig.isCeEfficiencyScalingEnabled()
        );
    }

    public static double resolveTechniqueCost(
        double baseCost,
        boolean appliesStarRage,
        int starRageAmplifier,
        boolean hasSukunaEffect,
        boolean hasSixEyes,
        int sixEyesAmplifier,
        double playerTechniqueUsedNumber,
        int jujutsuUpgradeDifficulty,
        boolean isExplicitlyFreeTechnique,
        boolean ceEfficiencyScalingEnabled
    ) {
        if (isExplicitlyFreeTechnique) {
            return 0.0D;
        }

        double resolvedCost = baseCost;
        if (appliesStarRage) {
            resolvedCost = Math.round(
                resolvedCost + STAR_RAGE_BASE_COST_INCREASE + STAR_RAGE_AMPLIFIER_COST_MULTIPLIER * (starRageAmplifier + 1.0D)
            );
        }

        double efficiencyMultiplier = CursePowerScalingRules.resolveTechniqueEfficiencyMultiplier(
            hasSixEyes,
            sixEyesAmplifier,
            hasSukunaEffect,
            playerTechniqueUsedNumber,
            jujutsuUpgradeDifficulty,
            ceEfficiencyScalingEnabled
        );
        return Math.round(resolvedCost * efficiencyMultiplier);
    }

    public static boolean isExplicitlyFreeTechnique(LivingEntity livingEntity) {
        return isExplicitlyFreeTechnique(
            OkkotsuCopiedTechniqueRules.isUnusedLoudspeaker(livingEntity.getMainHandItem()),
            OkkotsuCopiedTechniqueRules.isUnusedLoudspeaker(livingEntity.getOffhandItem())
        );
    }

    public static boolean isExplicitlyFreeTechnique(boolean mainHandHasUnusedLoudspeaker, boolean offhandHasUnusedLoudspeaker) {
        return mainHandHasUnusedLoudspeaker || offhandHasUnusedLoudspeaker;
    }
}
