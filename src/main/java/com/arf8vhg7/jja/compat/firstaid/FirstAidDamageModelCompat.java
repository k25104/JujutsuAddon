package com.arf8vhg7.jja.compat.firstaid;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class FirstAidDamageModelCompat {
    private FirstAidDamageModelCompat() {
    }

    @Nullable
    public static Object getDamageModel(@Nullable Player player) {
        return FirstAidCompatRuntime.getDamageModel(player);
    }

    @Nullable
    public static CompoundTag snapshotDamageModel(@Nullable Player player) {
        return FirstAidCompatRuntime.snapshotDamageModel(player);
    }

    @Nullable
    public static CompoundTag snapshotDamageModelWithRatios(@Nullable Player player) {
        return FirstAidCompatRuntime.snapshotDamageModelWithRatios(player);
    }

    public static void restoreDamageModel(@Nullable Player player, @Nullable CompoundTag snapshot) {
        FirstAidCompatRuntime.restoreDamageModel(player, snapshot);
    }

    public static void restoreDamageModelPreservingRatios(@Nullable Player player, @Nullable CompoundTag snapshot) {
        FirstAidCompatRuntime.restoreDamageModelPreservingRatios(player, snapshot);
    }

    public static void runScaleLogic(@Nullable Player player) {
        FirstAidCompatRuntime.runScaleLogic(player);
    }

    public static boolean distributeHeal(@Nullable Player player, float amount) {
        return FirstAidCompatRuntime.distributeHeal(player, amount);
    }

    public static void setUniformHealthRatio(@Nullable Player player, float ratio) {
        FirstAidCompatRuntime.setUniformHealthRatio(player, ratio);
    }

    public static boolean areAllPartHealthRatiosAtLeast(@Nullable Player player, float minimumRatio) {
        return FirstAidCompatRuntime.areAllPartHealthRatiosAtLeast(player, minimumRatio);
    }

    @Nullable
    public static CompoundTag snapshotMissingHealth(@Nullable Player player) {
        return FirstAidCompatRuntime.snapshotMissingHealth(player);
    }

    public static void restoreMissingHealthAfterScale(@Nullable Player player, @Nullable CompoundTag snapshot) {
        FirstAidCompatRuntime.restoreMissingHealthAfterScale(player, snapshot);
    }

    public static void restoreMissingHealth(@Nullable Player player, @Nullable CompoundTag snapshot) {
        FirstAidCompatRuntime.restoreMissingHealth(player, snapshot);
    }

    public static boolean trySyncDamageModelNow(@Nullable Player player) {
        return FirstAidCompatRuntime.trySyncDamageModelNow(player);
    }

    @Nullable
    public static CompoundTag snapshotPartHealth(@Nullable Player player) {
        return FirstAidCompatRuntime.snapshotPartHealth(player);
    }

    public static boolean hasDamageApplied(@Nullable Player player, @Nullable CompoundTag snapshot) {
        return FirstAidCompatRuntime.hasDamageApplied(player, snapshot);
    }

    public static void toggleTracking(@Nullable Player player, boolean tracking) {
        FirstAidCompatRuntime.toggleTracking(player, tracking);
    }

    public static void setTrackedHealthDirect(@Nullable Player player, float health) {
        FirstAidCompatRuntime.setTrackedHealthDirect(player, health);
    }

    public static void scheduleResync(@Nullable Player player) {
        FirstAidCompatRuntime.scheduleResync(player);
    }
}
