package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.state.AddonStatCounter;
import com.arf8vhg7.jja.feature.player.state.AddonStatsAccess;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueService;
import com.arf8vhg7.jja.feature.jja.domain.sd.SimpleDomainHoldService;
import com.arf8vhg7.jja.feature.jja.domain.sd.SimpleDomainWitnessService;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.entity.Entity;

public final class SimpleDomainOnEffectActiveTickProcedureHook {
    private SimpleDomainOnEffectActiveTickProcedureHook() {
    }

    public static void onActiveTick(Entity entity) {
        if (!AntiDomainTechniqueService.hasOwnedSimpleDomain(entity)) {
            return;
        }
        AddonStatsAccess.incrementCounter(entity, AddonStatCounter.SIMPLE_DOMAIN_USED);
        SimpleDomainWitnessService.witness(entity);
    }

    public static boolean shouldPlayHollowWickerBasketParticle(Entity entity, boolean original) {
        return AntiDomainTechniqueService.shouldUseActiveHwbVisual(entity, original);
    }

    public static boolean shouldSpawnRingParticle(Entity entity) {
        return shouldSpawnRingParticle(AntiDomainTechniqueService.shouldSuppressActivePlayerEffects(entity));
    }

    public static List<Entity> resolveNearbySimpleDomainTargets(Entity entity, List<Entity> original) {
        return resolveNearbySimpleDomainTargets(AntiDomainTechniqueService.shouldSuppressActivePlayerEffects(entity), original);
    }

    public static void extendSimpleDomainHold(Entity entity) {
        SimpleDomainHoldService.extendHoldOnActiveTick(entity);
    }

    public static double resolveSimpleDomainRadius(double radiusBeforeCap) {
        return AntiDomainTechniqueService.capSimpleDomainRadius(radiusBeforeCap);
    }

    static boolean shouldSpawnRingParticle(boolean suppressSimpleDomainDerivedEffects) {
        return !suppressSimpleDomainDerivedEffects;
    }

    static List<Entity> resolveNearbySimpleDomainTargets(boolean suppressSimpleDomainDerivedEffects, List<Entity> original) {
        return suppressSimpleDomainDerivedEffects ? Collections.emptyList() : original;
    }
}
