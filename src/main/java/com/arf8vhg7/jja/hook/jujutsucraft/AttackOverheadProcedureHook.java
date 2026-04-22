package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.mobility.scale.PehkuiTargetedMovement;
import com.arf8vhg7.jja.feature.combat.zone.ZoneChargeScalingService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class AttackOverheadProcedureHook {
    private AttackOverheadProcedureHook() {
    }

    public static double clampChargeStep(Entity entity, double original) {
        return ZoneChargeScalingService.clampCnt6ChargeStep(entity, original);
    }

    public static Vec3 adjustTargetedVelocity(Entity entity, Vec3 original) {
        return PehkuiTargetedMovement.toSolvedEndpointVelocity(entity, original);
    }
}
