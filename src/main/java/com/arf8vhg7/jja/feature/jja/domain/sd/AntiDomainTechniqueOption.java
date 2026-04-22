package com.arf8vhg7.jja.feature.jja.domain.sd;

import net.minecraft.network.chat.Component;

public enum AntiDomainTechniqueOption {
    NONE(-1, 0, "key.keyboard.unknown"),
    SIMPLE_DOMAIN(0, 1, "effect.simple_domain"),
    HOLLOW_WICKER_BASKET(1, 1 << 1, "advancements.mastery_hwb.title"),
    FALLING_BLOSSOM_EMOTION(2, 1 << 2, "effect.jujutsucraft.falling_blossom_emotion");

    private final int id;
    private final int mask;
    private final String translationKey;

    AntiDomainTechniqueOption(int id, int mask, String translationKey) {
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

    public boolean isAvailableIn(int availableMask) {
        return (availableMask & this.mask) != 0;
    }

    public static AntiDomainTechniqueOption fromId(int id) {
        for (AntiDomainTechniqueOption option : values()) {
            if (option.id == id) {
                return option;
            }
        }
        return NONE;
    }

    public static AntiDomainTechniqueOption normalize(int id, int availableMask) {
        AntiDomainTechniqueOption current = fromId(id);
        if (current == NONE) {
            return NONE;
        }
        if (availableMask == 0) {
            return NONE;
        }
        if (current.isAvailableIn(availableMask)) {
            return current;
        }
        return firstAvailable(availableMask);
    }

    public static AntiDomainTechniqueOption firstAvailable(int availableMask) {
        for (AntiDomainTechniqueOption option : values()) {
            if (option == NONE) {
                continue;
            }
            if (option.isAvailableIn(availableMask)) {
                return option;
            }
        }
        return NONE;
    }

    public static AntiDomainTechniqueOption nextAvailable(int currentId, int availableMask) {
        AntiDomainTechniqueOption current = normalize(currentId, availableMask);
        AntiDomainTechniqueOption[] options = values();
        for (int index = 1; index <= options.length; index++) {
            AntiDomainTechniqueOption next = options[(current.ordinal() + index) % options.length];
            if (next == NONE || next.isAvailableIn(availableMask)) {
                return next;
            }
        }
        return current;
    }
}
