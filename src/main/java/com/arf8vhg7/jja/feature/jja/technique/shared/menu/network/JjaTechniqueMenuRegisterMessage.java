package com.arf8vhg7.jja.feature.jja.technique.shared.menu.network;

import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaTechniqueMenuRegisterMessage {
    private final int slot;
    private final int selectTechniqueId;
    private final String canonicalName;

    public JjaTechniqueMenuRegisterMessage(int slot, int selectTechniqueId, String canonicalName) {
        this.slot = slot;
        this.selectTechniqueId = selectTechniqueId;
        this.canonicalName = canonicalName;
    }

    public JjaTechniqueMenuRegisterMessage(FriendlyByteBuf buffer) {
        this.slot = buffer.readVarInt();
        this.selectTechniqueId = buffer.readVarInt();
        this.canonicalName = buffer.readUtf();
    }

    public static void encode(JjaTechniqueMenuRegisterMessage message, FriendlyByteBuf buffer) {
        buffer.writeVarInt(message.slot);
        buffer.writeVarInt(message.selectTechniqueId);
        buffer.writeUtf(Objects.requireNonNull(message.canonicalName, "canonicalName"));
    }

    public static void handle(JjaTechniqueMenuRegisterMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(contextSupplier, player -> {
            RegisteredCurseTechniqueSlots.save(player, message.slot, message.selectTechniqueId, message.canonicalName);
        });
    }
}