package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

final class MegumiShadowImmersionRules {
    private MegumiShadowImmersionRules() {
    }

    static boolean shouldRemainActive(boolean currentlyActive, boolean feetInOwnerShadow, boolean headInOwnerShadow) {
        return shouldRemainActive(currentlyActive, feetInOwnerShadow, headInOwnerShadow, false, false);
    }

    static boolean shouldRemainActive(boolean currentlyActive, boolean feetInOwnerShadow, boolean headInOwnerShadow, boolean bodyInOwnerShadow) {
        return shouldRemainActive(currentlyActive, feetInOwnerShadow, headInOwnerShadow, bodyInOwnerShadow, false);
    }

    static boolean shouldRemainActive(
        boolean currentlyActive,
        boolean feetInOwnerShadow,
        boolean headInOwnerShadow,
        boolean bodyInOwnerShadow,
        boolean standingOnOwnerShadowFloor
    ) {
        if (standingOnOwnerShadowFloor) {
            return true;
        }
        if (currentlyActive) {
            return feetInOwnerShadow || headInOwnerShadow || bodyInOwnerShadow;
        }
        return feetInOwnerShadow && headInOwnerShadow;
    }

    static boolean shouldRefreshShadowHoldEffects(long gameTime) {
        return gameTime % MegumiShadowRules.SHADOW_HOLD_EFFECT_REFRESH_INTERVAL_TICKS == 0L;
    }

    static boolean isShadowInvulnerabilityActive(boolean shadowImmersionActive, boolean neutralized) {
        return shadowImmersionActive && !neutralized;
    }
}
