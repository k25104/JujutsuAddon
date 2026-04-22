package com.arf8vhg7.jja.feature.jja.rct.network;

import com.arf8vhg7.jja.feature.jja.rct.RctBrainService;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaBrainDestructionHoldMessage {
    private final boolean holding;

    public JjaBrainDestructionHoldMessage(boolean holding) {
        this.holding = holding;
    }

    public JjaBrainDestructionHoldMessage(FriendlyByteBuf buffer) {
        this.holding = buffer.readBoolean();
    }

    public static void encode(JjaBrainDestructionHoldMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.holding);
    }

    public static void handle(JjaBrainDestructionHoldMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(contextSupplier, player -> RctBrainService.handleHoldMessage(player, message.holding));
    }
}
