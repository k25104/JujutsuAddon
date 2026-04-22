package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniqueHeldItemRules;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyCombatPassContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public final class CursedToolsAbility2ProcedureHook {
    private CursedToolsAbility2ProcedureHook() {
    }

    public static double resolveHeldItemPowerBonus(Entity entity, double value) {
        if (!TechniqueHeldItemRules.shouldDisableHeldItemDamageBonus(entity, TwinnedBodyCombatPassContext.isExtraArmAttack())) {
            return value;
        }

        return 0.0D;
    }

    public static double resolveHeldItemDamageBonus(Entity entity, CompoundTag tag, String key, double value) {
        if (!"Damage".equals(key) || !TechniqueHeldItemRules.shouldDisableHeldItemDamageBonus(entity, TwinnedBodyCombatPassContext.isExtraArmAttack())) {
            return value;
        }
        return Math.min(value, tag.getDouble("Damage"));
    }
}
