package com.arf8vhg7.jja.feature.player.revive.network;

import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import com.arf8vhg7.jja.network.JjaPacketCodecs;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaReviveGiveUpMessage {
    public JjaReviveGiveUpMessage() {
    }

    public JjaReviveGiveUpMessage(FriendlyByteBuf buffer) {
        JjaPacketCodecs.decodeEmpty(buffer);
    }

    public static void encode(JjaReviveGiveUpMessage message, FriendlyByteBuf buffer) {
        JjaPacketCodecs.encodeEmpty(buffer);
    }

    public static void handle(JjaReviveGiveUpMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(contextSupplier, ReviveFlowService::handleGiveUp);
    }
}
