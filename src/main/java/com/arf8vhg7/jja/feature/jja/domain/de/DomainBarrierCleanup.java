package com.arf8vhg7.jja.feature.jja.domain.de;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.GetDomainBlockProcedure;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

final class DomainBarrierCleanup {
    private static final ResourceLocation BARRIER_TAG_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "barrier");
    private static final String KEY_OLD_BLOCK = "old_block";

    private DomainBarrierCleanup() {
    }

    static void removeOutsideRadius(ServerLevel level, Vec3 center, double oldRadius, double newRadius) {
        if (level == null || center == null || oldRadius <= newRadius) {
            return;
        }

        int minX = (int) Math.floor(center.x - oldRadius - 1.0D);
        int minY = (int) Math.floor(center.y - oldRadius - 1.0D);
        int minZ = (int) Math.floor(center.z - oldRadius - 1.0D);
        int maxX = (int) Math.ceil(center.x + oldRadius + 1.0D);
        int maxY = (int) Math.ceil(center.y + oldRadius + 1.0D);
        int maxZ = (int) Math.ceil(center.z + oldRadius + 1.0D);
        double keepRadius = Math.max(newRadius + 0.5D, 0.0D);
        double oldEnvelope = Math.max(oldRadius + 0.5D, 0.0D);

        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            BlockPos immutablePos = pos.immutable();
            double distanceSquared = DomainBarrierLatticeGeometry.distanceSquaredToBarrierLattice(center, immutablePos);
            if (distanceSquared <= keepRadius * keepRadius || distanceSquared > oldEnvelope * oldEnvelope) {
                continue;
            }

            removeBarrierBlock(level, immutablePos);
        }
    }

    static void rebuildChangedBand(ServerLevel level, Entity owner, Vec3 center, double oldRadius, double newRadius) {
        if (level == null || owner == null || center == null || oldRadius <= 0.0D || newRadius <= 0.0D) {
            return;
        }

        DomainBlocks domainBlocks = resolveDomainBlocks(owner);
        double maxRadius = Math.max(oldRadius, newRadius) + 0.5D;
        double minRadius = Math.max(0.0D, Math.min(oldRadius, newRadius) - 2.5D);
        double maxRadiusSquared = maxRadius * maxRadius;
        double minRadiusSquared = minRadius * minRadius;
        int minX = (int)Math.floor(center.x - maxRadius - 1.0D);
        int minY = (int)Math.floor(center.y - maxRadius - 1.0D);
        int minZ = (int)Math.floor(center.z - maxRadius - 1.0D);
        int maxX = (int)Math.ceil(center.x + maxRadius + 1.0D);
        int maxY = (int)Math.ceil(center.y + maxRadius + 1.0D);
        int maxZ = (int)Math.ceil(center.z + maxRadius + 1.0D);
        double yFloor = owner.getPersistentData().getDouble("y_pos_doma") - 1.0D;

        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            BlockPos immutablePos = pos.immutable();
            double distanceSquared = DomainBarrierLatticeGeometry.distanceSquaredToBarrierLattice(center, immutablePos);
            if (distanceSquared < minRadiusSquared || distanceSquared > maxRadiusSquared) {
                continue;
            }

            String desiredBlockName = resolveDesiredBlockName(domainBlocks, immutablePos, distanceSquared, yFloor, newRadius);
            if (desiredBlockName == null) {
                removeBarrierBlock(level, immutablePos);
            } else {
                placeBarrierBlock(level, immutablePos, desiredBlockName);
            }
        }
    }

    private static void removeBarrierBlock(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(BlockTags.create(BARRIER_TAG_ID))) {
            return;
        }

        level.setBlock(pos, resolveRestoredState(resolveOriginalBlockName(level, pos, state)), 3);
    }

    private static DomainBlocks resolveDomainBlocks(Entity owner) {
        GetDomainBlockProcedure.execute(owner);
        return new DomainBlocks(
            owner.getPersistentData().getString("domain_outside"),
            owner.getPersistentData().getString("domain_inside"),
            owner.getPersistentData().getString("domain_floor")
        );
    }

    @Nullable
    private static String resolveDesiredBlockName(DomainBlocks blocks, BlockPos pos, double distanceSquared, double yFloor, double radius) {
        if (distanceSquared >= radius * radius) {
            return null;
        }
        if (distanceSquared >= (radius - 1.0D) * (radius - 1.0D)) {
            return blocks.outside();
        }
        if (distanceSquared >= (radius - 2.0D) * (radius - 2.0D)) {
            return blocks.inside();
        }
        if (pos.getY() <= yFloor && pos.getY() >= yFloor - 4.0D) {
            return blocks.floor();
        }
        return null;
    }

    private static void placeBarrierBlock(ServerLevel level, BlockPos pos, String blockName) {
        BlockState desiredState = resolveBlockState(blockName);
        if (desiredState == null) {
            return;
        }

        BlockState currentState = level.getBlockState(pos);
        if (currentState.equals(desiredState)) {
            return;
        }

        String oldBlockName = resolveOriginalBlockName(level, pos, currentState);
        level.setBlock(pos, desiredState, 3);
        BlockEntity placedBlockEntity = level.getBlockEntity(pos);
        if (placedBlockEntity != null) {
            placedBlockEntity.getPersistentData().putString(KEY_OLD_BLOCK, oldBlockName);
            placedBlockEntity.setChanged();
        }
    }

    @Nullable
    private static BlockState resolveBlockState(String blockName) {
        if (blockName == null || blockName.isBlank()) {
            return null;
        }

        try {
            return BlockStateParser.parseForBlock(
                BuiltInRegistries.BLOCK.asLookup(),
                normalizeBlockStateName(blockName),
                false
            ).blockState();
        } catch (CommandSyntaxException exception) {
            return null;
        }
    }

    private static String resolveOriginalBlockName(ServerLevel level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (state.is(BlockTags.create(BARRIER_TAG_ID)) && blockEntity != null) {
            String oldBlockName = blockEntity.getPersistentData().getString(KEY_OLD_BLOCK);
            if (!oldBlockName.isBlank()) {
                return oldBlockName;
            }
        }

        return BlockStateParser.serialize(state);
    }

    static BlockState resolveRestoredState(String blockName) {
        BlockState restoredState = resolveBlockState(blockName);
        if (restoredState == null || restoredState.is(BlockTags.create(BARRIER_TAG_ID))) {
            return Blocks.AIR.defaultBlockState();
        }
        return restoredState;
    }

    static String normalizeBlockStateName(String blockName) {
        String normalized = blockName == null ? "" : blockName.trim();
        if (normalized.isEmpty()) {
            return "minecraft:air";
        }

        int propertyStart = normalized.indexOf('[');
        String blockId = propertyStart >= 0 ? normalized.substring(0, propertyStart) : normalized;
        return blockId.contains(":") ? normalized : "minecraft:" + normalized;
    }

    private record DomainBlocks(String outside, String inside, String floor) {
    }
}
