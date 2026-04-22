package com.arf8vhg7.jja.feature.jja.technique.family.hakari;

public final class HakariJackpotDurationOverride {
    public static final int HAKARI_JACKPOT_DURATION_TICKS = 5020;

    private HakariJackpotDurationOverride() {
    }

    public static int replaceHakariJackpotDuration(int originalDuration) {
        return HAKARI_JACKPOT_DURATION_TICKS;
    }
}
