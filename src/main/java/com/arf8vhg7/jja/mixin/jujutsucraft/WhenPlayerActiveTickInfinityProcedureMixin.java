package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.WhenPlayerActiveTickInfinityProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.WhenPlayerActiveTickInfinityProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WhenPlayerActiveTickInfinityProcedure.class, remap = false)
public abstract class WhenPlayerActiveTickInfinityProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$addInfinityEffectFirst(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        return WhenPlayerActiveTickInfinityProcedureHook.addInfinityEffect(livingEntity, effectInstance);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$addInfinityEffectSecond(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        return WhenPlayerActiveTickInfinityProcedureHook.addInfinityEffect(livingEntity, effectInstance);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$ignoreNeutralizationReapplyStop(boolean original) {
        return WhenPlayerActiveTickInfinityProcedureHook.ignoreNeutralizationForInfinity(original);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$ignoreSimpleDomainReapplyStop(boolean original) {
        return WhenPlayerActiveTickInfinityProcedureHook.ignoreSimpleDomainForInfinity(original);
    }
}
