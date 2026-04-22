package com.arf8vhg7.jja.feature.player.revive.network;

import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import com.arf8vhg7.jja.network.JjaPacketCodecs;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaReviveCoreClickMessage {
    public JjaReviveCoreClickMessage() {
    }

    public JjaReviveCoreClickMessage(FriendlyByteBuf buffer) {
        JjaPacketCodecs.decodeEmpty(buffer);
    }

    public static void encode(JjaReviveCoreClickMessage message, FriendlyByteBuf buffer) {
        JjaPacketCodecs.encodeEmpty(buffer);
    }

    public static void handle(JjaReviveCoreClickMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(contextSupplier, ReviveFlowService::handleCoreClick);
    }
}
