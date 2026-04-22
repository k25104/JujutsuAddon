package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniquePreviewCostService;
import com.arf8vhg7.jja.feature.jja.technique.shared.summon.SummonEnhancementService;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.JjaSkillManagementProbeContext;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class TechniqueDecideProcedureHook {
    private TechniqueDecideProcedureHook() {
    }

    public static boolean shouldCancel() {
        return JjaSkillManagementProbeContext.isProbeActive();
    }

    public static void onTechniqueDecide(Entity entity, double cost, double playerCt, double playerSelect, String name) {
        double adjustedCost = applyPreviewCostAdjustments(entity, cost);
        SummonEnhancementService.onTechniqueDecide(entity, adjustedCost, playerCt, playerSelect, name);
    }

    static double resolveTechniqueCost(
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
            true
        );
    }

    static double resolveTechniqueCost(
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
        return TechniquePreviewCostService.resolveTechniqueCost(
            baseCost,
            appliesStarRage,
            starRageAmplifier,
            hasSukunaEffect,
            hasSixEyes,
            sixEyesAmplifier,
            playerTechniqueUsedNumber,
            jujutsuUpgradeDifficulty,
            isExplicitlyFreeTechnique,
            ceEfficiencyScalingEnabled
        );
    }

    private static double applyPreviewCostAdjustments(Entity entity, double cost) {
        if (!(entity instanceof ServerPlayer player)) {
            return cost;
        }

        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVariables == null) {
            return cost;
        }

        double adjustedCost = TechniquePreviewCostService.resolveDisplayedCost(player, playerVariables, cost);
        if (playerVariables.PlayerSelectCurseTechniqueCost != adjustedCost) {
            playerVariables.PlayerSelectCurseTechniqueCost = adjustedCost;
            playerVariables.syncPlayerVariables(player);
        }
        return adjustedCost;
    }

    static boolean isExplicitlyFreeTechnique(boolean mainHandHasUnusedLoudspeaker, boolean offhandHasUnusedLoudspeaker) {
        return TechniquePreviewCostService.isExplicitlyFreeTechnique(mainHandHasUnusedLoudspeaker, offhandHasUnusedLoudspeaker);
    }
}
