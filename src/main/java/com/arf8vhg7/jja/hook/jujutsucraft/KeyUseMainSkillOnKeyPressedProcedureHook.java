package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.MainSkillSelectOverride;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class KeyUseMainSkillOnKeyPressedProcedureHook {
    private KeyUseMainSkillOnKeyPressedProcedureHook() {
    }

    public static RegisteredCurseTechniqueSlots.SelectOverride resolveSelectOverride(
        LevelAccessor world,
        Entity entity,
        double currentCt,
        double currentSelect
    ) {
        return MainSkillSelectOverride.resolve(world, entity, currentCt, currentSelect);
    }
}
