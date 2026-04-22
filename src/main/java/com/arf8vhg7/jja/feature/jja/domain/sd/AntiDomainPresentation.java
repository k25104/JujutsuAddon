package com.arf8vhg7.jja.feature.jja.domain.sd;

public enum AntiDomainPresentation {
    NONE(0),
    SIMPLE_DOMAIN(1),
    HOLLOW_WICKER_BASKET(2);

    private final int id;

    AntiDomainPresentation(int id) {
        this.id = id;
    }

    public int id() {
        return this.id;
    }

    public static AntiDomainPresentation fromId(int id) {
        for (AntiDomainPresentation presentation : values()) {
            if (presentation.id == id) {
                return presentation;
            }
        }
        return NONE;
    }

    public static AntiDomainPresentation fromOption(AntiDomainTechniqueOption option) {
        if (option == null || option == AntiDomainTechniqueOption.NONE) {
            return NONE;
        }
        return option == AntiDomainTechniqueOption.HOLLOW_WICKER_BASKET ? HOLLOW_WICKER_BASKET : SIMPLE_DOMAIN;
    }
}
