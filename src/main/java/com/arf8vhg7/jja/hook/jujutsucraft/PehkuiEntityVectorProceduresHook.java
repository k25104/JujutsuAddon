package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.mobility.scale.PehkuiTargetedMovement;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class PehkuiEntityVectorProceduresHook {
    private PehkuiEntityVectorProceduresHook() {
    }

    public static Vec3 adjustTargetedVelocity(Entity entity, Vec3 original) {
        return PehkuiTargetedMovement.toSolvedEndpointVelocity(entity, original);
    }
}
