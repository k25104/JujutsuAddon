package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SimpleDomainEffectExpiresProcedureHook;
import net.mcreator.jujutsucraft.procedures.SimpleDomainEffectExpiresProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleDomainEffectExpiresProcedure.class, remap = false)
public abstract class SimpleDomainEffectExpiresProcedureMixin {
    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$clearAntiDomainPresentation(Entity entity, double amplifier, CallbackInfo ci) {
        SimpleDomainEffectExpiresProcedureHook.onExpire(entity);
    }
}
