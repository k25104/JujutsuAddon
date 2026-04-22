package com.arf8vhg7.jja.feature.jja.domain.de;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class DomainExpansionDamageBoost {
    public static final double MULTIPLIER = 1.2;

    private DomainExpansionDamageBoost() {
    }

    public static double resolveMultiplier(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 1.0D;
        }
        return resolveMultiplier(livingEntity.hasEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get()));
    }

    static double resolveMultiplier(boolean hasDomainExpansionEffect) {
        return hasDomainExpansionEffect ? MULTIPLIER : 1.0D;
    }
}
