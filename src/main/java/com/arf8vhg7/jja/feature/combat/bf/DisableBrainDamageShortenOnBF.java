package com.arf8vhg7.jja.feature.combat.bf;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class DisableBrainDamageShortenOnBF {
    private DisableBrainDamageShortenOnBF() {
    }

    public static boolean removeEffect(LivingEntity livingEntity, MobEffect effect) {
        if (livingEntity == null || effect == null) {
            return false;
        }

        if (effect == JujutsucraftModMobEffects.BRAIN_DAMAGE.get()) {
            return false;
        }

        return livingEntity.removeEffect(effect);
    }

    public static boolean addEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        if (livingEntity == null || effectInstance == null) {
            return false;
        }

        if (effectInstance.getEffect() == JujutsucraftModMobEffects.BRAIN_DAMAGE.get()) {
            return false;
        }

        return livingEntity.addEffect(effectInstance);
    }
}
