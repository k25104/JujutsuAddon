package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import net.minecraft.world.entity.LivingEntity;

public final class RctHealGate {
    private RctHealGate() {
    }

    public static boolean isRctFullyHealed(LivingEntity entity) {
        return FirstAidHealthAccess.isEffectivelyAtFullHealth(entity);
    }
}
