package com.arf8vhg7.jja.feature.jja.technique.family.dhruv;

import java.util.UUID;
import java.util.function.Supplier;

public final class DhruvTrailPlacementContext {
    private static final ThreadLocal<UUID> CURRENT_SHIKIGAMI_UUID = new ThreadLocal<>();

    private DhruvTrailPlacementContext() {
    }

    public static <T> T withPlacement(UUID shikigamiUuid, Supplier<T> action) {
        UUID previous = CURRENT_SHIKIGAMI_UUID.get();
        CURRENT_SHIKIGAMI_UUID.set(shikigamiUuid);
        try {
            return action.get();
        } finally {
            if (previous == null) {
                CURRENT_SHIKIGAMI_UUID.remove();
            } else {
                CURRENT_SHIKIGAMI_UUID.set(previous);
            }
        }
    }

    public static int resolveScheduledTickDelay(int originalDelay) {
        return CURRENT_SHIKIGAMI_UUID.get() != null ? DhruvTrailBlockService.TRAIL_LIFETIME_TICKS : originalDelay;
    }
}
