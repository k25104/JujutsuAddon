package com.arf8vhg7.jja.feature.combat.damage;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class CombatCooldownStrengthScaling {
    private CombatCooldownStrengthScaling() {
    }

    public static double scaleAttackSpeedPenalty(Entity entity, double originalPenalty) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return originalPenalty;
        }

        MobEffectInstance strengthEffect = livingEntity.getEffect(MobEffects.DAMAGE_BOOST);
        if (strengthEffect == null) {
            return originalPenalty;
        }

        double strengthLevel = strengthEffect.getAmplifier() + 1.0D;
        double strengthDivisor = Math.max(1.0D, strengthLevel / 3.0D);
        return originalPenalty / strengthDivisor;
    }
}
