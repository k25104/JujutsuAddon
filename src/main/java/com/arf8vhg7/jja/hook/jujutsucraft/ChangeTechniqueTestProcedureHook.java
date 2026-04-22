package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.selection.JjaSkillManagementProbeContext;
import com.arf8vhg7.jja.feature.jja.technique.shared.registration.JjaSkillManagementService;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniqueSelectionEffectRules;
import com.arf8vhg7.jja.feature.jja.technique.family.gojo.GojoTechniqueSelectionService;
import com.arf8vhg7.jja.feature.jja.rct.RctStateService;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.JjaAddonTechniqueSelectionCatalog;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionProbeEvaluator;
import java.util.function.BooleanSupplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class ChangeTechniqueTestProcedureHook {
    private ChangeTechniqueTestProcedureHook() {
    }

    public static boolean evaluateCandidate(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double playerSelect,
        String name,
        BooleanSupplier original
    ) {
        return TechniqueSelectionProbeEvaluator.evaluateCandidate(world, x, y, z, entity, playerCt, playerSelect, name, original);
    }

    public static boolean applySelectionRules(Entity entity, double playerCt, double playerSelect, boolean originalSkip) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return originalSkip;
        }

        int curseTechniqueId = (int) Math.round(playerCt);
        int selectTechniqueId = (int) Math.round(playerSelect);
        boolean upstreamSkip = resolveUpstreamSkipAfterJjaOverrides(curseTechniqueId, selectTechniqueId, originalSkip);
        boolean hasJackpot = RctStateService.hasJackpot(livingEntity);
        boolean hasDomainExpansion = TechniqueTrueSphereProcedureHook.canUseTrueSphere(livingEntity);
        boolean selectionSkip = TechniqueSelectionEffectRules.resolveSelectionSkip(
            curseTechniqueId,
            selectTechniqueId,
            upstreamSkip,
            hasJackpot,
            hasDomainExpansion
        );

        if (selectionSkip || JjaSkillManagementProbeContext.isIgnoringHiddenProbe()) {
            return selectionSkip;
        }

        JjaSkillManagementProbeContext.CandidateContext candidate = JjaSkillManagementProbeContext.getCurrentCandidate();
        if (candidate == null) {
            return selectionSkip;
        }
        return selectionSkip || JjaSkillManagementService.isHiddenCandidate(entity, candidate.curseTechniqueId(), candidate.canonicalName());
    }

    static boolean resolveUpstreamSkipAfterJjaOverrides(int curseTechniqueId, int selectTechniqueId, boolean originalSkip) {
        if (
            GojoTechniqueSelectionService.isTeleportSelection(curseTechniqueId, selectTechniqueId)
                || !JjaAddonTechniqueSelectionCatalog.resolveCandidate(curseTechniqueId, selectTechniqueId).isEmpty()
        ) {
            return false;
        }
        return originalSkip;
    }
}
