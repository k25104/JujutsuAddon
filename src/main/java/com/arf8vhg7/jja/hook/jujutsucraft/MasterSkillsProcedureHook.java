package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.rct.RctMasteryItemHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class MasterSkillsProcedureHook {
    private MasterSkillsProcedureHook() {
    }

    public static boolean handle(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack) {
        return RctMasteryItemHandler.handle(world, x, y, z, entity, itemStack);
    }
}
