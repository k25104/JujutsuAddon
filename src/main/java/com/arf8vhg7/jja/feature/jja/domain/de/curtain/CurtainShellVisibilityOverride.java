package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

public enum CurtainShellVisibilityOverride {
    AUTO,
    VISIBLE,
    HIDDEN;

    public CurtainShellVisionMode apply(CurtainShellVisionMode baseMode) {
        return switch (this) {
            case AUTO -> baseMode;
            case VISIBLE -> CurtainShellVisionMode.BLACK;
            case HIDDEN -> CurtainShellVisionMode.TRANSPARENT;
        };
    }

    public static CurtainShellVisibilityOverride toggleFromEffectiveVisibility(boolean visible) {
        return visible ? HIDDEN : VISIBLE;
    }
}
