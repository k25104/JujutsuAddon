package com.arf8vhg7.jja.feature.player.revive.network;

import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import com.arf8vhg7.jja.feature.player.revive.ReviveHoldService;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaReviveHoldMessage {
    private final UUID targetId;
    private final boolean holding;

    public JjaReviveHoldMessage(UUID targetId, boolean holding) {
        this.targetId = targetId;
        this.holding = holding;
    }

    public JjaReviveHoldMessage(FriendlyByteBuf buffer) {
        this.targetId = buffer.readUUID();
        this.holding = buffer.readBoolean();
    }

    public static void encode(JjaReviveHoldMessage message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.targetId);
        buffer.writeBoolean(message.holding);
    }

    public static void handle(JjaReviveHoldMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(
            contextSupplier,
            player -> ReviveHoldService.handleHoldMessage(player, message.targetId, message.holding)
        );
    }
}
