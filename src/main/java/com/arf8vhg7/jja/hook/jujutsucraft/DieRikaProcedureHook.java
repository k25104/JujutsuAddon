package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.summon.SummonDeathCooldownPolicy;
import net.minecraft.world.entity.Entity;

public final class DieRikaProcedureHook {
    private DieRikaProcedureHook() {
    }

    public static boolean shouldApplyOwnerCooldown(Entity defeatedSummon) {
        return SummonDeathCooldownPolicy.shouldApplyDeathCooldown(defeatedSummon);
    }

    static boolean shouldApplyOwnerCooldownForClassName(String className) {
        return SummonDeathCooldownPolicy.shouldApplyDeathCooldownForClassName(className);
    }
}
