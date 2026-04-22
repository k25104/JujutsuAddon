package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.geto.GetoCursedSpiritAttractionService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class TechniqueBluePunchProcedureHook {
    private TechniqueBluePunchProcedureHook() {
    }

    public static boolean resolveAttackResult(
        LevelAccessor world,
        Entity entity,
        Entity entityiterator,
        Operation<Boolean> original
    ) {
        if (GetoCursedSpiritAttractionService.isGetoBluePunchContext(entity)) {
            return GetoCursedSpiritAttractionService.resolveBluePunchAttackResult(entity, entityiterator);
        }
        return original.call(world, entity, entityiterator);
    }

    public static boolean addEffect(
        Entity entity,
        LivingEntity livingEntity,
        MobEffectInstance effectInstance,
        Operation<Boolean> original
    ) {
        return switch (GetoCursedSpiritAttractionService.resolveBluePunchEffectAction(entity, livingEntity, effectInstance)) {
            case KEEP_ORIGINAL -> original.call(livingEntity, effectInstance);
            case REPLACE_WITH_ATTACK_CHARGE_SLOWNESS -> original.call(
                livingEntity,
                GetoCursedSpiritAttractionService.createAttackChargeSlowness()
            );
            case SUPPRESS -> false;
        };
    }

    public static void applyVector(
        Entity entity,
        Entity entityiterator,
        double x,
        double y,
        double z,
        Operation<Void> original
    ) {
        switch (GetoCursedSpiritAttractionService.resolveBluePunchTargetAction(entity, entityiterator)) {
            case CAPTURE -> GetoCursedSpiritAttractionService.captureBluePunchTarget(entity, entityiterator);
            case SKIP_VECTOR -> {
            }
            case APPLY_VECTOR -> original.call(entityiterator, x, y, z);
        }
    }

    public static boolean shouldPlayFrameSetSound(Entity entity) {
        return GetoCursedSpiritAttractionService.shouldPlayBluePunchFrameSetSound(entity);
    }

    public static boolean shouldRunGrab(Entity entity) {
        return GetoCursedSpiritAttractionService.shouldRunBluePunchGrab(entity);
    }

    public static boolean isHoldLimitReached(Entity entity, boolean original) {
        return GetoCursedSpiritAttractionService.isBluePunchHoldLimitReached(entity, original);
    }
}
