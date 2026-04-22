package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.rozetsu.RozetsuSummonService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class SkillRozetsuShikigami2ProcedureHook {
    private SkillRozetsuShikigami2ProcedureHook() {
    }

    public static boolean handleCustomSummon(LevelAccessor world, double x, double y, double z, Entity entity) {
        RozetsuSummonService.handleVesselSummon(world, x, y, z, entity);
        return true;
    }
}
