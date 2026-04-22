package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.naoya.NaoyaProjectionSorceryService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class AIProjectionSorceryProcedureHook {
    private AIProjectionSorceryProcedureHook() {
    }

    public static void accelerateProjectionFrames(LevelAccessor world, Entity entity) {
        NaoyaProjectionSorceryService.accelerateProjectionFrames(world, entity);
    }
}
