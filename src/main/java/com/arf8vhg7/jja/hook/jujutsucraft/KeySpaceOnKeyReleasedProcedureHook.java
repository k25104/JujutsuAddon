package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.mobility.fly.FlySpaceControlService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class KeySpaceOnKeyReleasedProcedureHook {
    private KeySpaceOnKeyReleasedProcedureHook() {
    }

    public static void onSpaceReleased(Entity entity) {
        if (entity instanceof Player player) {
            FlySpaceControlService.onSpaceReleased(player);
        }
    }
}
