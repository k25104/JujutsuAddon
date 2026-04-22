package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class MegumiShadowService {
    private static final double PLACEMENT_REACH = 8.0D;
    private static final String SHADOW_BLOCK_NAME = "jja:shadow_block";
    private static final String KEY_SHADOW_SUBMERGED = "jjaMegumiShadowSubmerged";
    private static final String KEY_SHADOW_TECHNIQUE_HELD = "jjaMegumiShadowTechniqueHeld";
    private static final String KEY_PRESS_START_TECHNIQUE = "PRESS_Z";
    private static final Map<UUID, Long> LAST_TRAIL_TICK_BY_OWNER = new HashMap<>();
    private static final Map<UUID, Long> LAST_SHADOW_BREATH_TICK_BY_ENTITY = new HashMap<>();
    private static final Map<UUID, Integer> LAST_SHADOW_AIR_SUPPLY_BY_ENTITY = new HashMap<>();
    private static final Map<UUID, Set<TrackedShadowBlock>> SHADOWS_BY_OWNER = new HashMap<>();

    private MegumiShadowService() {
    }

    public static boolean tryHandleTechnique(LevelAccessor world, double x, double y, double z, @Nullable Entity entity) {
        if (JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(entity) != MegumiShadowTechniqueSelectionService.SHADOW_SKILL) {
            return false;
        }
        if (entity == null) {
            return true;
        }
        if (!isPressingStartTechnique(entity)) {
            releaseShadowTechnique(entity);
            return true;
        }
        if (world instanceof ServerLevel level) {
            if (!isShadowTechniqueHeld(entity) || !MegumiShadowImmersionService.isShadowImmersionActive(entity)) {
                if (placeAtLookTarget(level, entity)) {
                    entity.getPersistentData().putBoolean(KEY_SHADOW_TECHNIQUE_HELD, true);
                }
            }
        }
        finishShadowTechniqueActivation(entity);
        return true;
    }

    public static boolean isShadowTechniqueHeld(@Nullable Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(KEY_SHADOW_TECHNIQUE_HELD);
    }

    public static void releaseShadowTechnique(@Nullable Entity entity) {
        if (entity == null) {
            return;
        }

        boolean wasHeld = isShadowTechniqueHeld(entity);
        entity.getPersistentData().remove(KEY_SHADOW_TECHNIQUE_HELD);
        if (wasHeld || JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(entity) == MegumiShadowTechniqueSelectionService.SHADOW_SKILL) {
            finishShadowTechniqueActivation(entity);
        }
    }

    public static void tickShadowBlock(Level level, BlockPos pos, BlockState state, ShadowBlockEntity shadowBlockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        long gameTime = serverLevel.getGameTime();
        UUID ownerId = shadowBlockEntity.ownerId();
        if (ownerId == null) {
            if (shadowBlockEntity.lastOwnerTouchGameTime() <= 0L) {
                shadowBlockEntity.markOwnerTouch(gameTime);
            } else if (MegumiShadowRules.shouldExpireDomainShadow(gameTime, shadowBlockEntity.lastOwnerTouchGameTime())) {
                serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
            return;
        }
        registerOwnerShadow(serverLevel, pos, ownerId);

        Entity owner = serverLevel.getEntity(ownerId);
        boolean keepShadowActive = shadowBlockEntity.hasOpenStorage();
        AABB blockBox = new AABB(pos);
        if (owner != null && owner.isAlive()) {
            if (owner.getBoundingBox().intersects(blockBox) || shadowBlockEntity.hasOpenStorage()) {
                shadowBlockEntity.markOwnerTouch(gameTime);
            }
            if (owner.getBoundingBox().intersects(blockBox)) {
                expandTrailOncePerOwnerTick(serverLevel, owner, gameTime);
            }
        }

        tickLivingEntities(serverLevel, blockBox, ownerId, gameTime);
        if (owner instanceof ServerPlayer ownerPlayer) {
            absorbItems(serverLevel, blockBox, ownerPlayer);
        }

        if (MegumiShadowRules.shouldRestore(gameTime, shadowBlockEntity.lastOwnerTouchGameTime(), keepShadowActive)) {
            restore(serverLevel, pos, shadowBlockEntity);
        }
    }

    static boolean tryOpenStorage(Level level, BlockPos pos, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ShadowBlockEntity shadowBlockEntity) || !shadowBlockEntity.isOwnedBy(player.getUUID())) {
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            shadowBlockEntity.markOwnerTouch(serverLevel.getGameTime());
        }
        MegumiShadowStorageService.open(serverPlayer, pos);
        return true;
    }

    static void onStorageOpened(Player player, BlockPos pos) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ShadowBlockEntity shadowBlockEntity && shadowBlockEntity.isOwnedBy(player.getUUID())) {
            shadowBlockEntity.startStorageOpen();
            shadowBlockEntity.markOwnerTouch(level.getGameTime());
        }
    }

    static void onStorageClosed(Player player, BlockPos pos) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ShadowBlockEntity shadowBlockEntity && shadowBlockEntity.isOwnedBy(player.getUUID())) {
            shadowBlockEntity.stopStorageOpen();
            shadowBlockEntity.markOwnerTouch(level.getGameTime());
        }
    }

    public static void useShadowFloorForDomain(Entity entity) {
        if (entity == null) {
            return;
        }

        double domain = entity.getPersistentData().getDouble("select") > 0.0D
            ? entity.getPersistentData().getDouble("select")
            : entity.getPersistentData().getDouble("skill_domain");
        if (domain == 6.0D) {
            entity.getPersistentData().putString("domain_floor", SHADOW_BLOCK_NAME);
        }
    }

    public static void expandAroundOwner(ServerLevel level, Entity owner) {
        expandTrailOncePerOwnerTick(level, owner, level.getGameTime());
    }

    private static boolean placeAtLookTarget(ServerLevel level, Entity owner) {
        Vec3 center = resolvePlacementCenter(level, owner);
        if (center == null) {
            return false;
        }
        return placeShadowCluster(level, center, MegumiShadowRules.ACTIVATION_PLACEMENT_RADIUS, owner);
    }

    @Nullable
    private static Vec3 resolvePlacementCenter(ServerLevel level, Entity owner) {
        if (owner.isShiftKeyDown()) {
            return owner.position();
        }

        Vec3 origin = owner.getEyePosition(1.0F);
        Vec3 end = origin.add(owner.getViewVector(1.0F).scale(PLACEMENT_REACH));
        BlockHitResult hitResult = level.clip(new ClipContext(origin, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, owner));
        return hitResult.getType() == HitResult.Type.BLOCK ? hitResult.getLocation() : null;
    }

    private static boolean placeShadowCluster(ServerLevel level, Vec3 center, double radius, Entity owner) {
        int minX = Mth.floor(center.x - radius);
        int minY = Mth.floor(center.y - radius);
        int minZ = Mth.floor(center.z - radius);
        int maxX = Mth.floor(center.x + radius);
        int maxY = Mth.floor(center.y + radius);
        int maxZ = Mth.floor(center.z + radius);
        boolean placedAny = false;
        for (BlockPos targetPos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            BlockPos immutablePos = targetPos.immutable();
            if (MegumiShadowRules.blockIntersectsRadius(center, immutablePos, radius)) {
                placedAny |= tryPlaceShadow(level, immutablePos, owner);
            }
        }
        return placedAny;
    }

    private static boolean tryPlaceShadow(ServerLevel level, BlockPos pos, Entity owner) {
        return tryPlaceShadow(level, pos, owner, true);
    }

    private static boolean tryPlaceShadow(ServerLevel level, BlockPos pos, Entity owner, boolean countExistingShadow) {
        return tryPlaceShadow(level, pos, owner, countExistingShadow, false);
    }

    private static boolean tryPlaceShadow(
        ServerLevel level,
        BlockPos pos,
        Entity owner,
        boolean countExistingShadow,
        boolean ignoreLight
    ) {
        if (!level.isInWorldBounds(pos)) {
            return false;
        }

        BlockState currentState = level.getBlockState(pos);
        BlockEntity currentBlockEntity = level.getBlockEntity(pos);
        if (currentState.is(MegumiShadowBlocks.SHADOW_BLOCK.get())) {
            if (currentBlockEntity instanceof ShadowBlockEntity shadowBlockEntity && shadowBlockEntity.isOwnedBy(owner.getUUID())) {
                shadowBlockEntity.markOwnerTouch(level.getGameTime());
                registerOwnerShadow(level, pos, owner.getUUID());
                return countExistingShadow;
            }
            return false;
        }

        if (!canReplace(level, pos, currentState) || (!ignoreLight && !isDarkEnoughForShadow(level, pos))) {
            return false;
        }

        CompoundTag originalBlockEntityTag = null;
        if (currentState.hasBlockEntity()) {
            if (currentBlockEntity == null) {
                return false;
            }
            originalBlockEntityTag = currentBlockEntity.saveWithFullMetadata();
        }

        BlockState originalState = currentState;
        if (!level.setBlock(pos, MegumiShadowBlocks.SHADOW_BLOCK.get().defaultBlockState(), 3)) {
            return false;
        }

        BlockEntity placedBlockEntity = level.getBlockEntity(pos);
        if (placedBlockEntity instanceof ShadowBlockEntity shadowBlockEntity) {
            shadowBlockEntity.initialize(owner.getUUID(), originalState, originalBlockEntityTag, level.getGameTime());
            registerOwnerShadow(level, pos, owner.getUUID());
        }
        return true;
    }

    private static boolean canReplace(ServerLevel level, BlockPos pos, BlockState state) {
        return MegumiShadowRules.canReplaceWithShadow(
            state.is(Blocks.BEDROCK),
            state.getDestroySpeed(level, pos),
            state.hasBlockEntity(),
            level.getBlockEntity(pos) != null,
            !state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty()
        );
    }

    private static boolean isDarkEnoughForShadow(ServerLevel level, BlockPos pos) {
        BlockPos surfacePos = pos.above();
        if (MegumiShadowRules.shouldIgnoreDarknessCheck(level.canSeeSky(surfacePos))) {
            return true;
        }
        return MegumiShadowRules.isDarkerThanSurroundings(
            level.getMaxLocalRawBrightness(surfacePos),
            brightestSurroundingRawBrightness(level, surfacePos)
        );
    }

    private static int brightestSurroundingRawBrightness(ServerLevel level, BlockPos surfacePos) {
        int brightest = level.getMaxLocalRawBrightness(surfacePos);
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                if (xOffset == 0 && zOffset == 0) {
                    continue;
                }
                brightest = Math.max(brightest, level.getMaxLocalRawBrightness(surfacePos.offset(xOffset, 0, zOffset)));
                brightest = Math.max(brightest, level.getMaxLocalRawBrightness(surfacePos.offset(xOffset, 1, zOffset)));
            }
        }
        return brightest;
    }

    private static void expandTrailOncePerOwnerTick(ServerLevel level, Entity owner, long gameTime) {
        UUID ownerId = owner.getUUID();
        if (LAST_TRAIL_TICK_BY_OWNER.getOrDefault(ownerId, Long.MIN_VALUE) == gameTime) {
            return;
        }
        LAST_TRAIL_TICK_BY_OWNER.put(ownerId, gameTime);
        double radius = MegumiShadowRules.placementRadius(MegumiShadowImmersionService.isShadowImmersionActive(owner));
        placeShadowCluster(level, owner.position(), radius, owner);
        placeShadowCluster(level, owner.getEyePosition(1.0F), radius, owner);
        placeShadowAlongMovement(level, owner, radius);
    }

    private static void placeShadowAlongMovement(ServerLevel level, Entity owner, double radius) {
        Vec3 movement = owner.getDeltaMovement();
        double movementLength = movement.length();
        if (movementLength <= 0.01D) {
            return;
        }

        int segments = Mth.clamp(Mth.ceil(movementLength / Math.max(0.75D, radius * 0.5D)), 1, 6);
        Vec3 basePosition = owner.position();
        Vec3 eyePosition = owner.getEyePosition(1.0F);
        for (int step = 1; step <= segments; step++) {
            double progress = (double) step / segments;
            Vec3 offset = movement.scale(progress);
            placeShadowCluster(level, basePosition.add(offset), radius, owner);
            placeShadowCluster(level, eyePosition.add(offset), radius, owner);
        }
    }

    private static void tickLivingEntities(ServerLevel level, AABB blockBox, UUID ownerId, long gameTime) {
        for (LivingEntity livingEntity : level.getEntitiesOfClass(LivingEntity.class, blockBox, LivingEntity::isAlive)) {
            if (ownerId.equals(livingEntity.getUUID())) {
                continue;
            }
            applyShadowBreath(level, livingEntity, gameTime);
        }
    }

    private static void applyShadowBreath(ServerLevel level, LivingEntity livingEntity, long gameTime) {
        UUID entityId = livingEntity.getUUID();
        if (LAST_SHADOW_BREATH_TICK_BY_ENTITY.getOrDefault(entityId, Long.MIN_VALUE) == gameTime) {
            return;
        }

        int previousAirSupply = LAST_SHADOW_BREATH_TICK_BY_ENTITY.getOrDefault(entityId, Long.MIN_VALUE) == gameTime - 1
            ? LAST_SHADOW_AIR_SUPPLY_BY_ENTITY.getOrDefault(entityId, livingEntity.getAirSupply())
            : livingEntity.getAirSupply();

        int newAirSupply = previousAirSupply;
        if (shouldLoseAirInShadow(livingEntity)) {
            newAirSupply = decreaseAirSupply(livingEntity, previousAirSupply);
            livingEntity.setAirSupply(newAirSupply);
            if (livingEntity.getAirSupply() == -20) {
                livingEntity.setAirSupply(0);
                newAirSupply = 0;
                livingEntity.hurt(level.damageSources().drown(), 2.0F);
            }
        }

        LAST_SHADOW_BREATH_TICK_BY_ENTITY.put(entityId, gameTime);
        LAST_SHADOW_AIR_SUPPLY_BY_ENTITY.put(entityId, newAirSupply);
    }

    private static boolean shouldLoseAirInShadow(LivingEntity livingEntity) {
        boolean invulnerablePlayer = livingEntity instanceof Player player && player.getAbilities().invulnerable;
        return !livingEntity.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(livingEntity) && !invulnerablePlayer;
    }

    private static int decreaseAirSupply(LivingEntity livingEntity, int airSupply) {
        int respiration = net.minecraft.world.item.enchantment.EnchantmentHelper.getRespiration(livingEntity);
        return respiration > 0 && livingEntity.level().getRandom().nextInt(respiration + 1) > 0 ? airSupply : airSupply - 1;
    }

    private static void absorbItems(ServerLevel level, AABB blockBox, ServerPlayer owner) {
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, blockBox, item -> item.isAlive() && !item.getItem().isEmpty())) {
            ItemStack original = itemEntity.getItem();
            ItemStack remaining = MegumiShadowStorageService.insert(owner, original);
            if (remaining.isEmpty()) {
                itemEntity.discard();
            } else if (remaining.getCount() != original.getCount() || !ItemStack.isSameItemSameTags(remaining, original)) {
                itemEntity.setItem(remaining);
            }
        }
    }

    public static void restoreShadowBlock(ServerLevel level, BlockPos pos, ShadowBlockEntity shadowBlockEntity) {
        restore(level, pos, shadowBlockEntity);
    }

    public static boolean isEntityInsideOwnedShadow(Level level, Entity entity, UUID ownerId) {
        AABB box = entity.getBoundingBox().deflate(1.0E-4D);
        for (BlockPos pos : BlockPos.betweenClosed(
            Mth.floor(box.minX),
            Mth.floor(box.minY),
            Mth.floor(box.minZ),
            Mth.floor(box.maxX),
            Mth.floor(box.maxY),
            Mth.floor(box.maxZ)
        )) {
            if (isOwnedShadowBlock(level, pos.immutable(), ownerId)) {
                return true;
            }
        }
        return false;
    }

    public static void restoreOwnedNormalShadowsAfterExit(ServerPlayer owner) {
        UUID ownerId = owner.getUUID();
        Set<TrackedShadowBlock> trackedShadows = SHADOWS_BY_OWNER.get(ownerId);
        if (trackedShadows == null || trackedShadows.isEmpty() || owner.getServer() == null) {
            return;
        }

        for (TrackedShadowBlock trackedShadow : List.copyOf(trackedShadows)) {
            ServerLevel level = owner.getServer().getLevel(trackedShadow.dimension());
            if (level == null || !level.hasChunkAt(trackedShadow.pos())) {
                continue;
            }

            BlockEntity blockEntity = level.getBlockEntity(trackedShadow.pos());
            if (!(blockEntity instanceof ShadowBlockEntity shadowBlockEntity) || !shadowBlockEntity.isOwnedBy(ownerId)) {
                unregisterOwnerShadow(ownerId, trackedShadow);
                continue;
            }
            if (shadowBlockEntity.hasOpenStorage()) {
                continue;
            }
            restore(level, trackedShadow.pos(), shadowBlockEntity);
        }
    }

    public static boolean canEntityPassThroughShadow(BlockGetter level, BlockPos pos, @Nullable Entity entity) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ShadowBlockEntity shadowBlockEntity)) {
            return false;
        }

        UUID ownerId = shadowBlockEntity.ownerId();
        if (ownerId == null) {
            return true;
        }
        if (entity == null) {
            return false;
        }
        if (entity.isSpectator()) {
            return true;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return true;
        }
        if (ownerId.equals(livingEntity.getUUID()) || isShadowSubmerged(livingEntity)) {
            return true;
        }
        if (livingEntity.hasEffect(JujutsucraftModMobEffects.STUN.get())) {
            livingEntity.getPersistentData().putBoolean(KEY_SHADOW_SUBMERGED, true);
            return true;
        }
        return false;
    }

    public static void onEntityInsideShadow(Level level, BlockPos pos, Entity entity) {
        if (!(level instanceof ServerLevel serverLevel) || entity == null) {
            return;
        }
        if (canEntityPassThroughShadow(level, pos, entity)) {
            return;
        }
        moveEntityUpToCollisionFreeSpace(serverLevel, entity);
    }

    private static void restore(ServerLevel level, BlockPos pos, ShadowBlockEntity shadowBlockEntity) {
        UUID ownerId = shadowBlockEntity.ownerId();
        BlockState originalState = shadowBlockEntity.originalState();
        CompoundTag originalBlockEntityTag = shadowBlockEntity.originalBlockEntityTag();
        level.setBlock(pos, originalState, 3);
        if (originalBlockEntityTag != null) {
            BlockEntity restoredBlockEntity = level.getBlockEntity(pos);
            if (restoredBlockEntity != null) {
                restoredBlockEntity.load(originalBlockEntityTag);
                restoredBlockEntity.setChanged();
            }
        }
        if (ownerId != null) {
            unregisterOwnerShadow(ownerId, new TrackedShadowBlock(level.dimension(), pos.immutable()));
        }
        pushEntitiesOut(level, pos);
    }

    private static boolean isOwnedShadowBlock(Level level, BlockPos pos, UUID ownerId) {
        if (!level.getBlockState(pos).is(MegumiShadowBlocks.SHADOW_BLOCK.get())) {
            return false;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof ShadowBlockEntity shadowBlockEntity && shadowBlockEntity.isOwnedBy(ownerId);
    }

    private static boolean isShadowSubmerged(LivingEntity livingEntity) {
        return livingEntity.getPersistentData().getBoolean(KEY_SHADOW_SUBMERGED);
    }

    private static void registerOwnerShadow(ServerLevel level, BlockPos pos, UUID ownerId) {
        SHADOWS_BY_OWNER.computeIfAbsent(ownerId, ignored -> new HashSet<>()).add(new TrackedShadowBlock(level.dimension(), pos.immutable()));
    }

    private static void unregisterOwnerShadow(UUID ownerId, TrackedShadowBlock trackedShadow) {
        Set<TrackedShadowBlock> trackedShadows = SHADOWS_BY_OWNER.get(ownerId);
        if (trackedShadows == null) {
            return;
        }
        trackedShadows.remove(trackedShadow);
        if (trackedShadows.isEmpty()) {
            SHADOWS_BY_OWNER.remove(ownerId);
        }
    }

    private static void pushEntitiesOut(ServerLevel level, BlockPos pos) {
        AABB blockBox = new AABB(pos);
        for (Entity entity : level.getEntities((Entity) null, blockBox, Entity::isAlive)) {
            if (level.noCollision(entity, entity.getBoundingBox())) {
                continue;
            }
            moveEntityUpToCollisionFreeSpace(level, entity);
        }
    }

    private static void moveEntityUpToCollisionFreeSpace(ServerLevel level, Entity entity) {
        AABB originalBox = entity.getBoundingBox();
        for (int yOffset = 1; yOffset <= 8; yOffset++) {
            AABB movedBox = originalBox.move(0.0D, yOffset, 0.0D);
            if (level.noCollision(entity, movedBox)) {
                entity.setPos(entity.getX(), entity.getY() + yOffset, entity.getZ());
                return;
            }
        }
        Direction direction = Direction.UP;
        entity.setPos(entity.getX() + direction.getStepX(), entity.getY() + direction.getStepY(), entity.getZ() + direction.getStepZ());
    }

    private static boolean isPressingStartTechnique(Entity entity) {
        return entity.getPersistentData().getBoolean(KEY_PRESS_START_TECHNIQUE);
    }

    private static boolean shouldKeepTechniqueActive(Entity entity) {
        return isPressingStartTechnique(entity)
            || MegumiShadowImmersionService.isShadowImmersionActive(entity)
            || hasOpenOwnedShadowStorage(entity);
    }

    private static boolean hasOpenOwnedShadowStorage(Entity entity) {
        Set<TrackedShadowBlock> trackedShadows = SHADOWS_BY_OWNER.get(entity.getUUID());
        if (trackedShadows == null || trackedShadows.isEmpty() || entity.getServer() == null) {
            return false;
        }

        for (TrackedShadowBlock trackedShadow : List.copyOf(trackedShadows)) {
            ServerLevel level = entity.getServer().getLevel(trackedShadow.dimension());
            if (level == null || !level.hasChunkAt(trackedShadow.pos())) {
                continue;
            }

            BlockEntity blockEntity = level.getBlockEntity(trackedShadow.pos());
            if (!(blockEntity instanceof ShadowBlockEntity shadowBlockEntity) || !shadowBlockEntity.isOwnedBy(entity.getUUID())) {
                unregisterOwnerShadow(entity.getUUID(), trackedShadow);
                continue;
            }
            if (shadowBlockEntity.hasOpenStorage()) {
                return true;
            }
        }
        return false;
    }

    private static void finishShadowTechniqueActivation(Entity entity) {
        if (shouldKeepTechniqueActive(entity)) {
            return;
        }
        JjaJujutsucraftDataAccess.jjaSetCurrentSkillValue(entity, 0.0D);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.removeEffect(JujutsucraftModMobEffects.CURSED_TECHNIQUE.get());
        }
    }

    private record TrackedShadowBlock(ResourceKey<Level> dimension, BlockPos pos) {
    }
}
