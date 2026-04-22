package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class LogicConfilmDomainProcedureHook {
    private LogicConfilmDomainProcedureHook() {
    }

    public static MobEffectInstance getEffect(LivingEntity livingEntity, MobEffect effect) {
        return DomainExpansionHookSupport.getThresholdDurationEffect(livingEntity, effect);
    }

    public static double resolveCurrentRadius(Entity entity, double radius) {
        return DomainExpansionHookSupport.resolveCurrentRadius(entity, radius);
    }
}
