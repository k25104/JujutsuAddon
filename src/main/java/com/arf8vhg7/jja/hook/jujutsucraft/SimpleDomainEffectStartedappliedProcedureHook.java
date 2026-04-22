package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueService;
import net.minecraft.world.entity.Entity;

public final class SimpleDomainEffectStartedappliedProcedureHook {
    private SimpleDomainEffectStartedappliedProcedureHook() {
    }

    public static boolean shouldEmitRingParticles(Entity entity) {
        return shouldEmitRingParticles(AntiDomainTechniqueService.shouldSuppressActivePlayerEffects(entity));
    }

    public static double resolveSimpleDomainRadius(double radiusBeforeCap) {
        return AntiDomainTechniqueService.capSimpleDomainRadius(radiusBeforeCap);
    }

    static boolean shouldEmitRingParticles(boolean suppressSimpleDomainDerivedEffects) {
        return !suppressSimpleDomainDerivedEffects;
    }
}
