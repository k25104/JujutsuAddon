package com.arf8vhg7.jja.network;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public final class JjaPacketSenders {
    private JjaPacketSenders() {
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        JjaNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToTrackingEntityAndSelf(Entity entity, Object message) {
        JjaNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }
}
