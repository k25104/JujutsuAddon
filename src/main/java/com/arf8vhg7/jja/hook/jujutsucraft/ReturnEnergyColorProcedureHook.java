package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.resource.ce.CEColorService;
import net.minecraft.world.entity.Entity;

public final class ReturnEnergyColorProcedureHook {
    private ReturnEnergyColorProcedureHook() {
    }

    public static Integer resolveOverrideColor(Entity entity) {
        return CEColorService.getOverrideColor(entity);
    }
}
