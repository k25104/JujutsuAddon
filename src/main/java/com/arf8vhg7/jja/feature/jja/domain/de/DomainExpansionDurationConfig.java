package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.config.JjaCommonConfig;

public final class DomainExpansionDurationConfig {
    private DomainExpansionDurationConfig() {
    }

    public static int getDomainExpansionDuration() {
        return JjaCommonConfig.DOMAIN_EXPANSION_DURATION.get();
    }

    public static int getUnstableDuration() {
        return JjaCommonConfig.UNSTABLE_DURATION.get();
    }
}
