package com.arf8vhg7.jja.hook.minecraft;

import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public final class ServerPlayerHook {
    private ServerPlayerHook() {
    }

    public static boolean tryEnterReviveWaitingAfterDeathMessage(ServerPlayer player, DamageSource damageSource) {
        return ReviveFlowService.tryEnterWaitingAfterDeathMessage(player, damageSource);
    }

    public static boolean resolveShowDeathMessages(boolean original, boolean suppressNextDeathMessage) {
        return original && !suppressNextDeathMessage;
    }

    public static boolean resolveShowDeathMessages(ServerPlayer player, boolean original) {
        return ReviveFlowService.resolveShowDeathMessages(player, original);
    }
}
