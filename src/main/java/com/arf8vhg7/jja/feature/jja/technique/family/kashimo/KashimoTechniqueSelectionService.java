package com.arf8vhg7.jja.feature.jja.technique.family.kashimo;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionCandidate;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionSupport;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionWindow;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class KashimoTechniqueSelectionService {
    public static final int CURSE_TECHNIQUE_ID = 7;
    public static final int NYOI_STAFF_RECALL_SELECT = 18;
    public static final int NYOI_STAFF_RECALL_SKILL = 718;
    public static final String NYOI_STAFF_RECALL_NAME_KEY = "jujutsu.technique.jja_kashimo_nyoi_staff_recall";

    private static final int MAX_SELECTION_STEPS = 25;

    private KashimoTechniqueSelectionService() {
    }

    public static boolean tryHandlePageSelection(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) {
            return false;
        }

        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(entity);
        if (playerVariables == null) {
            return false;
        }

        double playerCt = playerVariables.SecondTechnique ? playerVariables.PlayerCurseTechnique2 : playerVariables.PlayerCurseTechnique;
        if ((int) Math.round(playerCt) != CURSE_TECHNIQUE_ID) {
            return false;
        }

        double select = resolveInitialSelect(playerVariables.PlayerSelectCurseTechnique, playerVariables.noChangeTechnique, entity.isShiftKeyDown());
        return TechniqueSelectionSupport.tryHandlePageSelection(
            world,
            x,
            y,
            z,
            entity,
            playerCt,
            select,
            MAX_SELECTION_STEPS,
            KashimoTechniqueSelectionService::resolveSelection
        );
    }

    static double resolveInitialSelect(double currentSelect, boolean noChangeTechnique, boolean reverse) {
        if (noChangeTechnique) {
            return currentSelect;
        }
        return TechniqueSelectionWindow.advance(currentSelect, reverse);
    }

    static TechniqueSelectionCandidate resolveSelection(double select) {
        TechniqueSelectionCandidate sharedCandidate = TechniqueSelectionSupport.resolveSharedCombatCandidate(select);
        if (sharedCandidate != null) {
            return sharedCandidate;
        }

        return switch ((int) Math.round(select)) {
            case 3 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.kick", false, true, 50.0D);
            case 4 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.attack4", false, true, 50.0D);
            case 5 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.kashimo1", true, true, 200.0D);
            case 10 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.kashimo2", true, true, 100.0D);
            case 15 -> new TechniqueSelectionCandidate(select, "effect.mythical_beast_amber_effect", true, false, 0.0D);
            case 16 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.kashimo_ah", false, false, 100.0D);
            case 17 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.kashimo_energy_wave", false, false, 250.0D);
            case NYOI_STAFF_RECALL_SELECT -> new TechniqueSelectionCandidate(select, NYOI_STAFF_RECALL_NAME_KEY, true, true, 0.0D);
            case 19 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.flying_kick", false, true, 400.0D);
            case 20 -> new TechniqueSelectionCandidate(select, "effect.domain_expansion", false, false, 1250.0D);
            default -> TechniqueSelectionCandidate.none(select);
        };
    }

    public static TechniqueSelectionCandidate resolvePageOneSupplement(double playerCt, double playerSelect, String currentName) {
        if ((int) Math.round(playerCt) != CURSE_TECHNIQUE_ID
            || (int) Math.round(playerSelect) != NYOI_STAFF_RECALL_SELECT
            || !TechniqueSelectionCandidate.none(playerSelect).name().equals(currentName)) {
            return TechniqueSelectionCandidate.none(playerSelect);
        }
        return new TechniqueSelectionCandidate(playerSelect, NYOI_STAFF_RECALL_NAME_KEY, true, true, 0.0D);
    }
}
