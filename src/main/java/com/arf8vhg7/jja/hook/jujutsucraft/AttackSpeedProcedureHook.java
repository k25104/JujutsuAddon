package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.mobility.scale.PehkuiTargetedMovement;
import com.arf8vhg7.jja.feature.combat.zone.ZoneChargeScalingService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class AttackSpeedProcedureHook {
    private AttackSpeedProcedureHook() {
    }

    public static boolean isFullChargeOverflow(Entity entity, boolean original) {
        return ZoneChargeScalingService.isCnt6FullChargeOverflow(entity, original);
    }

    public static boolean isFullChargeReached(Entity entity, boolean original) {
        return ZoneChargeScalingService.isCnt6FullChargeReached(entity, original);
    }

    public static Vec3 adjustTargetedVelocity(Entity entity, Vec3 original) {
        return PehkuiTargetedMovement.toSolvedEndpointVelocity(entity, original);
    }
}
