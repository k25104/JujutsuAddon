package com.arf8vhg7.jja.mixin.minecraft;

import com.arf8vhg7.jja.hook.minecraft.ServerLevelHook;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @ModifyVariable(
        method = "sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I",
        at = @At("HEAD"),
        argsOnly = true,
        index = 1,
        require = 1
    )
    private ParticleOptions jja$remapCeParticle(ParticleOptions particle) {
        return ServerLevelHook.remapCeParticle(particle);
    }

    @ModifyVariable(
        method = "sendParticles(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/particles/ParticleOptions;ZDDDIDDDD)Z",
        at = @At("HEAD"),
        argsOnly = true,
        index = 2,
        require = 1
    )
    private ParticleOptions jja$remapCeParticleForPlayer(ParticleOptions particle) {
        return ServerLevelHook.remapCeParticle(particle);
    }
}
