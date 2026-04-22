package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.TechniqueKusakabe1ProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.TechniqueKusakabe1Procedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(value = TechniqueKusakabe1Procedure.class, remap = false)
public abstract class TechniqueKusakabe1ProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$trackKusakabeSimpleDomainSlowness(
        LivingEntity livingEntity,
        MobEffectInstance effectInstance,
        Operation<Boolean> original
    ) {
        boolean applied = original.call(livingEntity, effectInstance);
        if (applied && TechniqueKusakabe1ProcedureHook.isTrackedSlowness(effectInstance)) {
            TechniqueKusakabe1ProcedureHook.onTrackedSlownessApplied(livingEntity, effectInstance);
        }
        return applied;
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 2
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$keepSimpleDomainOnTargetCapture(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        return TechniqueKusakabe1ProcedureHook.removeTargetCaptureSimpleDomain(livingEntity, effect, original);
    }

    @Expression("2.5")
    @ModifyExpressionValue(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0), require = 1)
    private static double jja$scaleKusakabeCaptureDistancePhase1(double original, @Local(argsOnly = true) Entity entity) {
        return TechniqueKusakabe1ProcedureHook.resolveCaptureDistance(entity, original);
    }

    @Expression("2.5")
    @ModifyExpressionValue(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1), require = 1)
    private static double jja$scaleKusakabeCaptureDistancePhase2(double original, @Local(argsOnly = true) Entity entity) {
        return TechniqueKusakabe1ProcedureHook.resolveCaptureDistance(entity, original);
    }

    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$resetKusakabeSimpleDomainAnimation(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        TechniqueKusakabe1ProcedureHook.onTechniqueTickFinished(entity);
    }
}
