package com.arf8vhg7.jja.feature.jja.domain;

import com.arf8vhg7.jja.config.JjaCommonConfig;

public final class AntiDomainProgressionConfig {
    private AntiDomainProgressionConfig() {
    }

    public static boolean isSdItemOnly() {
        return JjaCommonConfig.SD_ITEM_ONLY.get();
    }

    public static boolean isFbeItemOnly() {
        return JjaCommonConfig.FBE_ITEM_ONLY.get();
    }

    public static boolean isDaItemOnly() {
        return JjaCommonConfig.DA_ITEM_ONLY.get();
    }

    static boolean isSdTechniqueProgressionDisabled(boolean sdItemOnly) {
        return sdItemOnly;
    }

    static boolean isFbeSimpleDomainFallbackEnabled(boolean fbeItemOnly) {
        return !fbeItemOnly;
    }

    static boolean isDaLegacyGrantDisabled(boolean daItemOnly) {
        return daItemOnly;
    }
}
