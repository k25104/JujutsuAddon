package com.arf8vhg7.jja.feature.player.state;

import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import net.minecraft.world.entity.Entity;

public final class AddonStatsAccess {
    private AddonStatsAccess() {
    }

    public static int getCounter(Entity entity, AddonStatCounter counter) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(entity);
        return addonStats == null ? 0 : counter.get(addonStats);
    }

    public static boolean setCounter(Entity entity, AddonStatCounter counter, int value) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(entity);
        if (addonStats == null) {
            return false;
        }
        counter.set(addonStats, value);
        return true;
    }

    public static boolean incrementCounter(Entity entity, AddonStatCounter counter) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(entity);
        if (!(entity instanceof net.minecraft.server.level.ServerPlayer) || addonStats == null) {
            return false;
        }
        counter.increment(addonStats);
        return true;
    }
}
