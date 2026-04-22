package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class SkillJudgementProcedureHook {
    private SkillJudgementProcedureHook() {
    }

    public static boolean addEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return DomainExpansionHookSupport.addEffectUnlessDomainExtension(livingEntity, effectInstance);
    }
}
