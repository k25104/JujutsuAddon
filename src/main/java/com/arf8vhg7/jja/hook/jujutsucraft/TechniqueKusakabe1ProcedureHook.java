package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.sd.KusakabeSimpleDomainAnimationService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import net.mcreator.jujutsucraft.procedures.ReturnEntitySizeProcedure;

public final class TechniqueKusakabe1ProcedureHook {
    private TechniqueKusakabe1ProcedureHook() {
    }

    public static boolean isTrackedSlowness(MobEffectInstance effectInstance) {
        return KusakabeSimpleDomainAnimationService.isTrackedSlowness(effectInstance);
    }

    public static void onTrackedSlownessApplied(Entity entity, MobEffectInstance effectInstance) {
        KusakabeSimpleDomainAnimationService.onTrackedSlownessApplied(entity, effectInstance);
    }

    public static void onTechniqueTickFinished(Entity entity) {
        KusakabeSimpleDomainAnimationService.onTechniqueTickFinished(entity);
    }

    public static boolean removeTargetCaptureSimpleDomain(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        if (effect == JujutsucraftModMobEffects.SIMPLE_DOMAIN.get()) {
            return false;
        }
        return original.call(livingEntity, effect);
    }

    public static double resolveCaptureDistance(Entity entity, double originalCaptureDistance) {
        return resolveCaptureDistance(originalCaptureDistance, ReturnEntitySizeProcedure.execute(entity));
    }

    public static double resolveCaptureDistance(double originalCaptureDistance, double entitySize) {
        return originalCaptureDistance * entitySize;
    }
}
