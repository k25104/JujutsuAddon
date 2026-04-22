package com.arf8vhg7.jja.feature.jja.technique.shared.effect;

import net.minecraft.world.effect.MobEffect;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class InsectArmorEffectVisibility {
    private InsectArmorEffectVisibility() {
    }

    public static void applyHiddenEffect(LivingEntity livingEntity, int amplifier) {
        livingEntity.addEffect(createHiddenEffect(MobEffectInstance.INFINITE_DURATION, amplifier, false));
    }

    public static MobEffectInstance normalize(MobEffectInstance effectInstance) {
        if (effectInstance == null || !isInsectArmorEffect(effectInstance.getEffect())) {
            return effectInstance;
        }

        return createHiddenEffect(
            effectInstance.getDuration(),
            effectInstance.getAmplifier(),
            effectInstance.isAmbient()
        );
    }

    private static MobEffectInstance createHiddenEffect(int duration, int amplifier, boolean ambient) {
        return new MobEffectInstance(
            JujutsucraftModMobEffects.INSECT_ARMOR_TECHNIQUE.get(),
            duration,
            amplifier,
            ambient,
            false,
            false
        );
    }

    private static boolean isInsectArmorEffect(MobEffect effect) {
        return effect == JujutsucraftModMobEffects.INSECT_ARMOR_TECHNIQUE.get();
    }
}
