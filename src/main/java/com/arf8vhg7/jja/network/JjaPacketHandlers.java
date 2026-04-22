package com.arf8vhg7.jja.network;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaPacketHandlers {
    private JjaPacketHandlers() {
    }

    public static void handleClient(Supplier<Context> contextSupplier, Runnable action) {
        Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isServer()) {
                return;
            }
            action.run();
        });
        context.setPacketHandled(true);
    }

    public static void handleServer(Supplier<Context> contextSupplier, Consumer<ServerPlayer> action) {
        Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            action.accept(player);
        });
        context.setPacketHandled(true);
    }
}
