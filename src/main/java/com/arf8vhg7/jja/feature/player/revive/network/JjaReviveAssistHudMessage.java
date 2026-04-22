package com.arf8vhg7.jja.feature.player.revive.network;

import com.arf8vhg7.jja.feature.player.revive.client.JjaReviveClientState;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaReviveAssistHudMessage {
    private final boolean active;
    private final int remainingTicks;
    private final UUID targetId;

    public JjaReviveAssistHudMessage(boolean active, int remainingTicks, UUID targetId) {
        this.active = active;
        this.remainingTicks = remainingTicks;
        this.targetId = targetId;
    }

    public JjaReviveAssistHudMessage(FriendlyByteBuf buffer) {
        this.active = buffer.readBoolean();
        this.remainingTicks = buffer.readInt();
        this.targetId = buffer.readBoolean() ? buffer.readUUID() : null;
    }

    public static void encode(JjaReviveAssistHudMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.active);
        buffer.writeInt(message.remainingTicks);
        buffer.writeBoolean(message.targetId != null);
        if (message.targetId != null) {
            buffer.writeUUID(message.targetId);
        }
    }

    public static void handle(JjaReviveAssistHudMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleClient(
            contextSupplier,
            () -> JjaReviveClientState.applyAssistState(message.active, message.remainingTicks, message.targetId)
        );
    }
}
