package com.arf8vhg7.jja.feature.player.physical;

import com.arf8vhg7.jja.config.JjaCommonConfig;

public final class PhysicalAbilityTuning {
    private static final int PROGRESSIVE_BUFF_REAPPLY_THRESHOLD = 4;

    private PhysicalAbilityTuning() {
    }

    public static long highLevelHealthBoost(double playerLevel) {
        return Math.round(playerLevel / 2.0 - 1.0);
    }

    public static double uncapArmor(double value, double max) {
        return value;
    }

    public static double uncapArmorToughness(double value, double max) {
        return value;
    }

    public static int normalizeBuffIncreaseInterval(int configured) {
        return Math.max(configured, 1);
    }

    public static int getBuffIncreaseInterval() {
        return normalizeBuffIncreaseInterval(JjaCommonConfig.BUFF_INCREASE_INTERVAL.get());
    }

    public static int toProgressiveBuffDuration(int interval) {
        return normalizeBuffIncreaseInterval(interval) + PROGRESSIVE_BUFF_REAPPLY_THRESHOLD;
    }

    public static int getProgressiveBuffDuration() {
        return toProgressiveBuffDuration(getBuffIncreaseInterval());
    }

    public static int getProgressiveBuffReapplyThreshold() {
        return PROGRESSIVE_BUFF_REAPPLY_THRESHOLD;
    }

    public static boolean isEvenTick(int tickCount) {
        return tickCount % 2 == 0;
    }
}
