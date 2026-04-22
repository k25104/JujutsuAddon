package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.dhruv.DhruvTrailPlacementContext;

public final class DomainBlockHook {
    private DomainBlockHook() {
    }

    public static int resolveScheduledTickDelay(int originalDelay) {
        return DhruvTrailPlacementContext.resolveScheduledTickDelay(originalDelay);
    }
}
