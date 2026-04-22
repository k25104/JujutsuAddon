package com.arf8vhg7.jja.feature.jja.technique.family.naoya;

import com.arf8vhg7.jja.util.JjaCommandHelper;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.mcreator.jujutsucraft.JujutsucraftMod;
import net.mcreator.jujutsucraft.entity.FrameEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModEntities;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.AIProjectionSorceryProcedure;
import net.mcreator.jujutsucraft.procedures.GetEntityFromUUIDProcedure;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class NaoyaProjectionSorceryService {
    private static final String KEY_FAST_FRAME_GENERATION = "jja_fast_frame_generation";
    private static final String KEY_FAST_FRAME_GENERATION_READY = "jja_fast_frame_generation_ready";
    private static final String KEY_OWNER_UUID = "OWNER_UUID";
    private static final String KEY_CNT1 = "cnt1";
    private static final String KEY_CNT2 = "cnt2";
    private static final String KEY_CNT4 = "cnt4";
    private static final String KEY_CNT5 = "cnt5";
    private static final String KEY_SKILL = "skill";
    private static final String KEY_CNT3 = "cnt3";
    private static final String KEY_CNT7 = "cnt7";
    private static final String KEY_CNT10 = "cnt10";
    private static final String KEY_PRESS_Z = "PRESS_Z";
    private static final String KEY_X_POS = "x_pos";
    private static final String KEY_Y_POS = "y_pos";
    private static final String KEY_Z_POS = "z_pos";

    private static final int PROJECTION_FRAME_MAX_COUNT = 20;
    private static final int LOOK_SYNC_DELAY_TICKS = 1;
    private static final int OWNER_TARGET_UPDATES_PER_STEP = 2;
    private static final double PROJECTION_MAX_TRACE_DISTANCE = 64.0D;
    private static final double FRAME_SEARCH_HORIZONTAL_RADIUS = 1024.0D;
    private static final double FRAME_SEARCH_VERTICAL_RADIUS = 384.0D;
    private static final double MIN_HORIZONTAL_VECTOR_SQR = 1.0E-6D;
    private static final double TARGET_DISTANCE_EPSILON = 1.0E-4D;
    private static final int TOP_SPEED_PUNCH_SKIP_SPEED_AMPLIFIER = 32;
    private static final double TOP_SPEED_PUNCH_IMMEDIATE_COUNTER = 21.0D;
    private static final int NEAREST_SURFACE_SCAN_LIMIT = 48;

    private static final Map<UUID, Long> LAST_ACCELERATION_TICK_BY_OWNER = new HashMap<>();
    private static boolean acceleratingProjectionFrames;

    private NaoyaProjectionSorceryService() {
    }

    public static boolean toggleFastFrameGeneration(@Nullable ServerPlayer player) {
        if (player == null) {
            return false;
        }
        boolean enabled = !isFastFrameGenerationEnabled(player);
        player.getPersistentData().putBoolean(KEY_FAST_FRAME_GENERATION, enabled);
        player.getPersistentData().putBoolean(KEY_FAST_FRAME_GENERATION_READY, false);
        return enabled;
    }

    public static boolean isFastFrameGenerationEnabled(@Nullable Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(KEY_FAST_FRAME_GENERATION);
    }

    public static boolean shouldApplyTopSpeedMovement(@Nullable LevelAccessor world) {
        return !(world instanceof Level level && level.isClientSide());
    }

    public static Vec3 resolveTopSpeedVelocity(@Nullable LevelAccessor world, Vec3 originalVelocity) {
        return originalVelocity;
    }

    public static double resolveTopSpeedPunchChargeCounter(@Nullable Entity entity, String key, double nextCounterValue) {
        if (!KEY_CNT10.equals(key) || entity == null) {
            return nextCounterValue;
        }
        int speedAmplifier = entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.MOVEMENT_SPEED)
            ? Objects.requireNonNull(livingEntity.getEffect(MobEffects.MOVEMENT_SPEED)).getAmplifier()
            : -1;
        return shouldSkipTopSpeedPunchCharge(
            entity instanceof Player,
            entity.getPersistentData().getDouble(KEY_CNT10),
            entity.isSprinting(),
            speedAmplifier
        )
            ? TOP_SPEED_PUNCH_IMMEDIATE_COUNTER
            : nextCounterValue;
    }

    static boolean shouldSkipTopSpeedPunchCharge(boolean isPlayer, double currentCounter, boolean isSprinting, int speedAmplifier) {
        if (!isPlayer || currentCounter != 0.0D || !isSprinting) {
            return false;
        }
        return speedAmplifier > TOP_SPEED_PUNCH_SKIP_SPEED_AMPLIFIER;
    }

    public static void accelerateProjectionFrames(@Nullable LevelAccessor world, @Nullable Entity projectionFrame) {
        if (!(world instanceof ServerLevel level) || projectionFrame == null || acceleratingProjectionFrames) {
            return;
        }
        @Nullable ServerPlayer owner = resolveProjectionOwner(level, projectionFrame);
        if (owner == null || !isFastFrameGenerationEnabled(owner)) {
            return;
        }
        Vec3 origin = owner.getEyePosition(1.0F);
        Vec3 direction = owner.getViewVector(1.0F);
        if (direction.lengthSqr() <= MIN_HORIZONTAL_VECTOR_SQR) {
            return;
        }
        Vec3 normalizedDirection = direction.normalize();
        double maxDistance = resolveProjectionTargetDistance(level, owner, origin, normalizedDirection);
        if (maxDistance <= 0.0D) {
            return;
        }

        long gameTime = level.getGameTime();
        UUID ownerId = owner.getUUID();
        if (LAST_ACCELERATION_TICK_BY_OWNER.getOrDefault(ownerId, Long.MIN_VALUE) == gameTime) {
            return;
        }
        LAST_ACCELERATION_TICK_BY_OWNER.put(ownerId, gameTime);
        pruneOldAccelerationEntries(gameTime);

        boolean frameGenerationCompleted = false;
        acceleratingProjectionFrames = true;
        try {
            frameGenerationCompleted = runFastProjectionFrameChain(level, owner, origin, normalizedDirection, maxDistance);
        } finally {
            acceleratingProjectionFrames = false;
        }

        jjaUpdateFastFrameGenerationReadiness(owner, frameGenerationCompleted);
    }

    private static boolean runFastProjectionFrameChain(
        ServerLevel level,
        ServerPlayer owner,
        Vec3 origin,
        Vec3 direction,
        double maxDistance
    ) {
        @Nullable Entity currentFrame = findFirstPendingProjectionFrame(level, owner);
        if (currentFrame == null || !currentFrame.isAlive()) {
            return false;
        }

        for (int i = 0; i < PROJECTION_FRAME_MAX_COUNT; i++) {
            if (!currentFrame.isAlive()) {
                break;
            }

            boolean hasRemainingDistance = advanceOwnerProjectionTarget(owner, origin, direction, maxDistance);

            AIProjectionSorceryProcedure.execute(level, currentFrame);
            Vec3 successorSearchCenter = Objects.requireNonNull(currentFrame.position());

            int currentFrameCount = (int) Math.round(currentFrame.getPersistentData().getDouble(KEY_CNT5));
            if (currentFrameCount >= PROJECTION_FRAME_MAX_COUNT || !hasRemainingDistance) {
                break;
            }

            @Nullable Entity nextFrame = findProjectionSuccessor(level, owner, successorSearchCenter, currentFrameCount + 1);
            if (nextFrame == null) {
                break;
            }

            currentFrame = nextFrame;
        }

        snapProjectionFramesToSurface(level, owner);
        return true;
    }

    private static void jjaUpdateFastFrameGenerationReadiness(ServerPlayer owner, boolean frameGenerationCompleted) {
        if (owner.getPersistentData().getDouble(KEY_CNT3) != 0.0D) {
            owner.getPersistentData().putBoolean(KEY_FAST_FRAME_GENERATION_READY, false);
            return;
        }

        if (frameGenerationCompleted) {
            owner.getPersistentData().putBoolean(KEY_FAST_FRAME_GENERATION_READY, true);
        }
    }

    private static @Nullable Entity findFirstPendingProjectionFrame(ServerLevel level, ServerPlayer owner) {
        String ownerUuid = owner.getStringUUID();
        AABB searchBox = Objects.requireNonNull(projectionFrameSearchBox(owner));
        List<Entity> pendingFrames = level.getEntitiesOfClass(
            Entity.class,
            searchBox,
            candidate -> isOwnedProjectionFrame(candidate, ownerUuid)
                && candidate.getPersistentData().getDouble(KEY_CNT5) > 1.0D
                && candidate.getPersistentData().getDouble(KEY_CNT5) < PROJECTION_FRAME_MAX_COUNT
                && candidate.getPersistentData().getDouble(KEY_CNT2) == 0.0D
        );
        pendingFrames.sort(Comparator.comparingDouble(candidate -> candidate.getPersistentData().getDouble(KEY_CNT5)));
        return pendingFrames.isEmpty() ? null : pendingFrames.get(0);
    }

    public static boolean shouldSkipProjectionFreezeEffect(@Nullable Entity caster, @Nullable LivingEntity target, @Nullable MobEffectInstance effectInstance) {
        if (caster == null || !isProjectionFreezeEffect(caster, target, effectInstance)) {
            return false;
        }
        return isFastFrameGenerationEnabled(caster) && caster.getPersistentData().getDouble(KEY_SKILL) == 0.0D;
    }

    public static boolean jjaShouldKeepProjectionFollowLocked(@Nullable Entity entity, boolean pressZ) {
        if (!pressZ) {
            jjaConsumeFastFrameGenerationReady(entity);
            return false;
        }
        return !jjaConsumeFastFrameGenerationReady(entity);
    }

    public static boolean jjaConsumeFastFrameGenerationReady(@Nullable Entity entity) {
        if (entity == null || !entity.getPersistentData().getBoolean(KEY_FAST_FRAME_GENERATION_READY)) {
            return false;
        }
        entity.getPersistentData().putBoolean(KEY_FAST_FRAME_GENERATION_READY, false);
        return true;
    }

    public static void handleProjectionFreezeEffectApplied(
        @Nullable LevelAccessor world,
        @Nullable Entity caster,
        @Nullable LivingEntity target,
        @Nullable MobEffectInstance effectInstance,
        boolean applied
    ) {
        if (!applied || !(world instanceof ServerLevel level) || !(caster instanceof ServerPlayer sourcePlayer)) {
            return;
        }
        if (!isProjectionSorceryEffect(caster, target, effectInstance) || isProjectionFrameEntity(target)) {
            return;
        }

        orientSourceTowardFrozenTargetHorizontally(sourcePlayer, target);
        if (!isFastFrameGenerationEnabled(sourcePlayer)) {
            return;
        }

        discardProjectionFrames(level, sourcePlayer);
        stopProjectionFollow(sourcePlayer);
    }

    private static boolean isProjectionFreezeEffect(@Nullable Entity caster, @Nullable LivingEntity target, @Nullable MobEffectInstance effectInstance) {
        return caster != null
            && target != null
            && target != caster
            && effectInstance != null
            && effectInstance.getDuration() == 30;
    }

    private static boolean isProjectionSorceryEffect(@Nullable Entity caster, @Nullable LivingEntity target, @Nullable MobEffectInstance effectInstance) {
        return caster != null
            && target != null
            && target != caster
            && effectInstance != null
            && effectInstance.getEffect() == JujutsucraftModMobEffects.PROJECTION_SORCERY.get();
    }

    private static boolean isProjectionFrameType(@Nullable Entity entity) {
        return entity != null && entity.getType() == JujutsucraftModEntities.ENTITY_PROJECTION_SORCERY.get();
    }

    private static boolean isProjectionFrameEntity(@Nullable Entity entity) {
        return isProjectionFrameType(entity) || entity instanceof FrameEntity;
    }

    private static @Nullable ServerPlayer resolveProjectionOwner(ServerLevel level, @Nullable Entity projectionFrame) {
        if (projectionFrame == null) {
            return null;
        }
        String ownerUuid = projectionFrame.getPersistentData().getString(KEY_OWNER_UUID);
        if (ownerUuid == null || ownerUuid.isBlank()) {
            return null;
        }
        @Nullable Entity owner = GetEntityFromUUIDProcedure.execute(level, ownerUuid);
        return owner instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }

    private static boolean advanceOwnerProjectionTarget(ServerPlayer owner, Vec3 origin, Vec3 direction, double maxDistance) {
        for (int i = 0; i < OWNER_TARGET_UPDATES_PER_STEP; i++) {
            double cnt1 = owner.getPersistentData().getDouble(KEY_CNT1) + 1.0D;
            owner.getPersistentData().putDouble(KEY_CNT1, cnt1);

            double cnt2 = owner.getPersistentData().getDouble(KEY_CNT2);
            if (cnt2 == 0.0D) {
                cnt2 = owner.getPersistentData().getDouble(KEY_CNT4);
            }

            cnt2 += (Math.sqrt(Math.max(cnt1 * 0.5D, 1.0D)) * 0.5D + 0.5D) * resolveProjectionStrength(owner);
            owner.getPersistentData().putDouble(KEY_CNT2, cnt2);

            double targetDistance = Math.min(cnt2, maxDistance);
            Vec3 targetPos = Objects.requireNonNull(origin.add(Objects.requireNonNull(direction.scale(targetDistance))));
            owner.getPersistentData().putDouble(KEY_X_POS, targetPos.x);
            owner.getPersistentData().putDouble(KEY_Y_POS, targetPos.y);
            owner.getPersistentData().putDouble(KEY_Z_POS, targetPos.z);
        }
        return owner.getPersistentData().getDouble(KEY_CNT2) + TARGET_DISTANCE_EPSILON < maxDistance;
    }

    private static double resolveProjectionTargetDistance(ServerLevel level, ServerPlayer owner, Vec3 origin, Vec3 direction) {
        double reachableDistance = resolveMaxProjectionDistance(owner);
        if (reachableDistance <= 0.0D) {
            return -1.0D;
        }

        Vec3 traceEnd = Objects.requireNonNull(origin.add(Objects.requireNonNull(direction.scale(PROJECTION_MAX_TRACE_DISTANCE))));
        HitResult hitResult = level.clip(
            new ClipContext(
                Objects.requireNonNull(origin),
                Objects.requireNonNull(traceEnd),
                resolveProjectionTraceBlockMode(),
                ClipContext.Fluid.NONE,
                Objects.requireNonNull(owner)
            )
        );
        double lookDistance = hitResult.getType() == HitResult.Type.MISS
            ? PROJECTION_MAX_TRACE_DISTANCE
            : origin.distanceTo(Objects.requireNonNull(hitResult.getLocation()));
        return Math.min(reachableDistance, lookDistance);
    }

    static @Nonnull ClipContext.Block resolveProjectionTraceBlockMode() {
        return ClipContext.Block.COLLIDER;
    }

    private static double resolveMaxProjectionDistance(ServerPlayer owner) {
        double simulatedCnt1 = owner.getPersistentData().getDouble(KEY_CNT1);
        double simulatedCnt2 = owner.getPersistentData().getDouble(KEY_CNT2);
        if (simulatedCnt2 == 0.0D) {
            simulatedCnt2 = owner.getPersistentData().getDouble(KEY_CNT4);
        }

        double strength = resolveProjectionStrength(owner);
        for (int i = 0; i < PROJECTION_FRAME_MAX_COUNT * OWNER_TARGET_UPDATES_PER_STEP; i++) {
            simulatedCnt1 += 1.0D;
            simulatedCnt2 += (Math.sqrt(Math.max(simulatedCnt1 * 0.5D, 1.0D)) * 0.5D + 0.5D) * strength;
        }
        if (simulatedCnt2 <= TARGET_DISTANCE_EPSILON) {
            return PROJECTION_MAX_TRACE_DISTANCE;
        }
        return simulatedCnt2;
    }

    private static double resolveProjectionStrength(ServerPlayer owner) {
        double strength = 0.5D;
        MobEffectInstance strengthEffect = owner.getEffect(Objects.requireNonNull(MobEffects.DAMAGE_BOOST));
        if (strengthEffect != null) {
            strength += Math.min(strengthEffect.getAmplifier(), 10) * 0.1D * 0.5D;
        }
        return strength;
    }

    private static @Nullable Entity findProjectionSuccessor(ServerLevel level, ServerPlayer owner, Vec3 searchCenter, int expectedCnt5) {
        Vec3 resolvedSearchCenter = Objects.requireNonNull(searchCenter);
        String ownerUuid = owner.getStringUUID();
        AABB searchBox = Objects.requireNonNull(projectionFrameSearchBox(owner));
        List<Entity> successors = level.getEntitiesOfClass(
            Entity.class,
            searchBox,
            candidate -> isOwnedProjectionFrame(candidate, ownerUuid)
                && (int) Math.round(candidate.getPersistentData().getDouble(KEY_CNT5)) == expectedCnt5
                && candidate.getPersistentData().getDouble(KEY_CNT5) < PROJECTION_FRAME_MAX_COUNT
                && candidate.getPersistentData().getDouble(KEY_CNT2) == 0.0D
        );
        successors.sort(Comparator.comparingDouble(candidate -> candidate.position().distanceToSqr(resolvedSearchCenter)));
        return successors.isEmpty() ? null : successors.get(0);
    }

    private static AABB projectionFrameSearchBox(ServerPlayer owner) {
        Vec3 ownerPosition = Objects.requireNonNull(owner.position());
        return Objects.requireNonNull(AABB.ofSize(
            ownerPosition,
            FRAME_SEARCH_HORIZONTAL_RADIUS * 2.0D,
            FRAME_SEARCH_VERTICAL_RADIUS * 2.0D,
            FRAME_SEARCH_HORIZONTAL_RADIUS * 2.0D
        ));
    }

    private static boolean isOwnedProjectionFrame(Entity candidate, String ownerUuid) {
        return isProjectionFrameType(candidate) && ownerUuid.equals(candidate.getPersistentData().getString(KEY_OWNER_UUID));
    }

    private static void snapProjectionFramesToSurface(ServerLevel level, ServerPlayer owner) {
        String ownerUuid = owner.getStringUUID();
        List<Entity> frames = level.getEntitiesOfClass(
            Entity.class,
            Objects.requireNonNull(projectionFrameSearchBox(owner)),
            frame -> isOwnedProjectionFrame(frame, ownerUuid) && frame.getPersistentData().getDouble(KEY_CNT5) >= 1.0D
        );
        frames.sort(Comparator.comparingDouble(frame -> frame.getPersistentData().getDouble(KEY_CNT5)));

        double referenceY = owner.getY();
        for (Entity frame : frames) {
            referenceY = snapProjectionFrameToSurface(level, frame, referenceY);
        }
    }

    private static double snapProjectionFrameToSurface(ServerLevel level, @Nullable Entity frame, double referenceY) {
        if (frame == null || !isProjectionFrameType(frame)) {
            return referenceY;
        }
        Vec3 position = Objects.requireNonNull(frame.position());
        double surfaceY = resolveSurfaceY(level, position.x, position.y, position.z, referenceY);
        frame.setPos(position.x, surfaceY, position.z);
        frame.getPersistentData().putDouble(KEY_Y_POS, surfaceY);
        return surfaceY;
    }

    private static double resolveSurfaceY(ServerLevel level, double x, double fallbackY, double z, double referenceY) {
        BlockPos columnPos = BlockPos.containing(x, level.getMinBuildHeight(), z);
        int chunkX = columnPos.getX() >> 4;
        int chunkZ = columnPos.getZ() >> 4;
        if (!level.getChunkSource().hasChunk(chunkX, chunkZ)) {
            return fallbackY;
        }

        int minGroundY = level.getMinBuildHeight();
        int maxGroundY = level.getMaxBuildHeight() - 2;
        int referenceGroundY = Math.max(minGroundY, Math.min((int) Math.floor(referenceY) - 1, maxGroundY));
        int columnX = columnPos.getX();
        int columnZ = columnPos.getZ();

        for (int offset = 0; offset <= NEAREST_SURFACE_SCAN_LIMIT; offset++) {
            double downSurfaceY = resolveSurfaceCandidateY(level, columnX, referenceGroundY - offset, columnZ, minGroundY, maxGroundY);
            if (!Double.isNaN(downSurfaceY)) {
                return downSurfaceY;
            }

            if (offset == 0) {
                continue;
            }

            double upSurfaceY = resolveSurfaceCandidateY(level, columnX, referenceGroundY + offset, columnZ, minGroundY, maxGroundY);
            if (!Double.isNaN(upSurfaceY)) {
                return upSurfaceY;
            }
        }

        int motionBlockingY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, columnX, columnZ);
        int worldSurfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, columnX, columnZ);
        double motionBlockingSurfaceY = Math.max(minGroundY, Math.min(motionBlockingY, level.getMaxBuildHeight()));
        double worldSurfaceSurfaceY = Math.max(minGroundY, Math.min(worldSurfaceY, level.getMaxBuildHeight()));
        return Math.abs(worldSurfaceSurfaceY - referenceY) < Math.abs(motionBlockingSurfaceY - referenceY)
            ? worldSurfaceSurfaceY
            : motionBlockingSurfaceY;
    }

    private static double resolveSurfaceCandidateY(ServerLevel level, int x, int groundY, int z, int minGroundY, int maxGroundY) {
        if (groundY < minGroundY || groundY > maxGroundY) {
            return Double.NaN;
        }

        BlockPos groundPos = new BlockPos(x, groundY, z);
        BlockState groundState = level.getBlockState(groundPos);
        if (!groundState.getCollisionShape(level, groundPos).isEmpty()) {
            return hasClearStandingSpace(level, groundPos.above(), minGroundY, maxGroundY) ? groundY + 1.0D : Double.NaN;
        }

        if (!groundState.getFluidState().is(Objects.requireNonNull(FluidTags.WATER))) {
            return Double.NaN;
        }

        while (groundY < maxGroundY) {
            BlockPos nextPos = Objects.requireNonNull(groundPos.above());
            if (!level.getBlockState(Objects.requireNonNull(nextPos)).getFluidState().is(Objects.requireNonNull(FluidTags.WATER))) {
                break;
            }
            groundPos = nextPos;
            groundY++;
        }

        return hasClearStandingSpace(level, groundPos.above(), minGroundY, maxGroundY) ? groundY + 1.0D : Double.NaN;
    }

    private static boolean hasClearStandingSpace(ServerLevel level, BlockPos feetPos, int minGroundY, int maxGroundY) {
        if (feetPos.getY() < minGroundY || feetPos.getY() >= maxGroundY + 1) {
            return false;
        }

        BlockState feetState = level.getBlockState(feetPos);
        if (!feetState.getCollisionShape(level, feetPos).isEmpty()) {
            return false;
        }

        BlockPos headPos = Objects.requireNonNull(feetPos.above());
        return level.getBlockState(Objects.requireNonNull(headPos)).getCollisionShape(level, Objects.requireNonNull(headPos)).isEmpty();
    }

    private static void orientSourceTowardFrozenTargetHorizontally(ServerPlayer sourcePlayer, LivingEntity frozenTarget) {
        @Nullable Float yaw = resolveHorizontalYawToward(sourcePlayer, frozenTarget);
        if (yaw == null) {
            return;
        }

        applySourceHorizontalFacing(sourcePlayer, yaw);

        JujutsucraftMod.queueServerWork(
            LOOK_SYNC_DELAY_TICKS,
            () -> {
                if (!sourcePlayer.isAlive() || !frozenTarget.isAlive() || sourcePlayer.level() != frozenTarget.level()) {
                    return;
                }
                @Nullable Float delayedYaw = resolveHorizontalYawToward(sourcePlayer, frozenTarget);
                if (delayedYaw != null) {
                    applySourceHorizontalFacing(sourcePlayer, delayedYaw);
                }
            }
        );
    }

    private static @Nullable Float resolveHorizontalYawToward(ServerPlayer sourcePlayer, LivingEntity frozenTarget) {
        Vec3 horizontal = Objects.requireNonNull(frozenTarget.position()).subtract(Objects.requireNonNull(sourcePlayer.position()));
        horizontal = new Vec3(horizontal.x, 0.0D, horizontal.z);
        if (horizontal.lengthSqr() <= MIN_HORIZONTAL_VECTOR_SQR) {
            return null;
        }
        return (float) (Math.toDegrees(Math.atan2(horizontal.z, horizontal.x)) - 90.0D);
    }

    private static void applySourceHorizontalFacing(ServerPlayer sourcePlayer, float yaw) {
        sourcePlayer.setYRot(yaw);
        sourcePlayer.setYHeadRot(yaw);
        sourcePlayer.setYBodyRot(yaw);
        sourcePlayer.setXRot(0.0F);
        sourcePlayer.connection.teleport(sourcePlayer.getX(), sourcePlayer.getY(), sourcePlayer.getZ(), yaw, 0.0F);
    }

    private static void discardProjectionFrames(ServerLevel level, ServerPlayer sourcePlayer) {
        String sourceUuid = sourcePlayer.getStringUUID();
        AABB searchBox = Objects.requireNonNull(projectionFrameSearchBox(sourcePlayer));
        List<Entity> frames = level.getEntitiesOfClass(
            Entity.class,
            searchBox,
            frame -> isProjectionFrameType(frame)
                && sourceUuid.equals(frame.getPersistentData().getString(KEY_OWNER_UUID))
        );
        for (Entity frame : frames) {
            frame.discard();
        }
    }

    private static void stopProjectionFollow(ServerPlayer sourcePlayer) {
        sourcePlayer.getPersistentData().putDouble(KEY_SKILL, 0.0D);
        sourcePlayer.getPersistentData().putDouble(KEY_CNT3, 1.0D);
        sourcePlayer.getPersistentData().putDouble(KEY_CNT5, PROJECTION_FRAME_MAX_COUNT);
        sourcePlayer.getPersistentData().putDouble(KEY_CNT7, 1.0D);
        sourcePlayer.getPersistentData().putBoolean(KEY_PRESS_Z, false);
        sourcePlayer.getPersistentData().putBoolean(KEY_FAST_FRAME_GENERATION_READY, false);
        sourcePlayer.setDeltaMovement(Objects.requireNonNull(Vec3.ZERO));
        sourcePlayer.fallDistance = 0.0F;
        sourcePlayer.removeEffect(Objects.requireNonNull(JujutsucraftModMobEffects.CURSED_TECHNIQUE.get()));

        String teamName = sourcePlayer.getDisplayName().getString();
        if (!teamName.isEmpty()) {
            JjaCommandHelper.executeAsEntity(sourcePlayer, "team remove " + teamName);
        }
    }

    private static void pruneOldAccelerationEntries(long gameTime) {
        if (LAST_ACCELERATION_TICK_BY_OWNER.size() <= 128) {
            return;
        }
        LAST_ACCELERATION_TICK_BY_OWNER.entrySet().removeIf(entry -> gameTime - entry.getValue() > 200L);
    }
}
