package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AISLOTProcedureHook;
import net.mcreator.jujutsucraft.procedures.AISLOTProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(value = AISLOTProcedure.class, remap = false)
public abstract class AISLOTProcedureMixin {
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 1800), remap = false, require = 1)
    private static int jja$modifyJackpotDuration(int originalDuration) {
        return AISLOTProcedureHook.modifyJackpotDuration(originalDuration);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$normalizeDomainDuration(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        return AISLOTProcedureHook.addEffect(livingEntity, effectInstance);
    }
}
