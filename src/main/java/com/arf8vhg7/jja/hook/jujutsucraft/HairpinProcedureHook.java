package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.kugisaki.KugisakiHairpinService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class HairpinProcedureHook {
    private HairpinProcedureHook() {
    }

    public static boolean handle(LevelAccessor world, double x, double y, double z, Entity entity) {
        KugisakiHairpinService.execute(world, x, y, z, entity);
        return true;
    }
}
