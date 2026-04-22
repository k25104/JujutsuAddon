package com.arf8vhg7.jja.feature.jja.domain.de;

import java.util.Objects;
import net.minecraft.network.chat.Component;

public enum DomainTypeOption {
    NONE(-1, 0, "key.keyboard.unknown"),
    BARRIER(0, 1, "effect.domain_expansion"),
    OPEN_BARRIER(1, 1 << 1, "advancements.mastery_open_barrier_type_domain.title");

    private final int id;
    private final int mask;
    private final String translationKey;

    DomainTypeOption(int id, int mask, String translationKey) {
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
        String translationKey = Objects.requireNonNull(this.translationKey);
        return Component.translatable(translationKey);
    }

    public boolean isOpenBarrier() {
        return this == OPEN_BARRIER;
    }

    public boolean isAvailableIn(int availableMask) {
        return (availableMask & this.mask) != 0;
    }

    public static DomainTypeOption fromId(int id) {
        for (DomainTypeOption option : values()) {
            if (option.id == id) {
                return option;
            }
        }
        return NONE;
    }

    public static DomainTypeOption nextAvailable(int currentId, int availableMask) {
        DomainTypeOption current = fromId(currentId);
        DomainTypeOption[] options = values();
        for (int index = 1; index <= options.length; index++) {
            DomainTypeOption next = options[(current.ordinal() + index) % options.length];
            if (next == NONE || next.isAvailableIn(availableMask)) {
                return next;
            }
        }
        return current;
    }
}
