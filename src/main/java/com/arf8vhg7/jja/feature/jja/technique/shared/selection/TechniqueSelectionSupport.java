package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import java.util.function.DoubleFunction;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.ChangeTechniqueTestProcedure;
import net.mcreator.jujutsucraft.procedures.TechniqueDecideProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class TechniqueSelectionSupport {
    private TechniqueSelectionSupport() {
    }

    @Nullable
    public static TechniqueSelectionCandidate resolveSharedCombatCandidate(double select) {
        int selectionId = (int) Math.round(select);
        if (selectionId >= 0 && selectionId <= 2) {
            return TechniqueSelectionCandidate.attack(select, selectionId);
        }
        if (selectionId == 21) {
            return TechniqueSelectionCandidate.cancelDomain(select);
        }
        return null;
    }

    public static boolean tryHandlePageSelection(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double initialSelect,
        int maxSelectionSteps,
        DoubleFunction<TechniqueSelectionCandidate> resolver
    ) {
        if (entity == null) {
            return false;
        }

        TechniqueSelectionCandidate selected = cycleSelection(world, x, y, z, entity, playerCt, initialSelect, maxSelectionSteps, resolver);
        TechniqueDecideProcedure.execute(
            entity,
            selected.passive(),
            selected.physical(),
            selected.cost(),
            playerCt,
            selected.select(),
            selected.name()
        );
        return true;
    }

    public static TechniqueSelectionCandidate cycleSelection(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double initialSelect,
        int maxSelectionSteps,
        DoubleFunction<TechniqueSelectionCandidate> resolver
    ) {
        return TechniqueSelectionCycle.resolve(
            initialSelect,
            entity.isShiftKeyDown(),
            maxSelectionSteps,
            resolver,
            TechniqueSelectionCandidate::isEmpty,
            candidate -> !TechniqueSelectionProbeEvaluator.evaluateCandidate(
                world,
                x,
                y,
                z,
                entity,
                playerCt,
                candidate.select(),
                candidate.name(),
                () -> ChangeTechniqueTestProcedure.execute(world, x, y, z, entity, playerCt, candidate.select())
            ),
            TechniqueSelectionCandidate::none
        );
    }
}
