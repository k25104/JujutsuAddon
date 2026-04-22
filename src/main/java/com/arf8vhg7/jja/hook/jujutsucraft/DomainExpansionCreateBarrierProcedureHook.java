package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionImmersivePortalsService;
import com.arf8vhg7.jja.feature.jja.domain.de.OpenBarrierMasteryReservationService;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupService;
import com.arf8vhg7.jja.feature.player.mobility.fly.ObservedDoubleJumpUnlockService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class DomainExpansionCreateBarrierProcedureHook {
    private DomainExpansionCreateBarrierProcedureHook() {
    }

    public static boolean addNormalizedDomainExpansionEffect(
        LivingEntity livingEntity,
        MobEffectInstance effectInstance,
        Operation<Boolean> original
    ) {
        return original.call(livingEntity, DomainExpansionHookSupport.normalizeDuration(effectInstance));
    }

    public static boolean shouldApplyNearbySlowness(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return false;
    }

    public static boolean forceOpenBarrierSelectCheck(boolean original) {
        return true;
    }

    public static boolean resolveOpenBarrierCrouchCheck(Entity entity, boolean original) {
        if (!(entity instanceof net.minecraft.world.entity.player.Player)) {
            return original;
        }
        return !TechniqueSetupService.shouldUseOpenBarrier(entity);
    }

    public static void onBarrierCreateStart(Entity entity) {
        ObservedDoubleJumpUnlockService.observeHigurumaBarrierStart(entity);
    }

    public static double resolveReservedOpenBarrierRoll(double original, Entity entity) {
        return OpenBarrierMasteryReservationService.resolveReservedRandomRoll(original, entity);
    }

    public static double resolveInitialCurrentRadius(Entity entity, double radius) {
        return DomainExpansionHookSupport.resolveInitialCurrentRadius(entity, radius);
    }

    public static void runImmersivePortalsBarrierBuild(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        Operation<Void> original
    ) {
        DomainExpansionImmersivePortalsService.ensureSynchronizedBarrierSession(world, entity);
        DomainExpansionImmersivePortalsService.runSynchronizedSecondaryBarrierBuild(world, entity);
        original.call(world, x, y, z, entity);
    }
}
