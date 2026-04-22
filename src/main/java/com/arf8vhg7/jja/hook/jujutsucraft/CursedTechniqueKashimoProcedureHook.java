package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.kashimo.KashimoNyoiStaffRecallService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class CursedTechniqueKashimoProcedureHook {
    private CursedTechniqueKashimoProcedureHook() {
    }

    public static boolean handleCustomSkill(LevelAccessor world, double x, double y, double z, Entity entity) {
        return KashimoNyoiStaffRecallService.tryHandle(world, x, y, z, entity);
    }
}
