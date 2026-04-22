package com.arf8vhg7.jja.feature.jja.domain.fbe;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class FallingBlossomEmotionDamageRules {
    private FallingBlossomEmotionDamageRules() {
    }

    public static boolean allowOutsideDomain(boolean original, Entity defender) {
        return original || hasEffect(defender);
    }

    public static boolean allowWithoutNeutralization(boolean original, Entity defender) {
        return original || hasEffect(defender);
    }

    public static MobEffectInstance resolveNeutralizationGateEffect(MobEffectInstance original, MobEffect effect, Entity defender) {
        if (!hasEffect(defender)) {
            return original;
        }
        if (original != null && original.getAmplifier() > 0) {
            return original;
        }
        return new MobEffectInstance(effect, 1, 1, false, false);
    }

    private static boolean hasEffect(Entity entity) {
        return entity instanceof LivingEntity livingEntity
            && livingEntity.hasEffect((MobEffect) JujutsucraftModMobEffects.FALLING_BLOSSOM_EMOTION.get());
    }
}
