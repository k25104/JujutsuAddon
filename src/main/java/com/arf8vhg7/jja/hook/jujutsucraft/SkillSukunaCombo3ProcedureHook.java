package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.activation.SukunaComboProjectileRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class SkillSukunaCombo3ProcedureHook {
    private SkillSukunaCombo3ProcedureHook() {
    }

    public static boolean allowProjectileSlash(Entity entity, boolean original) {
        return SukunaComboProjectileRules.allowProjectileSlash(entity, original);
    }

    public static boolean allowProjectileSlash(Entity entity, ItemStack mainHandSnapshot, boolean original) {
        return SukunaComboProjectileRules.allowProjectileSlash(entity, mainHandSnapshot, original);
    }
}
