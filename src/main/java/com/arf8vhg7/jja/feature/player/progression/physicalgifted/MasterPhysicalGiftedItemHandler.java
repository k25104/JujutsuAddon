package com.arf8vhg7.jja.feature.player.progression.physicalgifted;

import com.arf8vhg7.jja.feature.jja.technique.shared.selection.ForceRandomProfessionFlagService;
import net.mcreator.jujutsucraft.procedures.ReturnConfigForceRandomCursedTechniqueProcedure;
import net.mcreator.jujutsucraft.procedures.SelectMakiProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class MasterPhysicalGiftedItemHandler {
    private MasterPhysicalGiftedItemHandler() {
    }

    public static void handle(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        SelectMakiProcedure.execute(world, x, y, z, player);
        if (ReturnConfigForceRandomCursedTechniqueProcedure.execute()) {
            ForceRandomProfessionFlagService.handle(player);
        }
    }
}
