package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

public enum CurtainPhase {
    CHANT_1,
    CHANT_2,
    CHANT_3,
    BUILDING,
    ACTIVE;

    public boolean isClientRelevant() {
        return this == BUILDING || this == ACTIVE;
    }

    public boolean blocksOuterVisibility() {
        return this == ACTIVE;
    }
}
