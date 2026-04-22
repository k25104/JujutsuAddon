package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionImmersivePortalsService;
import com.arf8vhg7.jja.feature.jja.domain.de.OpenBarrierMasteryReservationService;
import com.arf8vhg7.jja.feature.jja.domain.de.DomainClashDamagePenalty;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import com.arf8vhg7.jja.feature.jja.domain.sd.SimpleDomainExpireGuard;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.nbt.CompoundTag;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public final class DomainExpansionOnEffectActiveTickProcedureHook {
    private DomainExpansionOnEffectActiveTickProcedureHook() {
    }

    public static MobEffectInstance getEffect(LivingEntity livingEntity, MobEffect effect) {
        return DomainExpansionHookSupport.getClashDurationEffect(livingEntity, effect);
    }

    public static boolean addEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return DomainExpansionHookSupport.addNormalizedDurationEffect(livingEntity, effectInstance);
    }

    public static double disableDurationDecay(double left, double right) {
        return DomainExpansionHookSupport.disableDurationDecay(left, right);
    }

    public static double normalizeSimpleDomainDurationDecayRoot(double original) {
        return original / 10.0D;
    }

    public static double resolveSimpleDomainTickInterval(double original) {
        return 1.0D;
    }

    public static void onActiveTick(Entity entity) {
        OpenBarrierMasteryReservationService.observeNearbyOpenBarrier(entity);
    }

    public static boolean removeSimpleDomainDuringDomainRewrite(LivingEntity livingEntity, MobEffect effect, Operation<Boolean> original) {
        return removeDomainEffectDuringAllowedSpace(livingEntity, effect, original);
    }

    public static void tickCounter(Entity entity) {
        DomainExpansionHookSupport.tickCounter(entity);
    }

    public static double removeClashDamagePenalty(double originalPenaltyFactor) {
        return DomainClashDamagePenalty.remove(originalPenaltyFactor);
    }

    public static double resolveAccumulatedTotalDamage(Entity entity, CompoundTag tag, String key, double originalValue) {
        if (!"totalDamage".equals(key) || !(entity instanceof LivingEntity livingEntity)) {
            return originalValue;
        }

        float currentHealth = FirstAidHealthAccess.getEffectiveHealth(livingEntity);
        return DomainClashDamagePenalty.resolveAccumulatedTotalDamage(
            tag,
            originalValue,
            currentHealth,
            livingEntity.getMaxHealth(),
            entity instanceof Player
        );
    }

    public static double modifyDrainInterval(double original) {
        return 1.0;
    }

    public static double modifyCursePowerDrain(double original) {
        return 1.0;
    }

    public static float getEffectiveHealth(Entity entity, float original) {
        return entity instanceof LivingEntity livingEntity ? FirstAidHealthAccess.getEffectiveHealth(livingEntity) : original;
    }

    public static boolean shouldSuppressDomainExpansionRemoval(Entity entity) {
        return DomainExpansionHookSupport.suppressOpenBarrierFailureAndApplyClashDamage(entity);
    }

    public static double resolveCurrentRadius(Entity entity, double radius) {
        return DomainExpansionHookSupport.resolveMovableRadius(entity, radius);
    }

    public static double resolveFeetAnchorDistanceSquared(Entity owner, Entity target, double originalDistance) {
        return DomainExpansionHookSupport.resolveFeetAnchorDistanceSquared(owner, target, originalDistance);
    }

    public static Vec3 resolveImmersivePortalsBattleBuildCenter(Entity entity, double x, double y, double z) {
        return DomainExpansionImmersivePortalsService.resolveTransferredPocketBuildCenter(entity, x, y, z);
    }

    public static void runImmersivePortalsBattleBuild(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        Operation<Void> original
    ) {
        DomainExpansionImmersivePortalsService.ensureSynchronizedBarrierSession(world, entity);
        DomainExpansionImmersivePortalsService.BattleBuildState state =
            DomainExpansionImmersivePortalsService.resolveTransferredPocketBattleBuildState(entity, x, y, z);
        DomainExpansionImmersivePortalsService.applyTransferredPocketBattleBuildState(entity, state);
        DomainExpansionImmersivePortalsService.runSynchronizedSecondaryBarrierBuild(world, entity);
        original.call(world, state.center().x, state.center().y, state.center().z, entity);
    }

    public static boolean removeDomainEffectDuringAllowedSpace(LivingEntity livingEntity, MobEffect effect, Operation<Boolean> original) {
        if (effect == JujutsucraftModMobEffects.DOMAIN_EXPANSION.get() && shouldSuppressDomainExpansionRemoval(livingEntity)) {
            return false;
        }

        return SimpleDomainExpireGuard.withGuard(livingEntity, () -> original.call(livingEntity, effect));
    }

}
