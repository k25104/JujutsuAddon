package com.arf8vhg7.jja.feature.jja.traits.sixeyes;

import java.util.List;

final class SixEyesSummonPathRules {
    private static final List<String> SUMMON_PATH_HINTS = List.of(
        "shikigami",
        "cursed_spirit",
        "divine_dog",
        "rika",
        "nue",
        "great_serpent",
        "round_deer",
        "max_elephant",
        "piercing_ox",
        "tiger_funeral",
        "merged_beast_agito",
        "mahoraga",
        "toad",
        "rabbit_escape",
        "moon_dregs",
        "sea_serpent",
        "bathynomus_giganteus",
        "garuda",
        "ryu",
        "tsuchigumo"
    );

    private SixEyesSummonPathRules() {
    }

    static boolean isRecognizedSummonPath(String entityPath) {
        if (entityPath == null || entityPath.isBlank()) {
            return false;
        }
        for (String hint : SUMMON_PATH_HINTS) {
            if (entityPath.contains(hint)) {
                return true;
            }
        }
        return false;
    }
}