package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import com.arf8vhg7.jja.feature.jja.technique.shared.selection.UpstreamTechniqueSelectionMetadata;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressed2Procedure;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressed3Procedure;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressed4Procedure;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressed5Procedure;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressedProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;

public final class JjaSkillManagementPageDispatcher {
    private static final UpstreamTechniqueSelectionMetadata TECHNIQUE_SELECTION_METADATA = UpstreamTechniqueSelectionMetadata.get();

    private JjaSkillManagementPageDispatcher() {
    }

    public static boolean usesDirectSelectionWindow(int curseTechniqueId) {
        return TECHNIQUE_SELECTION_METADATA.usesDirectSelectionWindow(curseTechniqueId);
    }

    static int resolvePageNumber(int curseTechniqueId) {
        return TECHNIQUE_SELECTION_METADATA.resolvePageNumber(curseTechniqueId);
    }

    public static void invoke(ServerPlayer player, int curseTechniqueId, int initialSelectTechniqueId) {
        LevelAccessor world = player.level();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        int pageNumber = resolvePageNumber(curseTechniqueId);
        if (pageNumber <= 1) {
            KeyChangeTechniqueOnKeyPressedProcedure.execute(world, x, y, z, player);
            return;
        }
        if (pageNumber == 2) {
            KeyChangeTechniqueOnKeyPressed2Procedure.execute(world, x, y, z, player, curseTechniqueId, initialSelectTechniqueId);
            return;
        }
        if (pageNumber == 3) {
            KeyChangeTechniqueOnKeyPressed3Procedure.execute(world, x, y, z, player, curseTechniqueId, initialSelectTechniqueId);
            return;
        }
        if (pageNumber == 4) {
            KeyChangeTechniqueOnKeyPressed4Procedure.execute(world, x, y, z, player, curseTechniqueId, initialSelectTechniqueId);
            return;
        }
        KeyChangeTechniqueOnKeyPressed5Procedure.execute(world, x, y, z, player, curseTechniqueId, initialSelectTechniqueId);
    }
}
