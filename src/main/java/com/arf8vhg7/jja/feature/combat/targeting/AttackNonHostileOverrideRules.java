package com.arf8vhg7.jja.feature.combat.targeting;

public final class AttackNonHostileOverrideRules {
    public record OverrideState(
        boolean originalAllowed,
        boolean attackNonHostileEnabled,
        boolean relaxableRelation,
        boolean combatTarget,
        boolean allowedIgnoringProfessionAndGroup
    ) {
    }

    private AttackNonHostileOverrideRules() {
    }

    public static boolean shouldOverride(OverrideState state) {
        return state.relaxableRelation()
            && state.combatTarget()
            && AttackNonHostileDecision.shouldOverride(
                new AttackNonHostileDecision.AttackGateState(
                    state.originalAllowed(),
                    state.attackNonHostileEnabled(),
                    state.allowedIgnoringProfessionAndGroup()
                )
            );
    }

    public static boolean hasProfessionRelation(
        boolean attackerSorcerer,
        boolean targetSorcerer,
        boolean attackerCursedSpirit,
        boolean targetCursedSpirit,
        boolean attackerCurseUser,
        boolean targetCurseUser
    ) {
        return (attackerSorcerer && targetSorcerer)
            || (attackerCursedSpirit && targetCursedSpirit)
            || (attackerCurseUser && targetCurseUser);
    }

    public static boolean isCombatTarget(boolean livingTarget, boolean invulnerable, boolean noAiMob, boolean domainEntity) {
        return livingTarget && !invulnerable && !noAiMob && !domainEntity;
    }
}
