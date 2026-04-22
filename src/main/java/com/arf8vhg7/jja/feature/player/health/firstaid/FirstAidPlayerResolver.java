package com.arf8vhg7.jja.feature.player.health.firstaid;

import com.arf8vhg7.jja.compat.firstaid.FirstAidCompatRuntime;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

final class FirstAidPlayerResolver {
    private FirstAidPlayerResolver() {
    }

    @Nullable
    static Player resolve(@Nullable Entity entity) {
        if (!(entity instanceof Player player) || player.level().isClientSide() || !FirstAidCompatRuntime.isFirstAidLoaded()) {
            return null;
        }
        return player;
    }
}
