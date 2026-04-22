package com.arf8vhg7.jja.feature.jja.technique.family.kugisaki;

public final class KugisakiHairpinRules {
    private KugisakiHairpinRules() {
    }

    public static double resolveNailDamageMultiplier(double nailCount) {
        return Math.max(nailCount, 0.0D);
    }
}
