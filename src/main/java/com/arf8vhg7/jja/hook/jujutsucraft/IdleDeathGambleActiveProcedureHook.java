package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class IdleDeathGambleActiveProcedureHook {
    private IdleDeathGambleActiveProcedureHook() {
    }

    public static MobEffectInstance getEffect(LivingEntity livingEntity, MobEffect effect) {
        return DomainExpansionHookSupport.getCountDurationEffect(livingEntity, effect);
    }

    public static boolean addEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return DomainExpansionHookSupport.addNormalizedDurationEffect(livingEntity, effectInstance);
    }
    public static double adjustDomainRange(LivingEntity livingEntity, double radius) {
        return DomainExpansionHookSupport.adjustOpenBarrierRange(livingEntity, radius, 2.0);
    }

}
