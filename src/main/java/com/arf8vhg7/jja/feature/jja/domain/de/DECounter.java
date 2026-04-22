package com.arf8vhg7.jja.feature.jja.domain.de;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class DECounter {
    public static final String KEY = "jja_cnt_domain";
    private static final double CLASH_DURATION_OFFSET = 1200.0;
    private static final double THRESHOLD_DURATION_OFFSET = 1201.0;

    private DECounter() {
    }

    public static void reset(Entity entity) {
        if (entity != null) {
            entity.getPersistentData().putDouble(KEY, 0.0);
        }
    }

    public static void tick(Entity entity) {
        if (entity != null) {
            entity.getPersistentData().putDouble(KEY, get(entity) + 1.0);
        }
    }

    public static double get(Entity entity) {
        return entity == null ? 0.0 : entity.getPersistentData().getDouble(KEY);
    }

    public static void clear(Entity entity) {
        if (entity != null) {
            entity.getPersistentData().remove(KEY);
        }
    }

    public static MobEffectInstance getEffectWithCountDuration(LivingEntity livingEntity, MobEffect effect) {
        return getEffectWithDuration(livingEntity, effect, get(livingEntity));
    }

    public static MobEffectInstance getEffectWithClashDuration(LivingEntity livingEntity, MobEffect effect) {
        return getEffectWithDuration(livingEntity, effect, CLASH_DURATION_OFFSET - get(livingEntity));
    }

    public static MobEffectInstance getEffectWithThresholdDuration(LivingEntity livingEntity, MobEffect effect) {
        return getEffectWithDuration(livingEntity, effect, THRESHOLD_DURATION_OFFSET - get(livingEntity));
    }

    public static int getThresholdDuration(MobEffectInstance effectInstance, Entity entity) {
        return getDuration(effectInstance, THRESHOLD_DURATION_OFFSET - get(entity));
    }

    public static boolean addEffectWithNormalizedDuration(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return livingEntity.addEffect(normalizeDuration(effectInstance));
    }

    public static boolean addEffectUnlessDomainExtension(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        if (effectInstance != null && isDomainExpansion(effectInstance.getEffect())) {
            return false;
        }

        return livingEntity.addEffect(effectInstance);
    }

    public static MobEffectInstance normalizeDuration(MobEffectInstance effectInstance) {
        return normalizeDuration(effectInstance, DomainExpansionDurationConfig.getDomainExpansionDuration());
    }

    static MobEffectInstance normalizeDuration(MobEffectInstance effectInstance, int configuredDuration) {
        if (effectInstance == null || !isDomainExpansion(effectInstance.getEffect())) {
            return effectInstance;
        }

        return copyWithDuration(effectInstance, resolveNormalizedDuration(configuredDuration));
    }

    static int resolveNormalizedDuration(int configuredDuration) {
        return configuredDuration;
    }

    public static double disableDurationDecay(double left, double right) {
        if (right == 1200.0) {
            return 1200.0;
        }

        return Math.min(left, right);
    }

    private static MobEffectInstance getEffectWithDuration(LivingEntity livingEntity, MobEffect effect, double duration) {
        MobEffectInstance effectInstance = livingEntity.getEffect(effect);
        if (!isDomainExpansion(effect) || effectInstance == null) {
            return effectInstance;
        }

        return copyWithDuration(effectInstance, duration);
    }

    private static int getDuration(MobEffectInstance effectInstance, double duration) {
        if (effectInstance == null || !isDomainExpansion(effectInstance.getEffect())) {
            return effectInstance == null ? 0 : effectInstance.getDuration();
        }

        return toInt(duration);
    }

    private static MobEffectInstance copyWithDuration(MobEffectInstance effectInstance, double duration) {
        return new MobEffectInstance(
            effectInstance.getEffect(),
            toInt(duration),
            effectInstance.getAmplifier(),
            effectInstance.isAmbient(),
            effectInstance.isVisible(),
            effectInstance.showIcon()
        );
    }

    private static boolean isDomainExpansion(MobEffect effect) {
        return effect == JujutsucraftModMobEffects.DOMAIN_EXPANSION.get();
    }

    private static int toInt(double value) {
        if (value >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        if (value <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }

        return (int)Math.round(value);
    }
}
