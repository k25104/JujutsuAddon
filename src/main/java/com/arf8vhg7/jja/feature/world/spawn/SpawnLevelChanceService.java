package com.arf8vhg7.jja.feature.world.spawn;

import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.SpawnLevel2Procedure;
import net.mcreator.jujutsucraft.procedures.SpawnLevel3Procedure;
import net.mcreator.jujutsucraft.procedures.SpawnLevel4Procedure;
import net.minecraft.world.level.LevelAccessor;

public final class SpawnLevelChanceService {
    private SpawnLevelChanceService() {
    }

    public static boolean shouldSpawnLevel3(LevelAccessor world) {
        return roll(resolveLevel3Chance(resolveStrongestPlayer(world))) && SpawnLevel2Procedure.execute(world);
    }

    public static boolean shouldSpawnLevel4(LevelAccessor world) {
        return roll(resolveLevel4Chance(resolveStrongestPlayer(world))) && SpawnLevel3Procedure.execute(world);
    }

    public static boolean shouldSpawnLevel5(LevelAccessor world) {
        return roll(resolveLevel5Chance(resolveStrongestPlayer(world))) && SpawnLevel4Procedure.execute(world);
    }

    public static double resolveLevel3Chance(double strongestPlayer) {
        if (strongestPlayer <= 1.0) {
            return 0.02;
        }
        if (strongestPlayer <= 2.0) {
            return 0.02;
        }
        if (strongestPlayer <= 4.0) {
            return 0.05;
        }
        if (strongestPlayer <= 7.0) {
            return 0.10;
        }
        if (strongestPlayer <= 9.0) {
            return 0.10;
        }
        if (strongestPlayer <= 11.0) {
            return 0.125;
        }
        if (strongestPlayer <= 13.0) {
            return 0.15;
        }
        if (strongestPlayer <= 20.0) {
            return 0.25;
        }
        return resolveExtendedChance(strongestPlayer, 0.40, 0.55, 0.70, 0.85, 1.00);
    }

    public static double resolveLevel4Chance(double strongestPlayer) {
        if (strongestPlayer <= 1.0) {
            return 0.01;
        }
        if (strongestPlayer <= 2.0) {
            return 0.01;
        }
        if (strongestPlayer <= 4.0) {
            return 0.01;
        }
        if (strongestPlayer <= 7.0) {
            return 0.01;
        }
        if (strongestPlayer <= 9.0) {
            return 0.025;
        }
        if (strongestPlayer <= 11.0) {
            return 0.15;
        }
        if (strongestPlayer <= 13.0) {
            return 0.25;
        }
        if (strongestPlayer <= 20.0) {
            return 0.25;
        }
        return resolveExtendedChance(strongestPlayer, 0.35, 0.45, 0.55, 0.65, 0.75);
    }

    public static double resolveLevel5Chance(double strongestPlayer) {
        if (strongestPlayer <= 1.0) {
            return 0.01;
        }
        if (strongestPlayer <= 2.0) {
            return 0.01;
        }
        if (strongestPlayer <= 4.0) {
            return 0.01;
        }
        if (strongestPlayer <= 7.0) {
            return 0.01;
        }
        if (strongestPlayer <= 9.0) {
            return 0.01;
        }
        if (strongestPlayer <= 11.0) {
            return 0.05;
        }
        if (strongestPlayer <= 13.0) {
            return 0.10;
        }
        if (strongestPlayer <= 20.0) {
            return 0.25;
        }
        return resolveExtendedChance(strongestPlayer, 0.30, 0.35, 0.40, 0.45, 0.50);
    }

    private static double resolveStrongestPlayer(LevelAccessor world) {
        return world == null ? 0.0 : JujutsucraftModVariables.MapVariables.get(world).STRONGEST_PLAYER;
    }

    private static double resolveExtendedChance(
        double strongestPlayer,
        double chanceAt22,
        double chanceAt24,
        double chanceAt26,
        double chanceAt28,
        double chanceAt30
    ) {
        if (strongestPlayer <= 22.0) {
            return chanceAt22;
        }
        if (strongestPlayer <= 24.0) {
            return chanceAt24;
        }
        if (strongestPlayer <= 26.0) {
            return chanceAt26;
        }
        if (strongestPlayer <= 28.0) {
            return chanceAt28;
        }
        return chanceAt30;
    }

    private static boolean roll(double chance) {
        return Math.random() < chance;
    }
}
