package com.arf8vhg7.jja.feature.jja.technique.family.geto;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionCandidate;
import com.arf8vhg7.jja.feature.jja.technique.shared.selection.TechniqueSelectionSupport;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public final class GetoTechniqueSelectionService {
    public static final int CURSE_TECHNIQUE_ID = 18;
    public static final int CURSED_SPIRIT_ATTRACTION_SELECT = 14;
    public static final int CURSED_SPIRIT_ATTRACTION_SKILL = 1814;
    public static final String CURSED_SPIRIT_ATTRACTION_NAME_KEY = "jujutsu.technique.jja_geto_cursed_spirit_attraction";

    private static final int MAX_SELECTION_STEPS = 52;
    private static final ResourceLocation CURSED_SPIRIT_MANIPULATION_DIMENSION = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "cursed_spirit_manipulation_dimension"
    );

    private GetoTechniqueSelectionService() {
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
            select -> resolveSelection(entity, select)
        );
    }

    static TechniqueSelectionCandidate resolveSelection(Entity entity, double select) {
        TechniqueSelectionCandidate sharedCandidate = TechniqueSelectionSupport.resolveSharedCombatCandidate(select);
        if (sharedCandidate != null) {
            return sharedCandidate;
        }

        boolean canUseManipulationControls = !isInManipulationDimension(entity);
        boolean creative = entity instanceof Player player && player.getAbilities().instabuild;
        int selectionId = (int) Math.round(select);
        if (selectionId == 20) {
            return new TechniqueSelectionCandidate(select, "entity.jujutsucraft.entity_womb_profusion", false, false, 1250.0D);
        }
        if (selectionId >= 17 && selectionId <= 19) {
            return creative
                ? new TechniqueSelectionCandidate(
                    select,
                    "jujutsu.technique.geto_set" + (selectionId - 16),
                    false,
                    false,
                    1500.0D + 250.0D * (selectionId - 17)
                )
                : TechniqueSelectionCandidate.none(select);
        }
        if (!canUseManipulationControls) {
            return TechniqueSelectionCandidate.none(select);
        }

        return switch (selectionId) {
            case 10 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.cancel", true, true, 0.0D);
            case 11, 12, 13 -> resolveStoredSpiritSelection(entity, select);
            case CURSED_SPIRIT_ATTRACTION_SELECT -> new TechniqueSelectionCandidate(
                select,
                CURSED_SPIRIT_ATTRACTION_NAME_KEY,
                true,
                false,
                0.0D
            );
            case 15 -> new TechniqueSelectionCandidate(select, "jujutsu.technique.uzumaki", false, false, 300.0D);
            default -> TechniqueSelectionCandidate.none(select);
        };
    }

    static TechniqueSelectionCandidate resolveStoredSpiritSelection(Entity entity, double select) {
        String currentSelectionName = JjaJujutsucraftCompat.jjaGetPlayerVariablesOrDefault(entity).PlayerSelectCurseTechniqueName;
        double targetIndex = -1.0D;
        double cursor = 0.0D;
        int selectionId = (int) Math.round(select);

        for (int index = 0; index < 10000; index++) {
            cursor += 1.0D;
            String dataKey = "data_cursed_spirit_manipulation" + Math.round(cursor);
            if (entity.getPersistentData().getDouble(dataKey) == 0.0D) {
                if (selectionId == 11) {
                    targetIndex = 1.0D;
                } else if (selectionId == 13) {
                    targetIndex = Math.round(cursor - 1.0D);
                }
            } else if (currentSelectionName.contains(entity.getPersistentData().getString(dataKey + "_name") + " ×")) {
                if (selectionId == 11) {
                    targetIndex = Math.round(cursor - 1.0D);
                } else if (selectionId == 13) {
                    targetIndex = Math.round(cursor + 1.0D);
                } else {
                    targetIndex = Math.round(cursor);
                }
            }

            if (targetIndex == -1.0D) {
                continue;
            }

            String targetKey = "data_cursed_spirit_manipulation" + Math.round(targetIndex);
            String name = entity.getPersistentData().getString(targetKey + "_name");
            if (name.isEmpty()) {
                return TechniqueSelectionCandidate.none(select);
            }
            double count = entity.getPersistentData().getDouble(targetKey + "_num");
            return new TechniqueSelectionCandidate(12.0D, name + " ×" + Math.round(count), true, false, 0.0D);
        }

        return TechniqueSelectionCandidate.none(select);
    }

    private static boolean isInManipulationDimension(Entity entity) {
        return entity != null && CURSED_SPIRIT_MANIPULATION_DIMENSION.equals(entity.level().dimension().location());
    }
}
