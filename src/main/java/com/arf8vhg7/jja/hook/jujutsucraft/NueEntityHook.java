package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.megumi.NueMountedControlService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class NueEntityHook {
    private NueEntityHook() {
    }

    public static void executeAiOrRideControl(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        Operation<Void> original
    ) {
        if (!NueMountedControlService.executeMountedRideControl(world, x, y, z, entity)) {
            original.call(world, x, y, z, entity);
        }
    }

    public static boolean shouldUseDefaultTravel(Entity entity, boolean original) {
        if (!original) {
            return false;
        }
        return !NueMountedControlService.shouldUseDefaultTravelWhileMounted(entity);
    }
}
