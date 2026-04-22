package com.arf8vhg7.jja.feature.combat.targeting;

final class AttackNonHostileSnapshotRules {
    private AttackNonHostileSnapshotRules() {
    }

    static boolean hasMatchingFriendNum(double attackerFriendNum, double targetFriendNum) {
        return attackerFriendNum != 0.0D && attackerFriendNum == targetFriendNum;
    }

    static boolean shouldForceBetrayalAttack(double myRanged, double targetRanged, String attackerEntityUuid, String targetOwnerUuid) {
        return !isSameOwner(targetOwnerUuid, attackerEntityUuid) && myRanged != targetRanged;
    }

    static boolean passesTargetType(double targetType, double velocitySquared, boolean onGround) {
        if (targetType == 0.0D) {
            return true;
        }
        if (targetType == 1.0D) {
            return velocitySquared > 1.0D;
        }
        if (targetType == 2.0D) {
            return velocitySquared >= 0.01D || onGround;
        }
        return true;
    }

    static boolean isSameOwner(String targetOwnerUuid, String attackerEntityUuid) {
        return targetOwnerUuid.equals(attackerEntityUuid) || attackerEntityUuid.equals(targetOwnerUuid);
    }
}
