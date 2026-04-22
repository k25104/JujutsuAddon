package com.arf8vhg7.jja.feature.player.state.network;

import com.arf8vhg7.jja.feature.player.state.client.JjaPlayerStateClientSync;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaPlayerStateSyncMessage {
    private final CompoundTag data;

    public JjaPlayerStateSyncMessage(CompoundTag data) {
        this.data = data.copy();
    }

    public JjaPlayerStateSyncMessage(FriendlyByteBuf buffer) {
        CompoundTag read = buffer.readNbt();
        this.data = read == null ? new CompoundTag() : read;
    }

    public static void encode(JjaPlayerStateSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeNbt(message.data);
    }

    public static void handle(JjaPlayerStateSyncMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleClient(contextSupplier, () -> JjaPlayerStateClientSync.apply(message.data));
    }
}
