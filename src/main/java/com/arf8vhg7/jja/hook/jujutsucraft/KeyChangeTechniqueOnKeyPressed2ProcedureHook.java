package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.geto.GetoTechniqueSelectionService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class KeyChangeTechniqueOnKeyPressed2ProcedureHook {
    private KeyChangeTechniqueOnKeyPressed2ProcedureHook() {
    }

    public static boolean handleCustomSelection(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double playerSelect
    ) {
        return GetoTechniqueSelectionService.tryHandlePageSelection(world, x, y, z, entity, playerCt, playerSelect);
    }
}
