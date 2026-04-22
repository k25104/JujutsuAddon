package com.arf8vhg7.jja.feature.jja.technique.shared.menu;

import net.minecraft.network.chat.Component;

public enum TechniqueSetupCategory {
    ANTI_DOMAIN(0, 1, "screen.jja.technique_setup.category.anti_domain"),
    DOMAIN_TYPE(1, 1 << 1, "screen.jja.technique_setup.category.domain_type");

    private final int id;
    private final int mask;
    private final String translationKey;

    TechniqueSetupCategory(int id, int mask, String translationKey) {
        this.id = id;
        this.mask = mask;
        this.translationKey = translationKey;
    }

    public int id() {
        return this.id;
    }

    public int mask() {
        return this.mask;
    }

    public Component displayName() {
        return Component.translatable(this.translationKey);
    }

    public static TechniqueSetupCategory fromId(int id) {
        for (TechniqueSetupCategory category : values()) {
            if (category.id == id) {
                return category;
            }
        }
        return ANTI_DOMAIN;
    }
}
