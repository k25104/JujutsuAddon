package com.arf8vhg7.jja.feature.jja.domain.sd;

final class AntiDomainHoldExtensionPolicy {
    private static final int SIMPLE_DOMAIN_BASE_DURATION = 100;
    private static final int SIMPLE_DOMAIN_DURATION_PER_LEVEL = 20;

    private AntiDomainHoldExtensionPolicy() {
    }

    static boolean shouldAttemptExtension(
        boolean holdRequested,
        boolean keyHeld,
        boolean autoExtend,
        boolean activeTickAllowed,
        boolean pausedByCursedTechnique
    ) {
        return holdRequested && (keyHeld || autoExtend) && activeTickAllowed && !pausedByCursedTechnique;
    }

    static int computeNextDuration(int currentDuration, int holdAmplifier) {
        return Math.min(computeMaxDuration(holdAmplifier), currentDuration + computeExtensionPerTick(holdAmplifier));
    }

    private static int computeMaxDuration(int strengthLevel) {
        return SIMPLE_DOMAIN_BASE_DURATION + strengthLevel * SIMPLE_DOMAIN_DURATION_PER_LEVEL;
    }

    private static int computeExtensionPerTick(int strengthLevel) {
        return 2 * (int) Math.round(Math.sqrt(strengthLevel + 1.0D));
    }
}
