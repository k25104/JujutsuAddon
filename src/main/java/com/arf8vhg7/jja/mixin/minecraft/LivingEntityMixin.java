package com.arf8vhg7.jja.mixin.minecraft;

import com.arf8vhg7.jja.hook.minecraft.LivingEntityHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapOperation(
        method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
        ),
        require = 1
    )
    private boolean jja$allowTwinnedBodyEchoCooldownBypass(
        DamageSource damageSource,
        TagKey<DamageType> tag,
        Operation<Boolean> original
    ) {
        if (tag == DamageTypeTags.BYPASSES_COOLDOWN && LivingEntityHook.shouldBypassDamageCooldown((LivingEntity) (Object) this, damageSource)) {
            return true;
        }

        return original.call(damageSource, tag);
    }
}