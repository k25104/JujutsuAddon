package com.arf8vhg7.jja.feature.player.revive;

import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerReviveState;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.common.ForgeMod;

public final class ReviveHoldService {
    private static final double DEFAULT_BLOCK_REACH = 4.5D;

    private ReviveHoldService() {
    }

    public static void handleHoldMessage(ServerPlayer rescuer, UUID targetId, boolean holding) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(rescuer);
        if (reviveState == null) {
            return;
        }
        if (!holding) {
            clearOutgoingHold(rescuer, true);
            return;
        }
        reviveState.setReviveHoldActive(true);
        reviveState.setReviveHoldTarget(targetId);
        reviveState.setReviveHoldTicks(0);
    }

    public static void tickRescueHold(ServerPlayer rescuer) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(rescuer);
        if (reviveState == null) {
            return;
        }
        if (!reviveState.isReviveHoldActive()) {
            if (reviveState.getReviveHoldTicks() > 0) {
                resetHoldProgress(rescuer, true);
            }
            return;
        }

        UUID targetId = reviveState.getReviveHoldTarget();
        if (targetId == null) {
            resetHoldProgress(rescuer, true);
            return;
        }

        ServerPlayer target = rescuer.server.getPlayerList().getPlayer(targetId);
        if (!isValidRescueTarget(rescuer, target)) {
            resetHoldProgress(rescuer, true);
            return;
        }

        int holdTicks = reviveState.getReviveHoldTicks() + 1;
        reviveState.setReviveHoldTicks(holdTicks);
        if (holdTicks >= ReviveFlowService.HOLD_REQUIRED_TICKS) {
            ReviveFlowService.clearWaiting(target, true);
            return;
        }
        if (holdTicks == 1 || holdTicks % 20 == 0) {
            ReviveSyncService.sendAssistHud(rescuer, true, ReviveFlowService.HOLD_REQUIRED_TICKS - holdTicks, target.getUUID());
        }
    }

    public static void clearOutgoingHold(ServerPlayer rescuer, boolean hideHud) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(rescuer);
        if (reviveState == null) {
            return;
        }
        boolean hadHud = hideHud
            && (reviveState.getReviveHoldTicks() > 0 || reviveState.isReviveHoldActive() || reviveState.getReviveHoldTarget() != null);
        reviveState.setReviveHoldActive(false);
        reviveState.setReviveHoldTicks(0);
        reviveState.setReviveHoldTarget(null);
        if (hadHud) {
            ReviveSyncService.sendAssistHud(rescuer, false, 0, null);
        }
    }

    public static void clearIncomingRescuers(ServerPlayer target) {
        MinecraftServer server = target.getServer();
        if (server == null) {
            return;
        }
        UUID targetId = target.getUUID();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerReviveState reviveState = PlayerStateAccess.revive(player);
            if (reviveState == null) {
                continue;
            }
            if (Objects.equals(reviveState.getReviveHoldTarget(), targetId)) {
                clearOutgoingHold(player, true);
            }
        }
    }

    private static void resetHoldProgress(ServerPlayer rescuer, boolean hideHud) {
        PlayerReviveState reviveState = PlayerStateAccess.revive(rescuer);
        if (reviveState == null) {
            return;
        }
        boolean hadProgress = reviveState.getReviveHoldTicks() > 0;
        reviveState.setReviveHoldTicks(0);
        if (hideHud && hadProgress) {
            ReviveSyncService.sendAssistHud(rescuer, false, 0, null);
        }
    }

    private static boolean isValidRescueTarget(ServerPlayer rescuer, ServerPlayer target) {
        if (target == null || target == rescuer || target.level() != rescuer.level()) {
            return false;
        }
        if (!ReviveFlowService.isWaiting(target) || target.isSpectator()) {
            return false;
        }
        if (!rescuer.hasLineOfSight(target)) {
            return false;
        }

        double blockReach = DEFAULT_BLOCK_REACH;
        AttributeInstance blockReachAttribute = rescuer.getAttribute(ForgeMod.BLOCK_REACH.get());
        if (blockReachAttribute != null) {
            blockReach = blockReachAttribute.getValue();
        }
        return rescuer.getEyePosition().distanceToSqr(target.getEyePosition()) <= blockReach * blockReach;
    }
}
