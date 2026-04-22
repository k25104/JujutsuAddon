package com.arf8vhg7.jja.feature.jja.technique.shared.summon;

public final class ShikigamiReleaseKillSuppressionContext {
    private static final ThreadLocal<Integer> ACTIVE_DEPTH = ThreadLocal.withInitial(() -> 0);

    private ShikigamiReleaseKillSuppressionContext() {
    }

    public static void run(Runnable action) {
        if (action == null) {
            return;
        }
        ACTIVE_DEPTH.set(ACTIVE_DEPTH.get() + 1);
        try {
            action.run();
        } finally {
            int nextDepth = ACTIVE_DEPTH.get() - 1;
            if (nextDepth <= 0) {
                ACTIVE_DEPTH.remove();
            } else {
                ACTIVE_DEPTH.set(nextDepth);
            }
        }
    }

    public static boolean isActive() {
        return ACTIVE_DEPTH.get() > 0;
    }
}
