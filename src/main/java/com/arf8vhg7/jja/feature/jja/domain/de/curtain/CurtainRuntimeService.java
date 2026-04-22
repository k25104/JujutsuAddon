package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModBlocks;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables.PlayerVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class CurtainRuntimeService {
    private static final Map<UUID, CurtainSession> ACTIVE_SESSIONS = new LinkedHashMap<>();
    private static final Map<UUID, CurtainShellVisibilityOverride> SHELL_VISIBILITY_OVERRIDES = new LinkedHashMap<>();

    private CurtainRuntimeService() {
    }

    public static boolean use(ServerPlayer owner) {
        CurtainSession existing = ACTIVE_SESSIONS.get(owner.getUUID());
        if (existing != null) {
            removeSession(owner.server, existing);
            return true;
        }

        int radius = resolveConfiguredRadius(owner);
        double cursePowerCost = CurtainCostRules.resolveCursePowerCost(radius);
        PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(owner);
        double currentCursePower = playerVariables != null ? playerVariables.PlayerCursePower : 0.0D;
        double effectiveCursePower = JjaCursePowerAccountingService.resolveEffectivePower(playerVariables, currentCursePower);
        if (playerVariables == null || effectiveCursePower < cursePowerCost) {
            return false;
        }

        Vec3 center = new Vec3(owner.getX(), owner.getY(), owner.getZ());
        CurtainSession session = new CurtainSession(
            owner.getUUID(),
            owner.level().dimension(),
            center,
            radius,
            cursePowerCost,
            CurtainGeometry.buildTopDownShellSlices(center, radius)
        );
        ACTIVE_SESSIONS.put(owner.getUUID(), session);
        sendOwnerMessage(owner, "message.jja.curtain.chant_1");
        return true;
    }

    public static void tick(@Nullable MinecraftServer server) {
        if (server == null || ACTIVE_SESSIONS.isEmpty()) {
            return;
        }

        for (CurtainSession session : new ArrayList<>(ACTIVE_SESSIONS.values())) {
            ServerPlayer owner = server.getPlayerList().getPlayer(session.ownerId());
            if (owner == null || !owner.isAlive() || owner.level().dimension() != session.dimension()) {
                removeSession(server, session);
                continue;
            }

            switch (session.phase()) {
                case CHANT_1 -> tickFirstChant(owner, session);
                case CHANT_2 -> tickSecondChant(owner, session);
                case CHANT_3 -> tickThirdChant(owner, session);
                case BUILDING -> tickBuilding(owner.serverLevel(), session);
                case ACTIVE -> {
                }
            }
        }
    }

    public static void clearOwner(@Nullable MinecraftServer server, @Nullable UUID ownerId) {
        if (ownerId == null) {
            return;
        }

        CurtainSession session = ACTIVE_SESSIONS.get(ownerId);
        if (session != null) {
            removeSession(server, session);
        }
    }

    public static void clearAll(@Nullable MinecraftServer server) {
        for (CurtainSession session : new ArrayList<>(ACTIVE_SESSIONS.values())) {
            removeSession(server, session);
        }
        SHELL_VISIBILITY_OVERRIDES.clear();
    }

    public static boolean toggleShellVisibility(ServerPlayer viewer) {
        CurtainShellVisibilityOverride nextOverride =
            CurtainShellVisibilityOverride.toggleFromEffectiveVisibility(resolveShellVisionMode(viewer) == CurtainShellVisionMode.BLACK);
        SHELL_VISIBILITY_OVERRIDES.put(viewer.getUUID(), nextOverride);
        return nextOverride == CurtainShellVisibilityOverride.VISIBLE;
    }

    public static CurtainShellVisibilityOverride getShellVisibilityOverride(@Nullable ServerPlayer viewer) {
        if (viewer == null) {
            return CurtainShellVisibilityOverride.AUTO;
        }
        return SHELL_VISIBILITY_OVERRIDES.getOrDefault(viewer.getUUID(), CurtainShellVisibilityOverride.AUTO);
    }

    public static void clearViewerState(@Nullable UUID viewerId) {
        if (viewerId != null) {
            SHELL_VISIBILITY_OVERRIDES.remove(viewerId);
        }
    }

    public static void onAttackTargetChanged(@Nullable MinecraftServer server) {
        if (server == null || ACTIVE_SESSIONS.values().stream().noneMatch(session -> session.phase().isClientRelevant())) {
            return;
        }
        CurtainSyncService.syncAllPlayers(server);
    }

    public static boolean canEntityPassThroughShell(@Nullable BlockGetter level, @Nullable BlockPos pos, @Nullable Entity entity) {
        if (!(level instanceof Level runtimeLevel) || pos == null || entity == null) {
            return false;
        }

        if (runtimeLevel.isClientSide()) {
            return com.arf8vhg7.jja.feature.jja.domain.de.curtain.client.CurtainClientState.canLocalPlayerPassThroughShell(entity, pos);
        }

        CurtainSession session = findSession((ServerLevel) runtimeLevel, pos);
        if (session == null) {
            return false;
        }

        if (!(entity instanceof Player player)) {
            return false;
        }

        boolean owner = player.getUUID().equals(session.ownerId());
        boolean allowlisted = isAllowlistedViewer((ServerLevel) runtimeLevel, session.ownerId(), player);
        return CurtainPassThroughRules.canPassShell(true, owner, allowlisted, CurtainViewerRules.isCompletePhysicalGifted(player));
    }

    public static boolean shouldBlockBarrierBreak(@Nullable LevelAccessor level, @Nullable BlockPos pos, @Nullable Entity sourceEntity) {
        if (!(level instanceof ServerLevel serverLevel) || pos == null || sourceEntity == null) {
            return false;
        }

        Entity actor = JjaJujutsucraftDataAccess.jjaResolveRootLivingOwner(serverLevel, sourceEntity);
        if (actor == null) {
            actor = sourceEntity;
        }

        for (CurtainSession session : ACTIVE_SESSIONS.values()) {
            if (session.dimension() != serverLevel.dimension() || !session.hasPlacedShellPosition(pos)) {
                continue;
            }
            return CurtainGeometry.isWithinCurtain(session.center(), session.radius(), actor);
        }
        return false;
    }

    static Collection<CurtainSession> activeSessions() {
        return ACTIVE_SESSIONS.values();
    }

    private static void tickFirstChant(ServerPlayer owner, CurtainSession session) {
        if (session.incrementChantTicksElapsed() != 20) {
            return;
        }
        session.setPhase(CurtainPhase.CHANT_2);
        sendOwnerMessage(owner, "message.jja.curtain.chant_2");
    }

    private static void tickSecondChant(ServerPlayer owner, CurtainSession session) {
        if (session.incrementChantTicksElapsed() != 40) {
            return;
        }
        session.setPhase(CurtainPhase.CHANT_3);
        sendOwnerMessage(owner, "message.jja.curtain.chant_3");
    }

    private static void tickThirdChant(ServerPlayer owner, CurtainSession session) {
        if (session.incrementChantTicksElapsed() != 60) {
            return;
        }

        PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(owner);
        if (playerVariables == null) {
            removeSession(owner.server, session);
            return;
        }

        JjaCursePowerAccountingService.queueSpentPower(playerVariables, session.cursePowerCost());
        playerVariables.syncPlayerVariables(owner);

        session.setPhase(CurtainPhase.BUILDING);
        CurtainSyncService.syncAllPlayers(owner.server);
        tickBuilding(owner.serverLevel(), session);
    }

    private static void tickBuilding(ServerLevel level, CurtainSession session) {
        if (!session.hasRemainingShellSlices()) {
            if (session.phase() != CurtainPhase.ACTIVE) {
                session.setPhase(CurtainPhase.ACTIVE);
                CurtainSyncService.syncAllPlayers(level.getServer());
            }
            return;
        }

        for (BlockPos pos : session.takeNextShellSlice()) {
            placeShell(level, session, pos);
        }

        if (!session.hasRemainingShellSlices()) {
            session.setPhase(CurtainPhase.ACTIVE);
            CurtainSyncService.syncAllPlayers(level.getServer());
        }
    }

    private static void placeShell(ServerLevel level, CurtainSession session, BlockPos pos) {
        BlockState currentState = level.getBlockState(pos);
        if (!CurtainPlacementRules.canReplaceShellBlock(level, pos, currentState)) {
            return;
        }

        session.trackReplacement(pos, currentState);
        level.setBlock(pos, CurtainBlocks.CURTAIN_SHELL.get().defaultBlockState(), 3);
        session.markPlaced(pos);
    }

    private static void removeSession(@Nullable MinecraftServer server, CurtainSession session) {
        ACTIVE_SESSIONS.remove(session.ownerId());
        if (server == null) {
            return;
        }

        ServerLevel level = server.getLevel(session.dimension());
        if (level != null) {
            for (Map.Entry<BlockPos, BlockState> entry : session.replacedStates().entrySet()) {
                BlockPos pos = entry.getKey();
                BlockState currentState = level.getBlockState(pos);
                if (!currentState.is(CurtainBlocks.CURTAIN_SHELL.get()) && !currentState.is(JujutsucraftModBlocks.DOMAIN_HOLE.get())) {
                    continue;
                }
                level.setBlock(pos, Objects.requireNonNullElse(entry.getValue(), Blocks.AIR.defaultBlockState()), 3);
            }
        }

        if (session.phase().isClientRelevant()) {
            CurtainSyncService.syncAllPlayers(server);
        }
    }

    @Nullable
    private static CurtainSession findSession(ServerLevel level, BlockPos pos) {
        for (CurtainSession session : ACTIVE_SESSIONS.values()) {
            if (session.dimension() != level.dimension()) {
                continue;
            }
            if (session.hasPlacedShellPosition(pos)) {
                return session;
            }
        }
        return null;
    }

    private static int resolveConfiguredRadius(ServerPlayer owner) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(owner);
        return addonStats == null ? 44 : addonStats.getCurtainRadius();
    }

    private static boolean isAllowlistedViewer(ServerLevel level, UUID ownerId, Player viewer) {
        if (viewer.getUUID().equals(ownerId)) {
            return true;
        }

        Entity ownerEntity = level.getPlayerByUUID(ownerId);
        if (!(ownerEntity instanceof Player ownerPlayer)) {
            return false;
        }

        PlayerRctState rctState = PlayerStateAccess.rct(ownerPlayer);
        return rctState != null && rctState.hasAttackTarget(viewer.getUUID());
    }

    private static void sendOwnerMessage(ServerPlayer owner, String messageKey) {
        owner.displayClientMessage(Component.translatable(messageKey).withStyle(ChatFormatting.BOLD), false);
    }

    private static CurtainShellVisionMode resolveShellVisionMode(ServerPlayer viewer) {
        return getShellVisibilityOverride(viewer).apply(CurtainViewerRules.resolveShellVisionMode(viewer));
    }
}
