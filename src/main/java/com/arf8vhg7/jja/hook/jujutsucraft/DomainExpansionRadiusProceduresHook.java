package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import net.minecraft.world.entity.Entity;

public final class DomainExpansionRadiusProceduresHook {
    private DomainExpansionRadiusProceduresHook() {
    }

    public static double resolveCurrentRadius(Entity entity, double radius) {
        return DomainExpansionHookSupport.resolveMovableRadius(entity, radius);
    }
}
