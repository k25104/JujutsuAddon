package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.KeySimpleDomainOnKeyReleasedProcedureHook;
import net.mcreator.jujutsucraft.procedures.KeySimpleDomainOnKeyReleasedProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeySimpleDomainOnKeyReleasedProcedure.class, remap = false)
public abstract class KeySimpleDomainOnKeyReleasedProcedureMixin {
    @Inject(method = "execute(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$handleKeyRelease(Entity entity, CallbackInfo ci) {
        KeySimpleDomainOnKeyReleasedProcedureHook.onKeyReleased(entity);
    }
}
