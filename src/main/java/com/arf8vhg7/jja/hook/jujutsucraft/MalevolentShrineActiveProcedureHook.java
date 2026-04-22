package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import com.arf8vhg7.jja.feature.jja.domain.de.MalevolentShrineTerrainDestruction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class MalevolentShrineActiveProcedureHook {
    static final double OPEN_MULTIPLIER_IN_CODE = 18.0D;

    private MalevolentShrineActiveProcedureHook() {
    }

    public static MobEffectInstance getEffect(LivingEntity livingEntity, MobEffect effect) {
        return DomainExpansionHookSupport.getCountDurationEffect(livingEntity, effect);
    }

    public static double adjustDomainRange(LivingEntity livingEntity, double radius) {
        return DomainExpansionHookSupport.adjustOpenBarrierRange(livingEntity, radius, OPEN_MULTIPLIER_IN_CODE);
    }

    static double adjustDomainRangeForState(boolean openBarrierActive, double currentRadius, double fallbackRadius) {
        return DomainExpansionHookSupport.resolveActiveRangeForState(
            openBarrierActive,
            currentRadius,
            fallbackRadius,
            OPEN_MULTIPLIER_IN_CODE
        );
    }

    public static void tryExtraTerrainDestruction(LevelAccessor world, Entity entity, double range, double xCenter, double yCenter, double zCenter) {
        MalevolentShrineTerrainDestruction.tryExtraAttempts(world, entity, range, xCenter, yCenter, zCenter);
    }
}
