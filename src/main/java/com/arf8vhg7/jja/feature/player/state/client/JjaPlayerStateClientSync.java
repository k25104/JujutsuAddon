package com.arf8vhg7.jja.feature.player.state.client;

import com.arf8vhg7.jja.feature.player.state.JjaPlayerState;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class JjaPlayerStateClientSync {
    private JjaPlayerStateClientSync() {
    }

    public static void apply(CompoundTag data) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        JjaPlayerState state = PlayerStateAccess.get(player);
        if (state != null) {
            state.readNBT(data.copy());
        }
    }
}
