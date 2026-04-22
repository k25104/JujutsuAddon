package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.zone.ZoneChargeScalingService;
import net.minecraft.world.entity.Entity;

public final class AttackStrongMahitoProcedureHook {
    private AttackStrongMahitoProcedureHook() {
    }

    public static boolean isFullChargeOverflow(Entity entity, boolean original) {
        return ZoneChargeScalingService.isCnt6FullChargeOverflow(entity, original);
    }

    public static boolean isFullChargeReached(Entity entity, boolean original) {
        return ZoneChargeScalingService.isCnt6FullChargeReached(entity, original);
    }
}
