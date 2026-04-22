package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.CounterProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.CounterProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CounterProcedure.class, remap = false)
@SuppressWarnings("deprecation")
public abstract class CounterProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$skipKusakabePlayerCounterWhenHwbActive(Entity counterEntity, CallbackInfo ci) {
        if (CounterProcedureHook.shouldSkipCounter(counterEntity)) {
            ci.cancel();
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$preserveCounterFbeForQualifiedEntities(
        LivingEntity livingEntity,
        MobEffect mobEffect,
        Operation<Boolean> original
    ) {
        return CounterProcedureHook.shouldRemoveCounterEffect(livingEntity, mobEffect)
            ? original.call(livingEntity, mobEffect)
            : false;
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$preserveCounterSimpleDomainForQualifiedEntities(
        LivingEntity livingEntity,
        MobEffect mobEffect,
        Operation<Boolean> original
    ) {
        return CounterProcedureHook.shouldRemoveCounterEffect(livingEntity, mobEffect)
            ? original.call(livingEntity, mobEffect)
            : false;
    }
}
