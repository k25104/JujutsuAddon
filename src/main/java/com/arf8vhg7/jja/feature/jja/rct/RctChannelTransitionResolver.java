package com.arf8vhg7.jja.feature.jja.rct;

public final class RctChannelTransitionResolver {
    private RctChannelTransitionResolver() {
    }

    public static boolean shouldCancelManualStart(
        boolean reviveWaiting,
        boolean cursedSpirit,
        boolean hasRctEffect,
        boolean selfHealComplete,
        boolean canUseOutputAtFullHeal
    ) {
        if (reviveWaiting) {
            return true;
        }
        if (cursedSpirit || hasRctEffect) {
            return false;
        }
        return selfHealComplete && !canUseOutputAtFullHeal;
    }

    public static boolean shouldStopSelfHealing(boolean hasJackpot, boolean selfHealComplete) {
        return !hasJackpot && selfHealComplete;
    }

    public static boolean shouldStopSelfHealing(boolean hasJackpot, boolean selfHealComplete, boolean keepChannelAfterSelfHeal) {
        return !keepChannelAfterSelfHeal && shouldStopSelfHealing(hasJackpot, selfHealComplete);
    }

    public static boolean canKeepRctChannelWithoutEffect(
        boolean selfHealComplete,
        boolean keepOutputChannel,
        boolean keepBrainRegeneration,
        boolean passesActiveTickCondition
    ) {
        return passesActiveTickCondition && (!selfHealComplete || keepOutputChannel || keepBrainRegeneration);
    }

    public static boolean shouldDisableAutoRct(boolean autoUnlocked, boolean hasCurseEnergy) {
        return !autoUnlocked || !hasCurseEnergy;
    }

    public static boolean shouldStopAutoRctWithoutEffect(
        boolean autoRunning,
        boolean hasRctEffect,
        boolean canKeepRctChannelWithoutEffect
    ) {
        return autoRunning && !hasRctEffect && !canKeepRctChannelWithoutEffect;
    }

    public static boolean shouldStartAutoRct(boolean hasRctEffect, boolean canStartNow) {
        return !hasRctEffect && canStartNow;
    }
}
