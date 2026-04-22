package com.arf8vhg7.jja.feature.combat.zone;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class ZoneEffectOverrides {
    private ZoneEffectOverrides() {
    }

    public static boolean shouldRecoverCursePower() {
        return false;
    }

    public static boolean shouldApplyDamageBoost() {
        return false;
    }

    public static MobEffectInstance adjustRangedStrengthEffect(Entity source, MobEffectInstance original) {
        if (shouldCopyZoneStrengthBonus(source) || original.getEffect() != MobEffects.DAMAGE_BOOST) {
            return original;
        }

        int adjustedAmplifier = Math.max(0, original.getAmplifier() - getZoneRangedStrengthBonus(source));
        if (adjustedAmplifier == original.getAmplifier()) {
            return original;
        }

        return new MobEffectInstance(
            original.getEffect(),
            original.getDuration(),
            adjustedAmplifier,
            original.isAmbient(),
            original.isVisible(),
            original.showIcon()
        );
    }

    public static double stripBlackFlashZoneBonus(Entity entity, double original) {
        if (shouldApplyBlackFlashZoneBonus(entity)) {
            return original;
        }
        return Math.max(0.0D, original - getZoneBlackFlashBonus(entity));
    }

    private static boolean shouldCopyZoneStrengthBonus(Entity source) {
        return shouldApplyDamageBoost() || getZoneRangedStrengthBonus(source) <= 0;
    }

    private static boolean shouldApplyBlackFlashZoneBonus(Entity entity) {
        return shouldApplyDamageBoost() || getZoneBlackFlashBonus(entity) <= 0;
    }

    private static int getZoneRangedStrengthBonus(Entity entity) {
        return getZoneAmplifier(entity) + 1;
    }

    private static int getZoneBlackFlashBonus(Entity entity) {
        return getZoneAmplifier(entity) + 2;
    }

    private static int getZoneAmplifier(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return -1;
        }

        MobEffectInstance zoneEffect = livingEntity.getEffect(JujutsucraftModMobEffects.ZONE.get());
        if (zoneEffect == null) {
            return -1;
        }
        return zoneEffect.getAmplifier();
    }
}
