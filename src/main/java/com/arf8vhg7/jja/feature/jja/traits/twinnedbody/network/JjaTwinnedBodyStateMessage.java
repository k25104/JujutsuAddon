package com.arf8vhg7.jja.feature.jja.traits.twinnedbody.network;

import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client.TwinnedBodyClientState;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.UUID;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaTwinnedBodyStateMessage {
    private final UUID entityId;
    private final boolean active;

    public JjaTwinnedBodyStateMessage(UUID entityId, boolean active) {
        this.entityId = Objects.requireNonNull(entityId);
        this.active = active;
    }

    public JjaTwinnedBodyStateMessage(FriendlyByteBuf buffer) {
        this.entityId = buffer.readUUID();
        this.active = buffer.readBoolean();
    }

    public static void encode(JjaTwinnedBodyStateMessage message, FriendlyByteBuf buffer) {
        buffer.writeUUID(Objects.requireNonNull(message.entityId));
        buffer.writeBoolean(message.active);
    }

    public static void handle(JjaTwinnedBodyStateMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleClient(contextSupplier, () -> TwinnedBodyClientState.apply(message.entityId, message.active));
    }
}
