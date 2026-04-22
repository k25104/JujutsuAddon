package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.world.spawn.SpawnLevelChanceService;
import net.minecraft.world.level.LevelAccessor;

public final class SpawnLevel3ProcedureHook {
    private SpawnLevel3ProcedureHook() {
    }

    public static boolean execute(LevelAccessor world) {
        return SpawnLevelChanceService.shouldSpawnLevel3(world);
    }
}
