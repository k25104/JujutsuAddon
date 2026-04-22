package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.effect.InfinityEffectVisibility;
import java.util.Objects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class WhenPlayerActiveTickInfinityProcedureHook {
    private WhenPlayerActiveTickInfinityProcedureHook() {
    }

    public static boolean ignoreNeutralizationForInfinity(boolean original) {
        return false;
    }

    public static boolean ignoreSimpleDomainForInfinity(boolean original) {
        return false;
    }

    public static boolean addInfinityEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        if (effectInstance == null) {
            return false;
        }

        MobEffectInstance normalizedEffect = Objects.requireNonNull(InfinityEffectVisibility.normalize(effectInstance));
        return livingEntity.addEffect(normalizedEffect);
    }
}
