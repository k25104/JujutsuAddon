package com.arf8vhg7.jja.feature.jja.domain.da;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class DisableUnstableDuringDA {
    private DisableUnstableDuringDA() {
    }

    public static boolean addEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        if (livingEntity == null || effectInstance == null) {
            return false;
        }

        if (effectInstance.getEffect() == JujutsucraftModMobEffects.UNSTABLE.get()) {
            return false;
        }

        return livingEntity.addEffect(effectInstance);
    }
}
