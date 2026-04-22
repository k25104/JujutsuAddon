package com.arf8vhg7.jja.feature.jja.technique.shared.menu.network;

import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateLifecycleService;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupService;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupViewState;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import com.arf8vhg7.jja.network.JjaPacketCodecs;
import com.arf8vhg7.jja.network.JjaPacketSenders;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaTechniqueSetupOpenMessage {
    public JjaTechniqueSetupOpenMessage() {
    }

    public JjaTechniqueSetupOpenMessage(FriendlyByteBuf buffer) {
        JjaPacketCodecs.decodeEmpty(buffer);
    }

    public static void encode(JjaTechniqueSetupOpenMessage message, FriendlyByteBuf buffer) {
        JjaPacketCodecs.encodeEmpty(buffer);
    }

    public static void handle(JjaTechniqueSetupOpenMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(contextSupplier, player -> {
            JjaPlayerStateLifecycleService.ensureTechniqueSetupAndSync(player);
            TechniqueSetupViewState viewState = TechniqueSetupService.buildViewState(player);
            JjaPacketSenders.sendToPlayer(player, new JjaTechniqueSetupStateMessage(true, viewState));
        });
    }
}
