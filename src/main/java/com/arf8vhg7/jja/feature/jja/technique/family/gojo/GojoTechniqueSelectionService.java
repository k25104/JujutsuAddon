package com.arf8vhg7.jja.feature.jja.technique.family.gojo;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionCandidate;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionSupport;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionWindow;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class GojoTechniqueSelectionService {
    public static final int CURSE_TECHNIQUE_ID = 2;
    public static final int TELEPORT_SELECT = 16;
    public static final int TELEPORT_SKILL = CURSE_TECHNIQUE_ID * 100 + TELEPORT_SELECT;
    public static final String TELEPORT_NAME_KEY = "jujutsu.technique.jja_gojo_teleport";
    private static final String EMPTY_SELECTION_NAME = TechniqueSelectionCandidate.none(0.0D).name();
    private static final int MAX_SELECTION_STEPS = 25;

    private GojoTechniqueSelectionService() {
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
        boolean hasTeleportUnlock = GojoProgressionService.hasTeleportUnlock(entity);
        return TechniqueSelectionSupport.tryHandlePageSelection(
            world,
            x,
            y,
            z,
            entity,
            playerCt,
            select,
            MAX_SELECTION_STEPS,
            candidateSelect -> resolveSelection(candidateSelect, hasTeleportUnlock)
        );
    }

    public static TechniqueSelectionCandidate resolvePageOneSupplement(@Nullable Entity entity, double playerCt, double playerSelect, String currentName) {
        return resolvePageOneSupplement(playerCt, playerSelect, currentName, GojoProgressionService.hasTeleportUnlock(entity));
    }

    public static boolean isTeleportSelection(int curseTechniqueId, int selectTechniqueId) {
        return curseTechniqueId == CURSE_TECHNIQUE_ID && selectTechniqueId == TELEPORT_SELECT;
    }

    static double resolveInitialSelect(double currentSelect, boolean noChangeTechnique, boolean reverse) {
        if (noChangeTechnique) {
            return currentSelect;
        }
        return TechniqueSelectionWindow.advance(currentSelect, reverse);
    }

    static TechniqueSelectionCandidate resolvePageOneSupplement(
        double playerCt,
        double playerSelect,
        String currentName,
        boolean hasTeleportUnlock
    ) {
        if ((int) Math.round(playerCt) != CURSE_TECHNIQUE_ID
            || (int) Math.round(playerSelect) != TELEPORT_SELECT
            || !EMPTY_SELECTION_NAME.equals(currentName)
            || !hasTeleportUnlock) {
            return TechniqueSelectionCandidate.none(playerSelect);
        }
        return new TechniqueSelectionCandidate(playerSelect, TELEPORT_NAME_KEY, true, false, 0.0D);
    }

    static TechniqueSelectionCandidate resolveSelection(double select, boolean hasTeleportUnlock) {
        TechniqueSelectionCandidate sharedCandidate = TechniqueSelectionSupport.resolveSharedCombatCandidate(select);
        if (sharedCandidate != null) {
            return sharedCandidate;
        }

        return switch ((int) Math.round(select)) {
            case 5 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.infinity", true, false, 0.0D);
            case 6 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.blue", false, false, 200.0D);
            case 7 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.red", false, false, 500.0D);
            case 8 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.gojo1", false, false, 200.0D);
            case 15 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.purple", false, false, 1000.0D);
            case TELEPORT_SELECT -> hasTeleportUnlock
                ? new TechniqueSelectionCandidate(select, TELEPORT_NAME_KEY, true, false, 0.0D)
                : TechniqueSelectionCandidate.none(select);
            case 20 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.unlimited_void", false, false, 1250.0D);
            default -> TechniqueSelectionCandidate.none(select);
        };
    }
}
