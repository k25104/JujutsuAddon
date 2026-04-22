package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidResetService;
import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeItemHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class Demotion1RightclickedProcedureHook {
    private Demotion1RightclickedProcedureHook() {
    }

    public static boolean handle(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack) {
        return SorcererGradeItemHandler.handleSpecialDemotion(world, x, y, z, entity, itemStack);
    }

    public static void beginFirstAidReset(Entity entity) {
        if (SorcererGradeItemHandler.isSpecialDemotion(entity)) {
            return;
        }
        FirstAidResetService.beginResetTransaction(entity);
    }

    public static void finishFirstAidReset(Entity entity) {
        if (SorcererGradeItemHandler.isSpecialDemotion(entity)) {
            return;
        }
        FirstAidResetService.finishResetTransaction(entity);
    }
}
