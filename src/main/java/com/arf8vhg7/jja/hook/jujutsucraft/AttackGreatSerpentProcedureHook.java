package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.megumi.GreatSerpentGrabService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class AttackGreatSerpentProcedureHook {
    private AttackGreatSerpentProcedureHook() {
    }

    public static void tickActiveGrab(Entity serpent) {
        GreatSerpentGrabService.tickActiveGrab(serpent);
    }

    public static boolean tryGrabTarget(Entity serpent, Entity target) {
        return GreatSerpentGrabService.tryGrabTarget(serpent, target);
    }

    public static void tryGrabNearbyTarget(LevelAccessor world, double x, double y, double z, Entity serpent) {
        GreatSerpentGrabService.tryGrabNearbyTarget(world, x, y, z, serpent);
    }

    public static Entity resolveHeldVehicle(Entity candidate, Entity serpent, Entity originalVehicle) {
        return GreatSerpentGrabService.resolveHeldVehicle(candidate, serpent, originalVehicle);
    }

    public static void clearGrabWhenInactive(Entity serpent) {
        GreatSerpentGrabService.clearGrabWhenInactive(serpent);
    }
}
