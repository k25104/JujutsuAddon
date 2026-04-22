package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.mobility.scale.PehkuiTargetedMovement;
import net.minecraft.world.entity.Entity;

public final class AttackJumpProcedureHook {
    private AttackJumpProcedureHook() {
    }

    public static double scaleTargetingStep(Entity entity, double originalDistance) {
        return PehkuiTargetedMovement.scaleMaxDistance(entity, originalDistance);
    }
}
