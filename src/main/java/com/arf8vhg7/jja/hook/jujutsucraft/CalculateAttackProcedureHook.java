package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.damage.CombatCooldownStrengthScaling;
import net.minecraft.world.entity.Entity;

public final class CalculateAttackProcedureHook {
    private CalculateAttackProcedureHook() {
    }

    public static double scaleCombatCooldownAttackSpeedPenalty(Entity entity, double originalPenalty) {
        return CombatCooldownStrengthScaling.scaleAttackSpeedPenalty(entity, originalPenalty);
    }
}
