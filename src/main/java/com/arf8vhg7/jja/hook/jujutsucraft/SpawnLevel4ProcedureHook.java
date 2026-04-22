package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.world.spawn.SpawnLevelChanceService;
import net.minecraft.world.level.LevelAccessor;

public final class SpawnLevel4ProcedureHook {
    private SpawnLevel4ProcedureHook() {
    }

    public static boolean execute(LevelAccessor world) {
        return SpawnLevelChanceService.shouldSpawnLevel4(world);
    }
}
