package com.arf8vhg7.jja.feature.jja.traits.twinnedbody;

import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.network.JjaTwinnedBodyStateMessage;
import com.arf8vhg7.jja.network.JjaPacketSenders;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class TwinnedBodySyncService {
    private TwinnedBodySyncService() {
    }

    public static void syncTrackingState(@Nullable Entity entity) {
        if (entity == null) {
            return;
        }

        JjaPacketSenders.sendToTrackingEntityAndSelf(
            entity,
            new JjaTwinnedBodyStateMessage(entity.getUUID(), TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(entity))
        );
    }

    public static void sendTrackingState(@Nullable ServerPlayer player, @Nullable Entity target) {
        if (player == null || target == null) {
            return;
        }

        JjaPacketSenders.sendToPlayer(
            player,
            new JjaTwinnedBodyStateMessage(target.getUUID(), TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(target))
        );
    }

    public static void clearTrackingState(@Nullable ServerPlayer player, @Nullable Entity target) {
        if (player == null || target == null) {
            return;
        }

        JjaPacketSenders.sendToPlayer(player, new JjaTwinnedBodyStateMessage(target.getUUID(), false));
    }
}
