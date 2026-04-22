package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.geto.GetoCursedSpiritAttractionService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class CursedTechniqueGetoProcedureHook {
    private CursedTechniqueGetoProcedureHook() {
    }

    public static boolean handleCustomSkill(LevelAccessor world, double x, double y, double z, Entity entity) {
        return GetoCursedSpiritAttractionService.tryHandle(world, x, y, z, entity);
    }
}
