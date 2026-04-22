package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.targeting.AttackTargetSelectionRestrictionService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class LogicAttackTargetProcedureHook {
    private LogicAttackTargetProcedureHook() {
    }

    public static boolean allowCurrentTarget(boolean original, LevelAccessor world, Entity entity) {
        return AttackTargetSelectionRestrictionService.allowCurrentTarget(world, entity, original);
    }
}
