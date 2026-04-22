package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidMutationService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class ComedianOnEffectActiveTickProcedureHook {
    private ComedianOnEffectActiveTickProcedureHook() {
    }

    public static float getEffectiveHealth(Entity entity, float original) {
        return entity instanceof LivingEntity livingEntity ? FirstAidHealthAccess.getEffectiveHealth(livingEntity) : original;
    }

    public static void applySelfHeal(LivingEntity livingEntity, float health, Operation<Void> original) {
        if (!FirstAidMutationService.applyDistributedMaxFractionHeal(livingEntity, 0.1D)) {
            original.call(livingEntity, health);
        }
    }
}
