package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.resource.ce.CEParticleContextService;
import net.minecraft.world.entity.Entity;

public final class BulletSkeletonBulletHitsBlockProcedureHook {
    private BulletSkeletonBulletHitsBlockProcedureHook() {
    }

    public static void enterCeParticleContext(Entity owner, Entity projectile) {
        CEParticleContextService.enter(projectile, owner);
    }

    public static void exitCeParticleContext() {
        CEParticleContextService.exit();
    }
}
