package com.arf8vhg7.jja.feature.player.state;

import com.arf8vhg7.jja.feature.player.state.network.JjaPlayerStateSyncMessage;
import com.arf8vhg7.jja.network.JjaPacketSenders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class JjaPlayerStateSync {
    private JjaPlayerStateSync() {
    }

    public static void sync(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            sync(serverPlayer);
        }
    }

    public static void sync(ServerPlayer player) {
        JjaPlayerState state = PlayerStateAccess.get(player);
        if (state == null) {
            return;
        }
        JjaPacketSenders.sendToPlayer(player, new JjaPlayerStateSyncMessage(createSyncTag(state)));
    }

    static CompoundTag createSyncTag(JjaPlayerState state) {
        CompoundTag syncTag = state.writeNBT();
        // Keep the slot-selection input local so delayed packets never restore stale key state.
        syncTag.putInt(JjaPlayerStateSchema.NBT_KEY_PRESSED_SLOT, 0);
        return syncTag;
    }
}
