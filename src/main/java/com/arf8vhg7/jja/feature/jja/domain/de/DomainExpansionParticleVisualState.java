package com.arf8vhg7.jja.feature.jja.domain.de;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record DomainExpansionParticleVisualState(
    UUID ownerId,
    ResourceLocation dimensionId,
    Vec3 center,
    double baseRadius,
    double currentRadius
) {
    public float factor() {
        return DomainExpansionRuntimeMath.resolveDomainFactor(this.currentRadius, this.baseRadius);
    }
}
