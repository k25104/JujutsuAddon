package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import com.arf8vhg7.jja.feature.jja.domain.de.UnstableDurationOnDomainRelease;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;

public final class DomainExpansionEffectExpiresProcedureHook {
    private DomainExpansionEffectExpiresProcedureHook() {
    }

    public static boolean addEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return UnstableDurationOnDomainRelease.addEffect(livingEntity, effectInstance);
    }

    public static void clearCounter(Entity entity) {
        DomainExpansionHookSupport.clearCounter(entity);
        DomainExpansionHookSupport.clearRadiusRuntime(entity);
    }
}
