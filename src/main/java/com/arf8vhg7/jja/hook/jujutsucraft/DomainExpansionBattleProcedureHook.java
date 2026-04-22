package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainBarrierSpeedScaling;
import net.minecraft.world.entity.Entity;

public final class DomainExpansionBattleProcedureHook {
    private DomainExpansionBattleProcedureHook() {
    }

    public static double scaleBarrierSpeedWithStrength(Entity entity, double originalSpeed) {
        return DomainBarrierSpeedScaling.scale(entity, originalSpeed);
    }

    public static double resolveCurrentRadius(Entity entity, double radius) {
        return com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport.resolveCurrentRadius(entity, radius);
    }

}
