package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class MalevolentShrineProcedureHook {
    private MalevolentShrineProcedureHook() {
    }

    public static double jjaAdjustYPosDoma(String key, double value) {
        if ("y_pos_doma".equals(key)) {
            return value - 1.0;
        }
        return value;
    }

    public static float getEffectiveHealth(Entity entity, float original) {
        return entity instanceof LivingEntity livingEntity ? FirstAidHealthAccess.getEffectiveHealth(livingEntity) : original;
    }
}
