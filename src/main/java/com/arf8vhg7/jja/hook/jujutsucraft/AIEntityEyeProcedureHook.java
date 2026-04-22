package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.ranta.RantaEvilEyeBoundTargetService;
import com.arf8vhg7.jja.feature.jja.technique.family.ranta.RantaEvilEyeUpkeepService;
import com.arf8vhg7.jja.feature.jja.resource.ce.CEParticleContextService;
import net.minecraft.world.entity.Entity;

public final class AIEntityEyeProcedureHook {
    private AIEntityEyeProcedureHook() {
    }

    public static void enterCeParticleContext(Entity entity) {
        CEParticleContextService.enter(entity);
    }

    public static void tickUpkeep(Entity entity) {
        RantaEvilEyeUpkeepService.tick(entity);
    }

    public static void exitCeParticleContext() {
        CEParticleContextService.exit();
    }

    public static void runBoundTargetDamage(Entity entity, Runnable damageAction) {
        RantaEvilEyeBoundTargetService.runBoundTargetDamage(entity, damageAction);
    }
}
