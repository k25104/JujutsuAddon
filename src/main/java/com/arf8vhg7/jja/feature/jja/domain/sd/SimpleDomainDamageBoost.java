package com.arf8vhg7.jja.feature.jja.domain.sd;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class SimpleDomainDamageBoost {
    public static final double MULTIPLIER = 1.1;

    private SimpleDomainDamageBoost() {
    }

    public static double resolveMultiplier(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 1.0D;
        }
        MobEffectInstance simpleDomain = livingEntity.getEffect(JujutsucraftModMobEffects.SIMPLE_DOMAIN.get());
        return resolveMultiplier(simpleDomain != null, simpleDomain != null ? simpleDomain.getAmplifier() : -1);
    }

    static double resolveMultiplier(boolean hasSimpleDomainEffect, int amplifier) {
        return hasSimpleDomainEffect && amplifier > 0 ? MULTIPLIER : 1.0D;
    }
}
