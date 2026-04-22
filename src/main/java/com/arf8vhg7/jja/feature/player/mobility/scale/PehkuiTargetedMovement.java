package com.arf8vhg7.jja.feature.player.mobility.scale;

import com.arf8vhg7.jja.compat.pehkui.JjaPehkuiCompat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class PehkuiTargetedMovement {
    private static final double SCALE_EPSILON = 1.0E-4D;

    private PehkuiTargetedMovement() {
    }

    public static double scaleMaxDistance(Entity entity, double originalDistance) {
        if (!supportsScaledAdjustment(entity)) {
            return originalDistance;
        }

        return originalDistance * resolveSizeScale(entity);
    }

    public static Vec3 toSolvedEndpointVelocity(Entity entity, Vec3 originalVelocity) {
        if (!supportsScaledAdjustment(entity) || !hasHorizontalComponent(originalVelocity)) {
            return originalVelocity;
        }

        float motionScale = JjaPehkuiCompat.getMotionScale(entity);
        if (isDefaultScale(motionScale)) {
            return originalVelocity;
        }

        return originalVelocity.scale(1.0D / motionScale);
    }

    public static Vec3 toSolvedEndpointVelocity(Entity entity, double x, double y, double z) {
        return toSolvedEndpointVelocity(entity, new Vec3(x, y, z));
    }

    public static double getUnscaledBoundingBoxWidth(Entity entity) {
        return unscaleDimension(entity.getBbWidth(), JjaPehkuiCompat.getBoundingBoxWidthScale(entity));
    }

    public static double getUnscaledBoundingBoxHeight(Entity entity) {
        return unscaleDimension(entity.getBbHeight(), JjaPehkuiCompat.getBoundingBoxHeightScale(entity));
    }

    private static boolean supportsScaledAdjustment(Entity entity) {
        return entity instanceof Player && JjaPehkuiCompat.isPehkuiLoaded();
    }

    private static boolean hasHorizontalComponent(Vec3 velocity) {
        return Math.abs(velocity.x) > SCALE_EPSILON || Math.abs(velocity.z) > SCALE_EPSILON;
    }

    private static boolean isDefaultScale(float scale) {
        return JjaPehkuiCompat.isDefaultScale(scale);
    }

    private static double unscaleDimension(double scaledDimension, float scale) {
        return isDefaultScale(scale) ? scaledDimension : scaledDimension / scale;
    }

    private static double resolveSizeScale(Entity entity) {
        return Math.max(JjaPehkuiCompat.getBoundingBoxWidthScale(entity), JjaPehkuiCompat.getBoundingBoxHeightScale(entity));
    }
}
