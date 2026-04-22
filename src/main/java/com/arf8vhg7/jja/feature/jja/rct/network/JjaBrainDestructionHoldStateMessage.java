package com.arf8vhg7.jja.feature.jja.rct.network;

import com.arf8vhg7.jja.feature.jja.rct.client.RctClientEvents;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaBrainDestructionHoldStateMessage {
    private final boolean holding;

    public JjaBrainDestructionHoldStateMessage(boolean holding) {
        this.holding = holding;
    }

    public JjaBrainDestructionHoldStateMessage(FriendlyByteBuf buffer) {
        this.holding = buffer.readBoolean();
    }

    public static void encode(JjaBrainDestructionHoldStateMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.holding);
    }

    public static void handle(JjaBrainDestructionHoldStateMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleClient(contextSupplier, () -> RctClientEvents.applyBrainDestructionHoldState(message.holding));
    }
}
