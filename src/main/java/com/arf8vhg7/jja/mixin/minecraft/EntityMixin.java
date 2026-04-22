package com.arf8vhg7.jja.mixin.minecraft;

import com.arf8vhg7.jja.hook.minecraft.EntityHook;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "isInvulnerableTo(Lnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("HEAD"), cancellable = true, require = 1)
    private void jja$allowTwinnedBodyEchoDamage(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (EntityHook.shouldBypassInvulnerability((Entity) (Object) this, damageSource)) {
            cir.setReturnValue(false);
        }
    }
}