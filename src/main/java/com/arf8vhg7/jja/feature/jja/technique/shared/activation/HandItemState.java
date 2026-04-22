package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

public enum HandItemState {
    EMPTY,
    BARE_HAND_EQUIVALENT,
    TAIJUTSU_WEAPON,
    SLASH_WEAPON;

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isBareHandEquivalent() {
        return this == BARE_HAND_EQUIVALENT;
    }

    public boolean isMeaningfulHeldItem() {
        return this == TAIJUTSU_WEAPON || this == SLASH_WEAPON;
    }

    public boolean isWeapon() {
        return this.isMeaningfulHeldItem();
    }

    public boolean isTaijutsuWeapon() {
        return this == TAIJUTSU_WEAPON;
    }

    public boolean isSlashWeapon() {
        return this == SLASH_WEAPON;
    }
}