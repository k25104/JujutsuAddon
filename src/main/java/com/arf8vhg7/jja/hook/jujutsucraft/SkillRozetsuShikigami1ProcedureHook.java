package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.rozetsu.RozetsuSummonService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class SkillRozetsuShikigami1ProcedureHook {
    private SkillRozetsuShikigami1ProcedureHook() {
    }

    public static boolean handleCustomSummon(LevelAccessor world, double x, double y, double z, Entity entity) {
        RozetsuSummonService.handleNormalSummon(world, x, y, z, entity);
        return true;
    }
}
