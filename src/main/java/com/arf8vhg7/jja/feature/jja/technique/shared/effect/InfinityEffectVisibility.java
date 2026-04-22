package com.arf8vhg7.jja.feature.jja.technique.shared.effect;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public final class InfinityEffectVisibility {
    private InfinityEffectVisibility() {
    }

    public static MobEffectInstance normalize(MobEffectInstance effectInstance) {
        if (effectInstance == null || !isInfinityEffect(effectInstance.getEffect())) {
            return effectInstance;
        }

        return new MobEffectInstance(
            effectInstance.getEffect(),
            effectInstance.getDuration(),
            effectInstance.getAmplifier(),
            effectInstance.isAmbient(),
            false,
            false
        );
    }

    private static boolean isInfinityEffect(MobEffect effect) {
        return effect == JujutsucraftModMobEffects.INFINITY_EFFECT.get();
    }
}
