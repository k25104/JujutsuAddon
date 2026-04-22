package com.arf8vhg7.jja.feature.jja.technique.shared.summon;

final class ShikigamiTechniqueDisplayRules {
    private ShikigamiTechniqueDisplayRules() {
    }

    static String appendPointSuffix(String displayName, int currentPoints, int maxPoints) {
        return displayName + "(" + currentPoints + "/" + maxPoints + ")";
    }
}
