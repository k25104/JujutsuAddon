package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

final class DomainExpansionContainmentHelper {
    private DomainExpansionContainmentHelper() {
    }

    static double distanceToCenterSqr(@Nullable Vec3 center, @Nullable Vec3 feetAnchor) {
        return DomainBarrierLatticeGeometry.distanceSquaredToBarrierLattice(center, feetAnchor);
    }

    static double distanceToDomainSpaceSqr(@Nullable Vec3 center, @Nullable Vec3 feetAnchor) {
        return distanceToCenterSqr(center, feetAnchor);
    }

    static boolean isWithinDomainSpace(@Nullable Vec3 center, @Nullable Vec3 feetAnchor, double radius) {
        return radius >= 0.0D && distanceToDomainSpaceSqr(center, feetAnchor) < radius * radius;
    }

    static boolean isWithinRadius(@Nullable Vec3 center, @Nullable Vec3 feetAnchor, double radius) {
        return DomainBarrierLatticeGeometry.isWithinBarrierLattice(center, feetAnchor, radius);
    }

    static double distanceToOwnerCenterSqr(@Nullable Entity owner, @Nullable Entity target) {
        return distanceToDomainSpaceSqr(JjaJujutsucraftDataAccess.jjaGetDomainCenter(owner), resolveFeetAnchor(target));
    }

    static boolean isWithinOwnerRadius(@Nullable Entity owner, @Nullable Entity target, double radius) {
        return isWithinDomainSpace(
            JjaJujutsucraftDataAccess.jjaGetDomainCenter(owner),
            resolveFeetAnchor(target),
            radius
        );
    }

    static boolean isWithinSourceRadius(@Nullable Vec3 sourceCenter, @Nullable Entity target, double radius) {
        return isWithinDomainSpace(sourceCenter, resolveFeetAnchor(target), radius);
    }

    @Nullable
    static Vec3 toDomainLocalPosition(@Nullable Vec3 sourceCenter, @Nullable Vec3 worldPosition) {
        return DomainExpansionRuntimeMath.toDomainLocalPosition(sourceCenter, worldPosition);
    }

    @Nullable
    static Vec3 toSourceWorldPosition(@Nullable Vec3 sourceCenter, @Nullable Vec3 localPosition) {
        return DomainExpansionRuntimeMath.toSourceWorldPosition(sourceCenter, localPosition);
    }

    @Nullable
    static Vec3 resolveFeetAnchor(@Nullable Entity entity) {
        return entity == null ? null : new Vec3(entity.getX(), entity.getY(), entity.getZ());
    }
}
