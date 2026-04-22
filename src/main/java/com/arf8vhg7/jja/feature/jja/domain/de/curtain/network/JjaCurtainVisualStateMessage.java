package com.arf8vhg7.jja.feature.jja.domain.de.curtain.network;

import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainPhase;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainShellVisibilityOverride;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainVisualState;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.client.CurtainClientState;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaCurtainVisualStateMessage {
    private final List<CurtainVisualState> states;
    private final CurtainShellVisibilityOverride shellVisibilityOverride;

    public JjaCurtainVisualStateMessage(List<CurtainVisualState> states, CurtainShellVisibilityOverride shellVisibilityOverride) {
        this.states = List.copyOf(states);
        this.shellVisibilityOverride = shellVisibilityOverride;
    }

    public JjaCurtainVisualStateMessage(FriendlyByteBuf buffer) {
        this.shellVisibilityOverride = buffer.readEnum(CurtainShellVisibilityOverride.class);
        int size = buffer.readVarInt();
        List<CurtainVisualState> decoded = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            decoded.add(
                new CurtainVisualState(
                    buffer.readUUID(),
                    buffer.readResourceLocation(),
                    new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
                    buffer.readVarInt(),
                    buffer.readEnum(CurtainPhase.class),
                    buffer.readBoolean()
                )
            );
        }
        this.states = List.copyOf(decoded);
    }

    public static void encode(JjaCurtainVisualStateMessage message, FriendlyByteBuf buffer) {
        buffer.writeEnum(message.shellVisibilityOverride);
        buffer.writeVarInt(message.states.size());
        for (CurtainVisualState state : message.states) {
            buffer.writeUUID(state.ownerId());
            buffer.writeResourceLocation(state.dimensionId());
            buffer.writeDouble(state.center().x);
            buffer.writeDouble(state.center().y);
            buffer.writeDouble(state.center().z);
            buffer.writeVarInt(state.radius());
            buffer.writeEnum(state.phase());
            buffer.writeBoolean(state.localViewerPassThrough());
        }
    }

    public static void handle(JjaCurtainVisualStateMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleClient(
            contextSupplier,
            () -> CurtainClientState.applySnapshot(message.states, message.shellVisibilityOverride)
        );
    }
}
