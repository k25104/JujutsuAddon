package com.arf8vhg7.jja.feature.jja.domain.de;

import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;

public final class DomainExpansionRuntimeMath {
    private static final double ENTITY_MOVABLE_RADIUS_MARGIN = 2.0D;

    private DomainExpansionRuntimeMath() {
    }

    public static double resolveInheritedRadius(double baseRadius, Collection<Double> activeSharedRadii) {
        double resolved = Math.max(baseRadius, 1.0D);
        if (activeSharedRadii == null) {
            return resolved;
        }

        for (Double radius : activeSharedRadii) {
            if (radius == null || radius.doubleValue() <= 0.0D) {
                continue;
            }
            resolved = Math.min(resolved, radius.doubleValue());
        }
        return resolved;
    }

    @Nullable
    public static CompressionCandidate selectCompressionCandidate(Collection<CompressionCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        return candidates.stream()
            .min(
                Comparator.comparingDouble(CompressionCandidate::factor)
                    .thenComparingDouble(CompressionCandidate::newRadius)
                    .thenComparing(candidate -> candidate.ownerId().toString())
            )
            .orElse(null);
    }

    public static float resolveDomainFactor(double currentRadius, double baseRadius) {
        if (baseRadius <= 0.0D) {
            return 1.0F;
        }

        return (float)(currentRadius / baseRadius);
    }

    public static double resolveEntityMovableRadius(double domainRadius) {
        return Math.max(domainRadius - ENTITY_MOVABLE_RADIUS_MARGIN, 0.0D);
    }

    public static Vec3 toDomainLocalPosition(@Nullable Vec3 sourceCenter, @Nullable Vec3 worldPosition) {
        if (worldPosition == null) {
            return Vec3.ZERO;
        }

        if (sourceCenter == null) {
            return worldPosition;
        }

        return worldPosition.subtract(sourceCenter);
    }

    public static Vec3 toSourceWorldPosition(@Nullable Vec3 sourceCenter, @Nullable Vec3 localPosition) {
        if (localPosition == null) {
            return Vec3.ZERO;
        }

        if (sourceCenter == null) {
            return localPosition;
        }

        return sourceCenter.add(localPosition);
    }

    @Nullable
    static ContainingDomainFactor resolveContainingFactor(@Nullable Vec3 entityAnchor, Collection<ContainingDomainCandidate> candidates) {
        if (entityAnchor == null || candidates == null || candidates.isEmpty()) {
            return null;
        }

        ContainingDomainFactor bestFactor = null;
        for (ContainingDomainCandidate candidate : candidates) {
            if (candidate == null || candidate.baseRadius() <= 0.0D) {
                continue;
            }
            if (!isWithinRadius(entityAnchor, candidate.center(), candidate.currentRadius())) {
                continue;
            }

            float factor = resolveDomainFactor(candidate.currentRadius(), candidate.baseRadius());
            if (bestFactor == null || factor < bestFactor.factor()) {
                bestFactor = new ContainingDomainFactor(candidate.baseRadius(), candidate.currentRadius(), factor);
            }
        }
        return bestFactor;
    }

    static boolean isWithinRadius(@Nullable Vec3 point, @Nullable Vec3 center, double radius) {
        return DomainBarrierLatticeGeometry.isWithinBarrierLattice(center, point, radius);
    }

    static Vec3 compressAnchor(Vec3 anchor, Vec3 center, double oldRadius, double newRadius) {
        if (anchor == null || center == null || oldRadius <= 0.0D || newRadius <= 0.0D) {
            return anchor;
        }

        Vec3 offset = anchor.subtract(center);
        double targetScale = Math.max(Math.min(newRadius / oldRadius, 1.0D), 0.0D);
        Vec3 scaledAnchor = new Vec3(
            center.x + offset.x * targetScale,
            center.y + offset.y * targetScale,
            center.z + offset.z * targetScale
        );
        if (isWithinRadius(scaledAnchor, center, newRadius)) {
            return scaledAnchor;
        }

        double low = 0.0D;
        double high = targetScale;
        for (int i = 0; i < 48; i++) {
            double mid = (low + high) * 0.5D;
            Vec3 candidate = new Vec3(
                center.x + offset.x * mid,
                center.y + offset.y * mid,
                center.z + offset.z * mid
            );
            if (isWithinRadius(candidate, center, newRadius)) {
                low = mid;
            } else {
                high = mid;
            }
        }

        Vec3 compressedAnchor = new Vec3(
            center.x + offset.x * low,
            center.y + offset.y * low,
            center.z + offset.z * low
        );
        return isWithinRadius(compressedAnchor, center, newRadius) ? compressedAnchor : center;
    }

    record ContainingDomainCandidate(Vec3 center, double baseRadius, double currentRadius) {
    }

    record ContainingDomainFactor(double baseRadius, double currentRadius, float factor) {
    }

    public record CompressionCandidate(UUID ownerId, double oldRadius, double newRadius) {
        public double factor() {
            if (oldRadius <= 0.0D) {
                return Double.POSITIVE_INFINITY;
            }

            return newRadius / oldRadius;
        }
    }
}
