package com.arf8vhg7.jja.feature.jja.technique.family.dhruv;

public final class DhruvEnhancementRules {
    private static final double PTEROSAUR_BASE_HP = 80.0D;
    private static final int SINGLE_SUMMON_COUNT = 1;
    private static final long SINGLE_PENDING_VALIDITY_TICKS = 40L;

    private DhruvEnhancementRules() {
    }

    public static PreviewConfig resolvePterosaurPreview() {
        return new PreviewConfig(PTEROSAUR_BASE_HP, SINGLE_SUMMON_COUNT, SINGLE_PENDING_VALIDITY_TICKS);
    }

    public record PreviewConfig(double activationBaseMaxHp, int expectedCount, long pendingValidityTicks) {
    }
}
