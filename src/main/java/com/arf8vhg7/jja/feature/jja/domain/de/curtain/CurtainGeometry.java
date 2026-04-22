package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainBarrierLatticeGeometry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class CurtainGeometry {
    private CurtainGeometry() {
    }

    public static List<List<BlockPos>> buildTopDownShellSlices(Vec3 center, int radius) {
        int normalizedRadius = Math.max(radius, 1);
        long centerX = Math.round(center.x);
        long centerY = Math.round(center.y);
        long centerZ = Math.round(center.z);
        Map<Integer, List<BlockPos>> slices = new TreeMap<>(Comparator.reverseOrder());

        for (int x = (int) centerX - normalizedRadius; x <= centerX + normalizedRadius; x++) {
            for (int y = (int) centerY - normalizedRadius; y <= centerY + normalizedRadius; y++) {
                for (int z = (int) centerZ - normalizedRadius; z <= centerZ + normalizedRadius; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!isShellPosition(center, normalizedRadius, pos)) {
                        continue;
                    }
                    slices.computeIfAbsent(y, ignored -> new ArrayList<>()).add(pos.immutable());
                }
            }
        }

        return slices.values().stream().map(List::copyOf).toList();
    }

    public static boolean isShellPosition(Vec3 center, int radius, @Nullable BlockPos pos) {
        if (pos == null) {
            return false;
        }

        double distanceSquared = DomainBarrierLatticeGeometry.distanceSquaredToBarrierLattice(center, pos);
        double outerRadius = Math.max(radius, 1);
        double innerRadius = Math.max(outerRadius - 1.0D, 0.0D);
        return distanceSquared < outerRadius * outerRadius && distanceSquared >= innerRadius * innerRadius;
    }

    public static boolean isWithinCurtain(Vec3 center, int radius, @Nullable Entity entity) {
        return entity != null && isWithinCurtain(center, radius, new Vec3(entity.getX(), entity.getY(), entity.getZ()));
    }

    public static boolean isWithinCurtain(Vec3 center, int radius, @Nullable Vec3 feetAnchor) {
        return DomainBarrierLatticeGeometry.isWithinBarrierLattice(center, feetAnchor, resolveContainmentRadius(radius));
    }

    static double resolveContainmentRadius(int radius) {
        return Math.max(radius, 1) + 0.5D;
    }
}
