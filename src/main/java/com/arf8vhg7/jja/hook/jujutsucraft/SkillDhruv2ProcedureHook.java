package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.dhruv.DhruvSummonService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class SkillDhruv2ProcedureHook {
    private SkillDhruv2ProcedureHook() {
    }

    public static boolean handleCustomSummon(LevelAccessor world, Entity entity) {
        DhruvSummonService.handlePterosaurSummon(world, entity);
        return true;
    }
}
