package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import com.arf8vhg7.jja.feature.jja.technique.family.hakari.HakariJackpotDurationOverride;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class AISLOTProcedureHook {
    private AISLOTProcedureHook() {
    }

    public static int modifyJackpotDuration(int originalDuration) {
        return HakariJackpotDurationOverride.replaceHakariJackpotDuration(originalDuration);
    }

    public static boolean addEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return DomainExpansionHookSupport.addNormalizedDurationEffect(livingEntity, effectInstance);
    }
}
