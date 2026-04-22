package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record CurtainVisualState(
    UUID ownerId,
    ResourceLocation dimensionId,
    Vec3 center,
    int radius,
    CurtainPhase phase,
    boolean localViewerPassThrough
) {
    public CurtainVisualState {
        ownerId = Objects.requireNonNull(ownerId);
        dimensionId = Objects.requireNonNull(dimensionId);
        center = Objects.requireNonNull(center);
        phase = Objects.requireNonNull(phase);
        radius = Math.max(1, radius);
    }
}
