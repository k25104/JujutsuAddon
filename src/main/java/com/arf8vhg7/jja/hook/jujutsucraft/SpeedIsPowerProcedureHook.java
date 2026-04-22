package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.naoya.NaoyaProjectionSorceryService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public final class SpeedIsPowerProcedureHook {
    private SpeedIsPowerProcedureHook() {
    }

    public static boolean shouldApplyServerMovement(LevelAccessor world) {
        return NaoyaProjectionSorceryService.shouldApplyTopSpeedMovement(world);
    }

    public static Vec3 resolveServerMovementVelocity(LevelAccessor world, Vec3 originalVelocity) {
        return NaoyaProjectionSorceryService.resolveTopSpeedVelocity(world, originalVelocity);
    }

    public static double resolveTopSpeedPunchChargeCounter(Entity entity, String key, double nextCounterValue) {
        return NaoyaProjectionSorceryService.resolveTopSpeedPunchChargeCounter(entity, key, nextCounterValue);
    }
}
