package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import net.minecraft.world.entity.Entity;

public final class WhenRespawnProcedureHook {
    private WhenRespawnProcedureHook() {
    }

    public static void applyScaledPlayerCursePowerFormer(Entity entity) {
        JjaCursePowerAccountingService.refreshPlayerCursePowerFormer(entity);
    }
}
