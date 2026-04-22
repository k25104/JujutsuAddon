package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

final class MegumiShadowRules {
    static final int MAX_STORAGE_SLOTS = 54;
    static final int RESTORE_DELAY_TICKS = 10;
    static final int DOMAIN_SHADOW_EXPIRE_DELAY_TICKS = 80;
    static final double ACTIVATION_PLACEMENT_RADIUS = 1.5D;
    static final double IMMERSION_PLACEMENT_RADIUS = 3.0D;
    static final int SURROUNDING_DARKNESS_MARGIN = 1;
    static final long SHADOW_HOLD_EFFECT_REFRESH_INTERVAL_TICKS = 2L;
    static final int SHADOW_HOLD_EFFECT_DURATION_TICKS = 5;
    static final int SHADOW_HOLD_COOLDOWN_AMPLIFIER = 1;
    static final int SHADOW_HOLD_UNSTABLE_AMPLIFIER = 0;

    private MegumiShadowRules() {
    }

    static int storageCapacityFromStrengthAmplifier(@Nullable Integer amplifier) {
        if (amplifier == null) {
            return 0;
        }
        return Mth.clamp(amplifier + 1, 0, MAX_STORAGE_SLOTS);
    }

    static boolean isDarkerThanSurroundings(int centerRawBrightness, int brightestSurroundingRawBrightness) {
        return Mth.clamp(centerRawBrightness, 0, 15) + SURROUNDING_DARKNESS_MARGIN
            <= Mth.clamp(brightestSurroundingRawBrightness, 0, 15);
    }

    static boolean shouldIgnoreDarknessCheck(boolean exposedToSky) {
        return !exposedToSky;
    }

    static double placementRadius(boolean shadowImmersionActive) {
        return shadowImmersionActive ? IMMERSION_PLACEMENT_RADIUS : ACTIVATION_PLACEMENT_RADIUS;
    }

    static boolean shouldRestore(long currentGameTime, long lastOwnerTouchGameTime) {
        return currentGameTime - lastOwnerTouchGameTime >= RESTORE_DELAY_TICKS;
    }

    static boolean shouldRestore(long currentGameTime, long lastOwnerTouchGameTime, boolean keepShadowActive) {
        return !keepShadowActive && shouldRestore(currentGameTime, lastOwnerTouchGameTime);
    }

    static boolean shouldExpireDomainShadow(long currentGameTime, long lastRefreshGameTime) {
        return currentGameTime - lastRefreshGameTime >= DOMAIN_SHADOW_EXPIRE_DELAY_TICKS;
    }

    static boolean canReplaceWithShadow(boolean bedrock, float destroySpeed, boolean hasBlockEntity, boolean blockEntityPresent, boolean hasCollision) {
        if (bedrock || destroySpeed < 0.0F) {
            return false;
        }
        if (!hasCollision) {
            return false;
        }
        return !hasBlockEntity || blockEntityPresent;
    }

    static boolean blockIntersectsRadius(Vec3 center, BlockPos pos, double radius) {
        double closestX = Mth.clamp(center.x, pos.getX(), pos.getX() + 1.0D);
        double closestY = Mth.clamp(center.y, pos.getY(), pos.getY() + 1.0D);
        double closestZ = Mth.clamp(center.z, pos.getZ(), pos.getZ() + 1.0D);
        double dx = center.x - closestX;
        double dy = center.y - closestY;
        double dz = center.z - closestZ;
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    static int menuRowsFor(int activeSlots) {
        int visibleSlots = Math.max(1, Mth.clamp(activeSlots, 0, MAX_STORAGE_SLOTS));
        return Mth.clamp((visibleSlots + 8) / 9, 1, 6);
    }
}
