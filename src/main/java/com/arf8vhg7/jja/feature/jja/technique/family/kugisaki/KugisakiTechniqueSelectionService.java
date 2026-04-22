package com.arf8vhg7.jja.feature.jja.technique.family.kugisaki;

import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionCandidate;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionSupport;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class KugisakiTechniqueSelectionService {
    public static final int CURSE_TECHNIQUE_ID = 34;
    public static final int NAIL_REFILL_SELECT = 10;
    public static final int NAIL_REFILL_SKILL = 3410;
    public static final String NAIL_REFILL_NAME_KEY = "jujutsu.technique.jja_kugisaki_nail_refill";

    private static final int MAX_SELECTION_STEPS = 25;

    private KugisakiTechniqueSelectionService() {
    }

    public static boolean tryHandlePageSelection(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double playerSelect
    ) {
        if (entity == null || (int) Math.round(playerCt) != CURSE_TECHNIQUE_ID) {
            return false;
        }

        return TechniqueSelectionSupport.tryHandlePageSelection(
            world,
            x,
            y,
            z,
            entity,
            playerCt,
            playerSelect,
            MAX_SELECTION_STEPS,
            KugisakiTechniqueSelectionService::resolveSelection
        );
    }

    static TechniqueSelectionCandidate resolveSelection(double select) {
        TechniqueSelectionCandidate sharedCandidate = TechniqueSelectionSupport.resolveSharedCombatCandidate(select);
        if (sharedCandidate != null) {
            return sharedCandidate;
        }
        int selectionId = (int) Math.round(select);
        return switch (selectionId) {
            case 6 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.kugisaki2", false, false, 100.0D);
            case NAIL_REFILL_SELECT -> new TechniqueSelectionCandidate(select, NAIL_REFILL_NAME_KEY, true, true, 0.0D);
            case 20 -> new TechniqueSelectionCandidate(select, "effect.domain_expansion", false, false, 1000.0D);
            default -> TechniqueSelectionCandidate.none(select);
        };
    }
}
