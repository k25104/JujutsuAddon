package com.arf8vhg7.jja.feature.jja.technique.shared.menu.network;

import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupViewState;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.client.TechniqueSetupClientState;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaTechniqueSetupStateMessage {
    private final boolean openScreen;
    private final TechniqueSetupViewState viewState;

    public JjaTechniqueSetupStateMessage(boolean openScreen, TechniqueSetupViewState viewState) {
        this.openScreen = openScreen;
        this.viewState = viewState;
    }

    public JjaTechniqueSetupStateMessage(FriendlyByteBuf buffer) {
        this.openScreen = buffer.readBoolean();
        this.viewState = TechniqueSetupViewState.read(buffer);
    }

    public static void encode(JjaTechniqueSetupStateMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.openScreen);
        message.viewState.write(buffer);
    }

    public static void handle(JjaTechniqueSetupStateMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleClient(contextSupplier, () -> TechniqueSetupClientState.apply(message.openScreen, message.viewState));
    }
}
