package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.gojo.GojoProgressionService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class CursedTechniqueGojoProcedureHook {
    private CursedTechniqueGojoProcedureHook() {
    }

    public static boolean handleCustomSkill(LevelAccessor world, double x, double y, double z, Entity entity) {
        return GojoProgressionService.tryHandleTeleportTechnique(world, x, y, z, entity);
    }
}
