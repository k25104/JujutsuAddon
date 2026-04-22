package com.arf8vhg7.jja.feature.combat.targeting;

import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import java.util.UUID;

public final class AttackTargetSelectionRestrictionRules {
    private AttackTargetSelectionRestrictionRules() {
    }

    public static boolean shouldRestrictPlayerTarget(
        boolean playerOwnedWorkerSummon,
        boolean playerTarget,
        PlayerRctState rctState,
        UUID targetId
    ) {
        return shouldRestrictPlayerTarget(playerOwnedWorkerSummon, playerTarget, rctState, targetId, false);
    }

    public static boolean shouldRestrictPlayerTarget(
        boolean playerOwnedWorkerSummon,
        boolean playerTarget,
        PlayerRctState rctState,
        UUID targetId,
        boolean invertRegisteredTargets
    ) {
        if (!playerOwnedWorkerSummon || !playerTarget || targetId == null) {
            return false;
        }
        boolean hasRegisteredTarget = AttackTargetRestrictionRules.hasRegisteredAttackTarget(rctState, targetId);
        return invertRegisteredTargets ? hasRegisteredTarget : !hasRegisteredTarget;
    }

    public static boolean shouldRestrictRoundDeerRecoveryTarget(
        boolean playerOwnedWorkerSummon,
        boolean playerTarget,
        PlayerRctState rctState,
        UUID targetId,
        boolean targetCursedSpirit
    ) {
        if (!playerOwnedWorkerSummon || !playerTarget || targetId == null) {
            return false;
        }
        boolean hasRegisteredTarget = AttackTargetRestrictionRules.hasRegisteredAttackTarget(rctState, targetId);
        return hasRegisteredTarget != targetCursedSpirit;
    }

    public static boolean shouldRoundDeerTreatPlayerAsAttackTarget(
        boolean playerOwnedWorkerSummon,
        boolean playerTarget,
        PlayerRctState rctState,
        UUID targetId
    ) {
        if (!playerOwnedWorkerSummon || !playerTarget || targetId == null) {
            return false;
        }
        return AttackTargetRestrictionRules.hasRegisteredAttackTarget(rctState, targetId);
    }
}
