package com.arf8vhg7.jja.feature.jja.domain.de;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class DomainBarrierSpeedScaling {
    private DomainBarrierSpeedScaling() {
    }

    public static double scale(Entity entity, double originalSpeed) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return originalSpeed;
        }

        MobEffectInstance strengthEffect = livingEntity.getEffect(MobEffects.DAMAGE_BOOST);
        if (strengthEffect == null) {
            return originalSpeed;
        }

        double strengthSpeed = Math.floor(Math.pow(1.05D, strengthEffect.getAmplifier()));
        return Math.max(originalSpeed, strengthSpeed);
    }
}
