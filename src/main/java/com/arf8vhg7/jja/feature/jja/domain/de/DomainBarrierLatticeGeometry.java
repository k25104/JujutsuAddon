package com.arf8vhg7.jja.feature.jja.domain.de;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class DomainBarrierLatticeGeometry {
    private DomainBarrierLatticeGeometry() {
    }

    public static double distanceSquaredToBarrierLattice(@Nullable Vec3 center, @Nullable Vec3 point) {
        if (center == null || point == null) {
            return Double.POSITIVE_INFINITY;
        }

        return distanceSquaredToBarrierLattice(center.x, center.y, center.z, Mth.floor(point.x), Mth.floor(point.y), Mth.floor(point.z));
    }

    public static double distanceSquaredToBarrierLattice(@Nullable Vec3 center, @Nullable BlockPos pos) {
        if (center == null) {
            return Double.POSITIVE_INFINITY;
        }

        return distanceSquaredToBarrierLattice(center.x, center.y, center.z, pos);
    }

    public static double distanceSquaredToBarrierLattice(double centerX, double centerY, double centerZ, @Nullable BlockPos pos) {
        if (pos == null) {
            return Double.POSITIVE_INFINITY;
        }

        return distanceSquaredToBarrierLattice(centerX, centerY, centerZ, pos.getX(), pos.getY(), pos.getZ());
    }

    public static double distanceSquaredToBarrierLattice(
        double centerX,
        double centerY,
        double centerZ,
        int blockX,
        int blockY,
        int blockZ
    ) {
        long roundedCenterX = Math.round(centerX);
        long roundedCenterY = Math.round(centerY);
        long roundedCenterZ = Math.round(centerZ);
        double dx = roundedCenterX - blockX;
        double dy = roundedCenterY - blockY;
        double dz = roundedCenterZ - blockZ;
        return dx * dx + dy * dy + dz * dz;
    }

    public static boolean isWithinBarrierLattice(@Nullable Vec3 center, @Nullable Vec3 point, double radius) {
        return radius >= 0.0D && distanceSquaredToBarrierLattice(center, point) < radius * radius;
    }

    public static boolean isWithinBarrierLattice(@Nullable Vec3 center, @Nullable BlockPos pos, double radius) {
        return radius >= 0.0D && distanceSquaredToBarrierLattice(center, pos) < radius * radius;
    }
}
