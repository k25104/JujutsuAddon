package com.arf8vhg7.jja.feature.jja.domain.de;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import java.util.Objects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class OpenBarrierDomainRange {
    private static final double OPEN_BARRIER_RANGE_MULTIPLIER = 18.0;

    private OpenBarrierDomainRange() {
    }

    public static double adjust(LivingEntity livingEntity, double radius, double openMultiplierInCode) {
        if (livingEntity == null) {
            return radius;
        }

        MobEffectInstance effectInstance = livingEntity.getEffect(Objects.requireNonNull(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get()));
        if (effectInstance == null || effectInstance.getAmplifier() <= 0) {
            return radius;
        }

        if (openMultiplierInCode <= 0.0) {
            return radius;
        }

        return radius * (OPEN_BARRIER_RANGE_MULTIPLIER / openMultiplierInCode);
    }
}
