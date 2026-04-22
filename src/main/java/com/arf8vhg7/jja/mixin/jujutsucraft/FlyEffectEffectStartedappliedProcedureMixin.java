package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.FlyEffectEffectStartedappliedProcedureHook;
import net.mcreator.jujutsucraft.procedures.FlyEffectEffectStartedappliedProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FlyEffectEffectStartedappliedProcedure.class, remap = false)
public abstract class FlyEffectEffectStartedappliedProcedureMixin {
    @Inject(method = "execute(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$skipImmediateMayflyGrant(Entity entity, CallbackInfo ci) {
        if (FlyEffectEffectStartedappliedProcedureHook.shouldCancel(entity)) {
            ci.cancel();
        }
    }
}
