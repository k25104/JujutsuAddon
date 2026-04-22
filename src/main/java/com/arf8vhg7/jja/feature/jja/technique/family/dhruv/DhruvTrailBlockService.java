package com.arf8vhg7.jja.feature.jja.technique.family.dhruv;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;
import net.mcreator.jujutsucraft.entity.Rika2Entity;
import net.mcreator.jujutsucraft.entity.RikaEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

public final class DhruvTrailBlockService {
    static final int TRAIL_LIFETIME_TICKS = 72_000;

    private static final String HETEROCEPHALUS_GLABER_CLASS_NAME = "net.mcreator.jujutsucraft.entity.ShikigamiHeterocephalusGlaberEntity";
    private static final String PTEROSAUR_CLASS_NAME = "net.mcreator.jujutsucraft.entity.ShikigamiPterosaurEntity";
    private static final String SHIKIGAMI_RIKA_DHRUV_CLASS_NAME = "net.mcreator.jujutsucraft.entity.ShikigamiRikaDhruvEntity";
    private static final String RIKA_CLASS_NAME = RikaEntity.class.getName();
    private static final String RIKA2_CLASS_NAME = Rika2Entity.class.getName();
    private static final String KEY_OWNER_UUID = "OWNER_UUID";
    private static final String KEY_DHRUV_TRAIL = "jjaDhruvTrail";
    private static final String KEY_DHRUV_TRAIL_SHIKIGAMI_UUID = "jjaDhruvTrailShikigamiUuid";
    private static final String KEY_DHRUV_TRAIL_CLEANUP_UUID = "jjaDhruvTrailCleanupUuid";
    private static final Map<UUID, Set<Long>> TRACKED_TRAIL_BLOCKS = new HashMap<>();

    private DhruvTrailBlockService() {
    }

    static List<BlockPos> resolveTrailPlacementTargets(@Nullable BlockPos randomPos, @Nullable BlockPos currentPos) {
        LinkedHashSet<BlockPos> orderedPositions = new LinkedHashSet<>();
        if (randomPos != null) {
            orderedPositions.add(randomPos);
        }
        if (currentPos != null) {
            orderedPositions.add(currentPos);
        }
        return List.copyOf(orderedPositions);
    }

    static String resolveDomainBlockOwnerUuid(@Nullable String placerUuid, boolean rangedAmmo, @Nullable String ownerUuid) {
        if (placerUuid == null || placerUuid.isEmpty()) {
            return "";
        }
        if (rangedAmmo && ownerUuid != null && !ownerUuid.isEmpty()) {
            return ownerUuid;
        }
        return placerUuid;
    }

    static boolean shouldClearBeforeUpstreamPlacement(boolean isAir, boolean emptyCollisionShape) {
        return !isAir && emptyCollisionShape;
    }

    public static void registerTrailBlock(@Nullable ServerLevel level, @Nullable BlockPos pos, @Nullable Entity shikigami) {
        registerTrailBlock(level, pos, shikigami, null);
    }

    public static void registerTrailBlock(
        @Nullable ServerLevel level,
        @Nullable BlockPos pos,
        @Nullable Entity shikigami,
        @Nullable UUID cleanupBindingUuid
    ) {
        if (level == null || pos == null || shikigami == null || !isTrailPlacementEntity(shikigami)) {
            return;
        }
        UUID trackedCleanupUuid = resolveTrackedCleanupUuid(cleanupBindingUuid, shikigami.getUUID());
        if (trackedCleanupUuid == null) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (!state.is(Objects.requireNonNull(JujutsucraftModBlocks.DOMAIN.get()))) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            CompoundTag persistentData = blockEntity.getPersistentData();
            TagKey<EntityType<?>> rangedAmmo = Objects.requireNonNull(rangedAmmoTag());
            persistentData.putString(KEY_OWNER_UUID, Objects.requireNonNull(resolveDomainBlockOwnerUuid(
                shikigami.getStringUUID(),
                shikigami.getType().is(rangedAmmo),
                shikigami.getPersistentData().getString(KEY_OWNER_UUID)
            )));
            persistentData.putBoolean(KEY_DHRUV_TRAIL, true);
            persistentData.putUUID(KEY_DHRUV_TRAIL_SHIKIGAMI_UUID, Objects.requireNonNull(shikigami.getUUID()));
            persistentData.putUUID(KEY_DHRUV_TRAIL_CLEANUP_UUID, trackedCleanupUuid);
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        TRACKED_TRAIL_BLOCKS.computeIfAbsent(trackedCleanupUuid, ignored -> new HashSet<>()).add(pos.asLong());
    }

    public static void cleanupTrackedTrailBlocks(@Nullable ServerLevel level, @Nullable Entity shikigami) {
        if (level == null || shikigami == null || !shouldHandleCleanupOnLeave(shikigami)) {
            return;
        }
        cleanupTrackedTrailBlocks(level, shikigami.getUUID());
    }

    static void cleanupTrackedTrailBlocks(@Nullable ServerLevel level, @Nullable UUID cleanupBindingUuid) {
        if (level == null || cleanupBindingUuid == null) {
            return;
        }

        Set<Long> trackedPositions = TRACKED_TRAIL_BLOCKS.remove(cleanupBindingUuid);
        if (trackedPositions == null || trackedPositions.isEmpty()) {
            return;
        }

        for (long packedPos : trackedPositions) {
            BlockPos pos = BlockPos.of(packedPos);
            if (!isTrackedTrailBlock(level, pos, cleanupBindingUuid)) {
                continue;
            }
            level.setBlock(Objects.requireNonNull(pos), Objects.requireNonNull(Blocks.AIR.defaultBlockState()), 3);
        }
    }

    public static boolean isTrailPlacementEntity(@Nullable Entity entity) {
        return entity != null && isTrailPlacementClass(entity.getClass());
    }

    public static boolean shouldHandleCleanupOnLeave(@Nullable Entity entity) {
        return entity != null && isTrailCleanupEntity(entity);
    }

    static boolean canReplaceTrailBlock(boolean isAir, boolean emptyCollisionShape) {
        return isAir || emptyCollisionShape;
    }

    static @Nullable UUID resolveTrackedCleanupUuid(@Nullable UUID cleanupBindingUuid, @Nullable UUID shikigamiUuid) {
        return cleanupBindingUuid != null ? cleanupBindingUuid : shikigamiUuid;
    }

    static boolean isTrailPlacementClass(@Nullable Class<?> entityClass) {
        Class<?> current = entityClass;
        while (current != null) {
            if (isTrailPlacementClassName(current.getName())) {
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }

    static boolean isTrailCleanupEntity(@Nullable Entity entity) {
        return entity != null && isTrailCleanupClass(entity.getClass());
    }

    static boolean isTrailCleanupClass(@Nullable Class<?> entityClass) {
        Class<?> current = entityClass;
        while (current != null) {
            if (isTrailCleanupClassName(current.getName())) {
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }

    static boolean isTrailPlacementClassName(String className) {
        return HETEROCEPHALUS_GLABER_CLASS_NAME.equals(className)
            || PTEROSAUR_CLASS_NAME.equals(className)
            || SHIKIGAMI_RIKA_DHRUV_CLASS_NAME.equals(className);
    }

    static boolean isTrailCleanupClassName(String className) {
        return isTrailPlacementClassName(className)
            || RIKA_CLASS_NAME.equals(className)
            || RIKA2_CLASS_NAME.equals(className);
    }

    private static TagKey<EntityType<?>> rangedAmmoTag() {
        return TagKey.create(
            Objects.requireNonNull(Registries.ENTITY_TYPE),
            Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath("forge", "ranged_ammo"))
        );
    }

    private static boolean isTrackedTrailBlock(@Nullable ServerLevel level, @Nullable BlockPos pos, UUID cleanupBindingUuid) {
        if (level == null || pos == null) {
            return false;
        }
        if (!level.getBlockState(pos).is(Objects.requireNonNull(JujutsucraftModBlocks.DOMAIN.get()))) {
            return false;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return false;
        }

        CompoundTag persistentData = blockEntity.getPersistentData();
        return persistentData.getBoolean(KEY_DHRUV_TRAIL)
            && (
                persistentData.hasUUID(KEY_DHRUV_TRAIL_CLEANUP_UUID)
                    && cleanupBindingUuid.equals(persistentData.getUUID(KEY_DHRUV_TRAIL_CLEANUP_UUID))
                || persistentData.hasUUID(KEY_DHRUV_TRAIL_SHIKIGAMI_UUID)
                    && cleanupBindingUuid.equals(persistentData.getUUID(KEY_DHRUV_TRAIL_SHIKIGAMI_UUID))
            );
    }
}
