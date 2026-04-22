package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import java.util.function.BooleanSupplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class TechniqueSelectionProbeEvaluator {
    private TechniqueSelectionProbeEvaluator() {
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
        int curseTechniqueId = (int) Math.round(playerCt);
        int selectTechniqueId = (int) Math.round(playerSelect);
        JjaSkillManagementProbeContext.setCurrentCandidate(curseTechniqueId, selectTechniqueId, name);
        try {
            boolean skipped = original.getAsBoolean();
            JjaSkillManagementProbeContext.captureProbeCandidate(curseTechniqueId, selectTechniqueId, name, skipped);
            return skipped;
        } finally {
            JjaSkillManagementProbeContext.clearCurrentCandidate();
        }
    }
}
