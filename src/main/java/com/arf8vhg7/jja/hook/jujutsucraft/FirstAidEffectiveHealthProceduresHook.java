package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class FirstAidEffectiveHealthProceduresHook {
    private FirstAidEffectiveHealthProceduresHook() {
    }

    public static float getEffectiveHealth(Entity entity, float original) {
        return entity instanceof LivingEntity livingEntity ? FirstAidHealthAccess.getEffectiveHealth(livingEntity) : original;
    }
}
