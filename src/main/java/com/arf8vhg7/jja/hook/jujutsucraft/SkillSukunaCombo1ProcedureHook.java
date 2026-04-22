package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.activation.SukunaComboProjectileRules;
import net.minecraft.world.entity.Entity;

public final class SkillSukunaCombo1ProcedureHook {
    private SkillSukunaCombo1ProcedureHook() {
    }

    public static boolean allowProjectileSlash(Entity entity, boolean original) {
        return SukunaComboProjectileRules.allowProjectileSlash(entity, original);
    }
}
