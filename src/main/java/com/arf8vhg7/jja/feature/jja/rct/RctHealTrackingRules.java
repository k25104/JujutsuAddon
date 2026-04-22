package com.arf8vhg7.jja.feature.jja.rct;

public final class RctHealTrackingRules {
    private RctHealTrackingRules() {
    }

    public static boolean shouldTrackSelfHeal(boolean manualPress, boolean autoRunning) {
        return manualPress || autoRunning;
    }
}
