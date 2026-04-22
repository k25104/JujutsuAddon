package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.resource.ce.CEParticleContextService;
import net.minecraft.world.entity.Entity;

public final class EntityReceiptOnEntityTickUpdateProcedureHook {
    private EntityReceiptOnEntityTickUpdateProcedureHook() {
    }

    public static void enterCeParticleContext(Entity entity) {
        CEParticleContextService.enter(entity);
    }

    public static void exitCeParticleContext() {
        CEParticleContextService.exit();
    }
}
