package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.selection.RandomCtSelectionService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public final class SelectRandomProcedureHook {
    private SelectRandomProcedureHook() {
    }

    public static boolean handle(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (world instanceof Level level && level.isClientSide()) {
            return true;
        }
        RandomCtSelectionService.beginSelectableRandomSelection(entity);
        return RandomCtSelectionService.handle(world, x, y, z, entity);
    }
}
