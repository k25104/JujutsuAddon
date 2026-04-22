package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.da.DomainAmplificationWitnessService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class WhenBeforeEntityisHurtNoSourceProcedureHook {
    private WhenBeforeEntityisHurtNoSourceProcedureHook() {
    }

    public static double scaleReverseCursedTechniqueGrantRandom(double original, Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 1.0D;
        }

        return scaleReverseCursedTechniqueGrantRandom(original, livingEntity.getHealth(), livingEntity.getMaxHealth());
    }

    public static double scaleReverseCursedTechniqueGrantRandom(double original, double currentHealth, double maxHealth) {
        double missingHealthFactor = resolveMissingHealthFactor(currentHealth, maxHealth);
        if (missingHealthFactor <= 0.0D) {
            return 1.0D;
        }

        return original / missingHealthFactor;
    }

    public static double suppressLegacyDomainAmplificationGrantRandom(double original) {
        return DomainAmplificationWitnessService.shouldSuppressLegacyGrant() ? 1.0D : original;
    }

    static double resolveMissingHealthFactor(double currentHealth, double maxHealth) {
        if (!(maxHealth > 0.0D)) {
            return 0.0D;
        }

        double factor = (maxHealth - currentHealth) / maxHealth;
        return Math.max(Math.min(factor, 1.0D), 0.0D);
    }
}
