package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeItemHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class Recommendation1RightClickedInAirProcedureHook {
    private Recommendation1RightClickedInAirProcedureHook() {
    }

    public static boolean handle(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack) {
        return SorcererGradeItemHandler.handleRecommendation2(world, x, y, z, entity, itemStack);
    }
}
