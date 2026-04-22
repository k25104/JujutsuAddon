package com.arf8vhg7.jja.feature.player.progression.fame;

import com.arf8vhg7.jja.config.JjaCommonConfig;

public final class FameFeatureConfig {
    private FameFeatureConfig() {
    }

    public static boolean isSukunaFameEnabled() {
        return JjaCommonConfig.ENABLE_SUKUNA_FAME.get();
    }

    public static boolean shouldBlockSukunaFame(boolean originalHasSukunaEffect) {
        return shouldBlockSukunaFame(originalHasSukunaEffect, isSukunaFameEnabled());
    }

    static boolean shouldBlockSukunaFame(boolean originalHasSukunaEffect, boolean enableSukunaFame) {
        return originalHasSukunaEffect && !enableSukunaFame;
    }
}