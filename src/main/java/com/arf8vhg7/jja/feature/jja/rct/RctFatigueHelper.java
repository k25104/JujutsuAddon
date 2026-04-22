package com.arf8vhg7.jja.feature.jja.rct;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class RctFatigueHelper {
    private RctFatigueHelper() {
    }

    public static boolean addRctFatigueIfPresent(LivingEntity entity, int extraTicks) {
        if (entity == null || RctMath.isCursedSpirit(entity)) {
            return false;
        }
        MobEffectInstance current = entity.getEffect((MobEffect) JujutsucraftModMobEffects.FATIGUE.get());
        if (current == null) {
            return setOrCreateFatigue(entity, resolveNextFatigueDuration(0, extraTicks), 0);
        }
        return entity.addEffect(
            new MobEffectInstance(
                (MobEffect) JujutsucraftModMobEffects.FATIGUE.get(),
                resolveNextFatigueDuration(current.getDuration(), extraTicks),
                current.getAmplifier(),
                false,
                false
            )
        );
    }

    public static boolean setOrCreateFatigue(LivingEntity entity, int duration, int amplifier) {
        if (entity == null || RctMath.isCursedSpirit(entity)) {
            return false;
        }
        return entity.addEffect(
            new MobEffectInstance(
                (MobEffect) JujutsucraftModMobEffects.FATIGUE.get(),
                Math.max(0, duration),
                Math.max(0, amplifier),
                false,
                false
            )
        );
    }

    static int resolveNextFatigueDuration(int currentDuration, int extraTicks) {
        return Math.min(Math.max(currentDuration, 0) + Math.max(extraTicks, 0), 6000);
    }
}
