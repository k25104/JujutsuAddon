package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

import com.arf8vhg7.jja.feature.jja.domain.de.curtain.network.JjaCurtainVisualStateMessage;
import com.arf8vhg7.jja.network.JjaPacketSenders;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class CurtainSyncService {
    private CurtainSyncService() {
    }

    public static void syncAllPlayers(@Nullable MinecraftServer server) {
        if (server == null) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            syncToPlayer(player);
        }
    }

    public static void syncToPlayer(@Nullable ServerPlayer player) {
        if (player == null) {
            return;
        }

        JjaPacketSenders.sendToPlayer(
            player,
            new JjaCurtainVisualStateMessage(createSnapshot(player), CurtainRuntimeService.getShellVisibilityOverride(player))
        );
    }

    private static List<CurtainVisualState> createSnapshot(ServerPlayer viewer) {
        return CurtainRuntimeService.activeSessions()
            .stream()
            .filter(session -> session.phase().isClientRelevant())
            .sorted(Comparator.comparing(CurtainSession::ownerId))
            .map(
                session -> new CurtainVisualState(
                    session.ownerId(),
                    session.dimension().location(),
                    session.center(),
                    session.radius(),
                    session.phase(),
                    isAllowlistedViewer(viewer, session.ownerId())
                )
            )
            .toList();
    }

    private static boolean isAllowlistedViewer(ServerPlayer viewer, UUID ownerId) {
        if (viewer.getUUID().equals(ownerId)) {
            return true;
        }

        Entity ownerEntity = viewer.serverLevel().getServer().getPlayerList().getPlayer(ownerId);
        if (!(ownerEntity instanceof Player ownerPlayer)) {
            return false;
        }

        PlayerRctState rctState = PlayerStateAccess.rct(ownerPlayer);
        return rctState != null && rctState.hasAttackTarget(viewer.getUUID());
    }
}
