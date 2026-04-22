package com.arf8vhg7.jja.feature.jja.resource.ce;

import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class CePoolScalingSync {
    private static boolean dirty = true;

    private CePoolScalingSync() {
    }

    public static void markDirty() {
        dirty = true;
    }

    public static void refresh(@Nullable MinecraftServer server) {
        if (!dirty || server == null) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            JjaCursePowerAccountingService.refreshPlayerCursePowerFormer(player);
        }

        dirty = false;
    }

    static boolean isDirty() {
        return dirty;
    }

    static void clearDirty() {
        dirty = false;
    }
}
