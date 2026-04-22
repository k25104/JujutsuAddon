package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.resource.ce.CEParticleContextService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class SkillBoogieWoogieProcedureHook {
    private SkillBoogieWoogieProcedureHook() {
    }

    public static void enterCeParticleContext(Entity entity) {
        CEParticleContextService.enter(entity);
    }

    public static void exitCeParticleContext() {
        CEParticleContextService.exit();
    }

    public static float resolveSwapTeleportYaw(ServerPlayer teleportedPlayer, Entity caster, float originalYaw) {
        if (teleportedPlayer != caster) {
            return originalYaw;
        }
        return originalYaw + 180.0F;
    }

    public static float resolveSwapTeleportPitch(ServerPlayer teleportedPlayer, Entity caster, float originalPitch) {
        if (teleportedPlayer != caster) {
            return originalPitch;
        }
        return -originalPitch;
    }
}
