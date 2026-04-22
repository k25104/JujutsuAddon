package com.arf8vhg7.jja.feature.jja.domain.de.network;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionRadiusRuntime;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaDomainRadiusAdjustMessage {
    private final int direction;

    public JjaDomainRadiusAdjustMessage(Direction direction) {
        this.direction = direction.ordinal();
    }

    public JjaDomainRadiusAdjustMessage(FriendlyByteBuf buffer) {
        this.direction = buffer.readInt();
    }

    public static void encode(JjaDomainRadiusAdjustMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.direction);
    }

    public static void handle(JjaDomainRadiusAdjustMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(contextSupplier, player -> {
            Direction direction = Direction.fromOrdinal(message.direction);
            if (direction == null) {
                return;
            }

            switch (direction) {
                case EXPAND -> DomainExpansionRadiusRuntime.expandActiveRadius(player);
                case SHRINK -> DomainExpansionRadiusRuntime.shrinkActiveRadius(player);
            }
        });
    }

    public enum Direction {
        EXPAND,
        SHRINK;

        private static Direction fromOrdinal(int ordinal) {
            Direction[] values = values();
            return ordinal >= 0 && ordinal < values.length ? values[ordinal] : null;
        }
    }
}
