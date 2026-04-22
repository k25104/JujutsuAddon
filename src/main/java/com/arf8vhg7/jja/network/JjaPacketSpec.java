package com.arf8vhg7.jja.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public record JjaPacketSpec<T>(
    Class<T> messageType,
    BiConsumer<T, FriendlyByteBuf> encoder,
    Function<FriendlyByteBuf, T> decoder,
    BiConsumer<T, Supplier<Context>> handler
) {
}
