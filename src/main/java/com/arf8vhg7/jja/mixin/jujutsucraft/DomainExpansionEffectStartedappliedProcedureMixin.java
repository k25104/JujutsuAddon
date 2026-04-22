package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DomainExpansionEffectStartedappliedProcedureHook;
import net.mcreator.jujutsucraft.procedures.DomainExpansionEffectStartedappliedProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DomainExpansionEffectStartedappliedProcedure.class, remap = false)
public abstract class DomainExpansionEffectStartedappliedProcedureMixin {
    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$resetCounter(Entity entity, CallbackInfo callbackInfo) {
        DomainExpansionEffectStartedappliedProcedureHook.resetCounter(entity);
    }
}
