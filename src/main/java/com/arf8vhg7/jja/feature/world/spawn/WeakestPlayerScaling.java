package com.arf8vhg7.jja.feature.world.spawn;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import java.util.UUID;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class WeakestPlayerScaling {
    private WeakestPlayerScaling() {
    }

    public static void refresh(@Nullable MinecraftServer server) {
        refresh(server, null);
    }

    public static void refresh(@Nullable MinecraftServer server, @Nullable UUID excludedPlayerId) {
        if (server == null) {
            return;
        }

        ServerLevel overworld = server.overworld();
        if (overworld == null) {
            return;
        }

        double strongestPlayer = resolveStrongestPlayer(server, excludedPlayerId);
        JujutsucraftModVariables.MapVariables mapVariables = JujutsucraftModVariables.MapVariables.get(overworld);
        if (Double.compare(mapVariables.STRONGEST_PLAYER, strongestPlayer) == 0) {
            return;
        }

        mapVariables.STRONGEST_PLAYER = strongestPlayer;
        mapVariables.syncData(overworld);
    }

    private static double resolveStrongestPlayer(MinecraftServer server, @Nullable UUID excludedPlayerId) {
        boolean weakestPlayerScaling = JjaCommonConfig.WEAKEST_PLAYER_SCALING.get();
        double strongestPlayer = weakestPlayerScaling ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        boolean found = false;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (excludedPlayerId != null && excludedPlayerId.equals(player.getUUID())) {
                continue;
            }

            JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
            if (playerVariables == null) {
                continue;
            }

            strongestPlayer = weakestPlayerScaling
                ? Math.min(strongestPlayer, playerVariables.PlayerLevel)
                : Math.max(strongestPlayer, playerVariables.PlayerLevel);
            found = true;
        }

        return found ? strongestPlayer : 0.0;
    }
}
