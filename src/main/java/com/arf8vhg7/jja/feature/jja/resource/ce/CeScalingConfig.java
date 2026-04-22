package com.arf8vhg7.jja.feature.jja.resource.ce;

import com.arf8vhg7.jja.config.JjaCommonConfig;

public final class CeScalingConfig {
    private CeScalingConfig() {
    }

    public static boolean isCeEfficiencyScalingEnabled() {
        return JjaCommonConfig.CE_EFFICIENCY_SCALING.get();
    }

    public static boolean isCePoolScalingEnabled() {
        return JjaCommonConfig.CE_POOL_SCALING.get();
    }
}
