package com.arf8vhg7.jja.feature.player.revive.network;

import com.arf8vhg7.jja.feature.player.revive.client.JjaReviveClientState;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaReviveStateMessage {
    private final boolean waiting;
    private final int remainingTicks;
    private final int remainingRevives;
    private final int specialStage;

    public JjaReviveStateMessage(boolean waiting, int remainingTicks, int remainingRevives, int specialStage) {
        this.waiting = waiting;
        this.remainingTicks = remainingTicks;
        this.remainingRevives = remainingRevives;
        this.specialStage = specialStage;
    }

    public JjaReviveStateMessage(FriendlyByteBuf buffer) {
        this.waiting = buffer.readBoolean();
        this.remainingTicks = buffer.readInt();
        this.remainingRevives = buffer.readInt();
        this.specialStage = buffer.readInt();
    }

    public static void encode(JjaReviveStateMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.waiting);
        buffer.writeInt(message.remainingTicks);
        buffer.writeInt(message.remainingRevives);
        buffer.writeInt(message.specialStage);
    }

    public static void handle(JjaReviveStateMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleClient(
            contextSupplier,
            () -> JjaReviveClientState.applyWaitingState(
                message.waiting,
                message.remainingTicks,
                message.remainingRevives,
                message.specialStage
            )
        );
    }
}
