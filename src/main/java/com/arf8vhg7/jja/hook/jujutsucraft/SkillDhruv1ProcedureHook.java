package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.dhruv.DhruvSummonService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class SkillDhruv1ProcedureHook {
    private SkillDhruv1ProcedureHook() {
    }

    public static boolean handleCustomSummon(LevelAccessor world, Entity entity) {
        DhruvSummonService.handleMouseSummon(world, entity);
        return true;
    }
}
