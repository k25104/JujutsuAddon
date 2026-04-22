package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.mobility.fly.FlySpaceControlService;
import com.arf8vhg7.jja.feature.player.mobility.fly.ObservedDoubleJumpUnlockService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class KeySpaceOnKeyPressedProcedureHook {
    private KeySpaceOnKeyPressedProcedureHook() {
    }

    public static void onSpacePressed(Entity entity) {
        if (entity instanceof Player player) {
            FlySpaceControlService.onSpacePressed(player);
        }
    }

    public static boolean shouldCancelPlayerContinuation(Entity entity) {
        return entity instanceof Player;
    }

    public static void onUpstreamDoubleJump(Entity entity) {
        ObservedDoubleJumpUnlockService.observeDoubleJump(entity);
    }
}
