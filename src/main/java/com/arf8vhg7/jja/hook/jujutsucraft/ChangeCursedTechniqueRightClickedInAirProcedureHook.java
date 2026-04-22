package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.selection.ForceRandomCtChangerService;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidResetService;
import net.mcreator.jujutsucraft.procedures.ReturnConfigForceRandomCursedTechniqueProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public final class ChangeCursedTechniqueRightClickedInAirProcedureHook {
    private ChangeCursedTechniqueRightClickedInAirProcedureHook() {
    }

    public static boolean shouldUseCustomForceRandomFlow(LevelAccessor world, Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        return ReturnConfigForceRandomCursedTechniqueProcedure.execute() && !player.getAbilities().instabuild;
    }

    public static boolean handle(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!shouldUseCustomForceRandomFlow(world, entity)) {
            return false;
        }
        if (world instanceof Level level && level.isClientSide()) {
            return true;
        }

        return ForceRandomCtChangerService.handle(entity);
    }

    public static void beginFirstAidReset(LevelAccessor world, Entity entity) {
        if (shouldUseCustomForceRandomFlow(world, entity)) {
            return;
        }
        FirstAidResetService.beginResetTransaction(entity);
    }

    public static void finishFirstAidReset(LevelAccessor world, Entity entity) {
        if (shouldUseCustomForceRandomFlow(world, entity)) {
            return;
        }
        FirstAidResetService.finishResetTransaction(entity);
    }
}
