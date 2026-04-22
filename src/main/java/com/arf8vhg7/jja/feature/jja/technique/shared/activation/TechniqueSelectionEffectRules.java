package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

public final class TechniqueSelectionEffectRules {
    private static final int HAKARI_CURSE_TECHNIQUE = 29;
    private static final int HAKARI_DOMAIN_EXPANSION_SELECT = 20;
    private static final int YOROZU_CURSE_TECHNIQUE = 39;
    private static final int YOROZU_TRUE_SPHERE_SELECT = 15;

    private TechniqueSelectionEffectRules() {
    }

    public static boolean resolveSelectionSkip(
        int curseTechniqueId,
        int selectTechniqueId,
        boolean originalSkip,
        boolean hasJackpot,
        boolean hasDomainExpansion
    ) {
        if (isHakariDomainExpansion(curseTechniqueId, selectTechniqueId) && hasJackpot) {
            return false;
        }

        if (isYorozuTrueSphere(curseTechniqueId, selectTechniqueId) && !hasDomainExpansion) {
            return true;
        }

        return originalSkip;
    }

    static boolean isHakariDomainExpansion(int curseTechniqueId, int selectTechniqueId) {
        return curseTechniqueId == HAKARI_CURSE_TECHNIQUE && selectTechniqueId == HAKARI_DOMAIN_EXPANSION_SELECT;
    }

    static boolean isYorozuTrueSphere(int curseTechniqueId, int selectTechniqueId) {
        return curseTechniqueId == YOROZU_CURSE_TECHNIQUE && selectTechniqueId == YOROZU_TRUE_SPHERE_SELECT;
    }
}