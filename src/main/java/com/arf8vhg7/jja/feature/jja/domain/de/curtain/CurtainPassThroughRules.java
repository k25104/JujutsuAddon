package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

public final class CurtainPassThroughRules {
    private CurtainPassThroughRules() {
    }

    public static boolean canPassShell(boolean player, boolean owner, boolean allowlisted, boolean completePhysicalGifted) {
        return player && (completePhysicalGifted || owner || allowlisted);
    }
}
