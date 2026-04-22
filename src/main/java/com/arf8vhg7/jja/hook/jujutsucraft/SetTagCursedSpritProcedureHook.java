package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.RikaCursedSpiritAdjustment;
import net.minecraft.world.entity.Entity;

public final class SetTagCursedSpritProcedureHook {
    private SetTagCursedSpritProcedureHook() {
    }

    public static void clearRika2CursedSpiritTag(Entity entity) {
        RikaCursedSpiritAdjustment.clearRika2CursedSpiritTag(entity);
    }
}
