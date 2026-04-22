package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

public final class CurtainCostRules {
    private CurtainCostRules() {
    }

    public static double resolveCursePowerCost(int radius) {
        return 100.0D * Math.max(radius, 1) / 22.0D;
    }
}
