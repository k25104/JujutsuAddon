package com.arf8vhg7.jja.feature.combat.targeting;

import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import java.util.UUID;

final class AttackTargetRestrictionRules {
    private AttackTargetRestrictionRules() {
    }

    static boolean isPlayerOwnedWorkerSummon(double friendNum, double friendNumWorker, double ownerFriendNum) {
        return friendNum != 0.0D
            && friendNumWorker != 0.0D
            && friendNumWorker == ownerFriendNum;
    }

    static boolean hasRegisteredAttackTarget(PlayerRctState rctState, UUID targetId) {
        return rctState != null && rctState.hasAttackTarget(targetId);
    }

    static boolean shouldInvertPlayerTargetRestriction(boolean roundDeer) {
        return roundDeer;
    }
}
