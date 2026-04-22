package com.arf8vhg7.jja.feature.player.revive;

import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerReviveState;
import com.arf8vhg7.jja.feature.player.revive.network.JjaReviveAssistHudMessage;
import com.arf8vhg7.jja.feature.player.revive.network.JjaReviveStateMessage;
import com.arf8vhg7.jja.network.JjaPacketSenders;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;

public final class ReviveSyncService {
    private ReviveSyncService() {
    }

    public static void syncWaitingState(ServerPlayer player) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(player);
        if (reviveState == null) {
            return;
        }
        sendState(
            player,
            reviveState.getReviveRemainingTicks() > 0,
            reviveState.getReviveRemainingTicks(),
            reviveState.getRemainingRevives(),
            JjaReviveSpecialStage.fromId(reviveState.getReviveSpecialStage())
        );
    }

    static void sendState(
        ServerPlayer player,
        boolean waiting,
        int remainingTicks,
        int remainingRevives,
        JjaReviveSpecialStage specialStage
    ) {
        JjaPacketSenders.sendToPlayer(player, new JjaReviveStateMessage(waiting, remainingTicks, remainingRevives, specialStage.id()));
    }

    static void sendAssistHud(ServerPlayer player, boolean active, int remainingTicks, UUID targetId) {
        JjaPacketSenders.sendToPlayer(player, new JjaReviveAssistHudMessage(active, remainingTicks, targetId));
    }
}
