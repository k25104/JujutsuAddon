package com.arf8vhg7.jja.feature.jja.domain.de;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class UnstableDurationOnDomainRelease {
    private static final String KEY_CURSED_SPIRIT = "CursedSpirit";
    private static final int ORIGINAL_DURATION = 600;

    private UnstableDurationOnDomainRelease() {
    }

    public static boolean addEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        if (livingEntity == null) {
            return false;
        }

        return livingEntity.addEffect(adjustDuration(
            livingEntity.getPersistentData().getBoolean(KEY_CURSED_SPIRIT),
            effectInstance,
            DomainExpansionDurationConfig.getUnstableDuration()
        ));
    }

    static MobEffectInstance adjustDuration(boolean cursedSpirit, MobEffectInstance effectInstance, int unstableDuration) {
        if (effectInstance == null) {
            return null;
        }

        if (effectInstance.getEffect() != JujutsucraftModMobEffects.UNSTABLE.get()) {
            return effectInstance;
        }

        if (effectInstance.getDuration() != ORIGINAL_DURATION) {
            return effectInstance;
        }

        return new MobEffectInstance(
            effectInstance.getEffect(),
            resolveReleaseDuration(cursedSpirit, unstableDuration),
            effectInstance.getAmplifier(),
            effectInstance.isAmbient(),
            effectInstance.isVisible(),
            effectInstance.showIcon()
        );
    }

    static int resolveReleaseDuration(boolean cursedSpirit, int unstableDuration) {
        return cursedSpirit ? unstableDuration / 2 : unstableDuration;
    }
}
