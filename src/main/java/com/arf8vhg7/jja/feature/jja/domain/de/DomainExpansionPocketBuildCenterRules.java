package com.arf8vhg7.jja.feature.jja.domain.de;

import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;

final class DomainExpansionPocketBuildCenterRules {
    private DomainExpansionPocketBuildCenterRules() {
    }

    static Vec3 resolveTransferredPocketBuildCenter(boolean managedTransferredSession, Vec3 originalCenter, @Nullable Vec3 pocketCenter) {
        if (!managedTransferredSession || pocketCenter == null) {
            return originalCenter;
        }

        return pocketCenter;
    }
}
