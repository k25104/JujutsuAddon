package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

final class CurtainSession {
    private final UUID ownerId;
    private final ResourceKey<Level> dimension;
    private final Vec3 center;
    private final int radius;
    private final double cursePowerCost;
    private final List<List<BlockPos>> shellSlices;
    private final Map<BlockPos, BlockState> replacedStates = new LinkedHashMap<>();
    private final Set<BlockPos> placedShellPositions = new LinkedHashSet<>();
    private CurtainPhase phase = CurtainPhase.CHANT_1;
    private int chantTicksElapsed;
    private int nextSliceIndex;

    CurtainSession(UUID ownerId, ResourceKey<Level> dimension, Vec3 center, int radius, double cursePowerCost, List<List<BlockPos>> shellSlices) {
        this.ownerId = ownerId;
        this.dimension = dimension;
        this.center = center;
        this.radius = radius;
        this.cursePowerCost = cursePowerCost;
        this.shellSlices = shellSlices;
    }

    UUID ownerId() {
        return this.ownerId;
    }

    ResourceKey<Level> dimension() {
        return this.dimension;
    }

    Vec3 center() {
        return this.center;
    }

    int radius() {
        return this.radius;
    }

    double cursePowerCost() {
        return this.cursePowerCost;
    }

    CurtainPhase phase() {
        return this.phase;
    }

    void setPhase(CurtainPhase phase) {
        this.phase = phase;
    }

    int incrementChantTicksElapsed() {
        return ++this.chantTicksElapsed;
    }

    boolean hasRemainingShellSlices() {
        return this.nextSliceIndex < this.shellSlices.size();
    }

    List<BlockPos> takeNextShellSlice() {
        return this.shellSlices.get(this.nextSliceIndex++);
    }

    void trackReplacement(BlockPos pos, BlockState replacedState) {
        this.replacedStates.putIfAbsent(pos.immutable(), replacedState);
    }

    Map<BlockPos, BlockState> replacedStates() {
        return this.replacedStates;
    }

    void markPlaced(BlockPos pos) {
        this.placedShellPositions.add(pos.immutable());
    }

    Set<BlockPos> placedShellPositions() {
        return this.placedShellPositions;
    }

    boolean hasPlacedShellPosition(BlockPos pos) {
        return this.placedShellPositions.contains(pos);
    }
}
