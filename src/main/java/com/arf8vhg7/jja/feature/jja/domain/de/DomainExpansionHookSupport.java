package com.arf8vhg7.jja.feature.jja.domain.de;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class DomainExpansionHookSupport {
    private DomainExpansionHookSupport() {
    }

    public static MobEffectInstance getCountDurationEffect(LivingEntity livingEntity, MobEffect effect) {
        return DECounter.getEffectWithCountDuration(livingEntity, effect);
    }

    public static MobEffectInstance getClashDurationEffect(LivingEntity livingEntity, MobEffect effect) {
        return DECounter.getEffectWithClashDuration(livingEntity, effect);
    }

    public static MobEffectInstance getThresholdDurationEffect(LivingEntity livingEntity, MobEffect effect) {
        return DECounter.getEffectWithThresholdDuration(livingEntity, effect);
    }

    public static boolean addNormalizedDurationEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return DECounter.addEffectWithNormalizedDuration(livingEntity, effectInstance);
    }

    public static double disableDurationDecay(double left, double right) {
        return DECounter.disableDurationDecay(left, right);
    }

    public static void tickCounter(Entity entity) {
        DECounter.tick(entity);
    }

    public static void resetCounter(Entity entity) {
        DECounter.reset(entity);
    }

    public static void clearCounter(Entity entity) {
        DECounter.clear(entity);
    }

    public static double getCounter(Entity entity) {
        return DECounter.get(entity);
    }

    public static MobEffectInstance normalizeDuration(MobEffectInstance effectInstance) {
        return DECounter.normalizeDuration(effectInstance);
    }

    public static boolean addEffectUnlessDomainExtension(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return DECounter.addEffectUnlessDomainExtension(livingEntity, effectInstance);
    }

    public static double adjustOpenBarrierRange(LivingEntity livingEntity, double radius, double openMultiplierInCode) {
        return DomainExpansionRadiusRuntime.resolveActiveRange(livingEntity, radius, openMultiplierInCode);
    }

    public static double resolveActiveRangeForState(
        boolean openBarrierActive,
        double currentRadius,
        double fallbackRadius,
        double openMultiplierInCode
    ) {
        return DomainExpansionRadiusRuntime.resolveActiveRangeForState(
            openBarrierActive,
            currentRadius,
            fallbackRadius,
            openMultiplierInCode
        );
    }

    public static double resolveConfiguredRadius(LevelAccessor world) {
        return world == null ? DomainExpansionConfiguredRadiusSync.getConfiguredRadius() : DomainExpansionConfiguredRadiusSync.getConfiguredRadius();
    }

    public static double resolveBaseRadius(Entity entity, double radius) {
        return DomainExpansionRadiusRuntime.resolveBaseRadius(entity, radius);
    }

    public static double resolveCurrentRadius(Entity entity, double radius) {
        return DomainExpansionRadiusRuntime.resolveCurrentRadius(entity, radius);
    }

    public static double resolveMovableRadius(Entity entity, double radius) {
        return DomainExpansionRadiusRuntime.resolveMovableRadius(entity, radius);
    }

    public static double resolveInitialCurrentRadius(Entity entity, double radius) {
        return DomainExpansionRadiusRuntime.resolveInitialCurrentRadius(entity, radius);
    }

    public static boolean isAntiOpenBarrierEligible(Entity entity) {
        return DomainExpansionRadiusRuntime.isAntiOpenBarrierEligible(entity);
    }

    public static boolean applyOpenBarrierClashDamage(Entity openOwner, Entity closedOwner) {
        return DomainExpansionRadiusRuntime.applyOpenBarrierClashDamage(openOwner, closedOwner);
    }

    public static boolean shouldSuppressOpenBarrierRepaint(Entity entity) {
        return DomainExpansionRadiusRuntime.shouldSuppressOpenBarrierRepaint(entity);
    }

    public static boolean suppressOpenBarrierFailureAndApplyClashDamage(Entity entity) {
        return DomainExpansionRadiusRuntime.suppressOpenBarrierFailureAndApplyClashDamage(entity);
    }

    public static void clearRadiusRuntime(Entity entity) {
        DomainExpansionRadiusRuntime.clear(entity);
    }

    public static boolean isWithinActiveDomainSpace(Entity entity) {
        if (entity == null) {
            return false;
        }

        double currentRadius = DomainExpansionRadiusRuntime.resolveCurrentRadius(entity, DomainExpansionConfiguredRadiusSync.getConfiguredRadius());
        if (currentRadius <= 0.0D) {
            return false;
        }

        return DomainExpansionContainmentHelper.isWithinOwnerRadius(
            entity,
            entity,
            DomainExpansionRadiusRuntime.resolveBarrierEnvelopeRadius(currentRadius)
        );
    }

    public static double resolveFeetAnchorDistanceSquared(Entity owner, Entity target, double originalDistance) {
        double distance = DomainExpansionContainmentHelper.distanceToOwnerCenterSqr(owner, target);
        return Double.isFinite(distance) ? distance : originalDistance;
    }

    public static boolean isClosedDomainActive(Entity entity) {
        return DomainExpansionRadiusRuntime.isClosedDomainActive(entity);
    }
}
