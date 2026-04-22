package com.arf8vhg7.jja.feature.combat.targeting;

public final class JjaAttackTargetSelectionContextService {
    private static final ThreadLocal<Integer> ACTIVE_DEPTH = ThreadLocal.withInitial(() -> 0);

    private JjaAttackTargetSelectionContextService() {
    }

    public static void jjaEnter() {
        ACTIVE_DEPTH.set(ACTIVE_DEPTH.get() + 1);
    }

    public static void jjaExit() {
        int nextDepth = ACTIVE_DEPTH.get() - 1;
        if (nextDepth <= 0) {
            ACTIVE_DEPTH.remove();
        } else {
            ACTIVE_DEPTH.set(nextDepth);
        }
    }

    public static boolean jjaIsActive() {
        return ACTIVE_DEPTH.get() > 0;
    }
}