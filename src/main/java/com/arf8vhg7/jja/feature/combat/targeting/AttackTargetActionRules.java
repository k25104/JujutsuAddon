package com.arf8vhg7.jja.feature.combat.targeting;

public final class AttackTargetActionRules {
    private AttackTargetActionRules() {
    }

    public static boolean shouldAllowPrayerSongWeakness(boolean targetRegistered) {
        return targetRegistered;
    }

    public static boolean shouldCancelUnregisteredRightClickTechnique(boolean sukunaBranch, boolean rightClickTechnique, boolean targetRegistered) {
        return rightClickTechnique && !targetRegistered && !sukunaBranch;
    }

    public static boolean shouldBreakMahoragaDomain(boolean mahoragaUser, boolean targetRegistered, boolean targetHasActiveDomain) {
        return mahoragaUser && targetRegistered && targetHasActiveDomain;
    }
}