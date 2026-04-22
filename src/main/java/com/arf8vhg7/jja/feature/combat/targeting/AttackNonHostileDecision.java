package com.arf8vhg7.jja.feature.combat.targeting;

public final class AttackNonHostileDecision {
    public record AttackGateState(boolean originalAllowed, boolean attackNonHostileEnabled, boolean allowedIgnoringProfessionAndGroup) {
    }

    private AttackNonHostileDecision() {
    }

    public static boolean shouldOverride(AttackGateState state) {
        return !state.originalAllowed() && state.attackNonHostileEnabled() && state.allowedIgnoringProfessionAndGroup();
    }
}
