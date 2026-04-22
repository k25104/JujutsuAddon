package com.arf8vhg7.jja.feature.player.health.firstaid;

import com.arf8vhg7.jja.compat.firstaid.FirstAidHealthCompat;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class FirstAidHealthAccess {
    private FirstAidHealthAccess() {
    }

    public static float getEffectiveHealth(@Nullable LivingEntity livingEntity) {
        return FirstAidHealthCompat.getEffectiveHealth(livingEntity);
    }

    public static float getEffectiveHealth(@Nullable LivingEntity livingEntity, float fallback) {
        return livingEntity == null ? fallback : FirstAidHealthCompat.getEffectiveHealth(livingEntity);
    }

    public static float getEffectiveHealth(@Nullable Entity entity, float fallback) {
        return entity instanceof LivingEntity livingEntity ? FirstAidHealthCompat.getEffectiveHealth(livingEntity) : fallback;
    }

    public static boolean isEffectivelyAtFullHealth(@Nullable LivingEntity livingEntity) {
        return FirstAidHealthCompat.isEffectivelyAtFullHealth(livingEntity);
    }
}
