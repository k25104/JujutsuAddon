package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.config.JjaCommonConfig;

public final class RctFatigueConfig {
    private RctFatigueConfig() {
    }

    public static int getRctFatigueRate() {
        return JjaCommonConfig.RCT_FATIGUE_RATE.get();
    }

    public static int getBrainHealingFatigueAmount() {
        return JjaCommonConfig.BRAIN_HEALING_FATIGUE_AMOUNT.get();
    }

    static int resolveRctFatigueTicks(int outputCount) {
        return resolveRctFatigueTicks(getRctFatigueRate(), outputCount);
    }

    static int resolveRctFatigueTicks(int fatigueRate, int outputCount) {
        return Math.max(0, fatigueRate) * Math.max(0, outputCount);
    }

    static int resolveBrainHealingNextDuration(int currentDuration) {
        return resolveBrainHealingNextDuration(currentDuration, getBrainHealingFatigueAmount());
    }

    static int resolveBrainHealingNextDuration(int currentDuration, int fatigueAmount) {
        int positiveFatigueAmount = Math.max(0, fatigueAmount);
        return Math.max(currentDuration, Math.min(6000, currentDuration + positiveFatigueAmount));
    }
}