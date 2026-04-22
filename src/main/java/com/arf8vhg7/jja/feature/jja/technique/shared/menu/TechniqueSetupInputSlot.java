package com.arf8vhg7.jja.feature.jja.technique.shared.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public enum TechniqueSetupInputSlot {
    NORMAL(0, "options.difficulty.normal"),
    CROUCH(1, "key.sneak");

    private final int id;
    private final String translationKey;

    TechniqueSetupInputSlot(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public int id() {
        return this.id;
    }

    public Component displayName() {
        return Component.translatable(this.translationKey);
    }

    public static TechniqueSetupInputSlot fromId(int id) {
        for (TechniqueSetupInputSlot slot : values()) {
            if (slot.id == id) {
                return slot;
            }
        }
        return NORMAL;
    }

    public static TechniqueSetupInputSlot fromEntity(Entity entity) {
        return entity != null && entity.isShiftKeyDown() ? CROUCH : NORMAL;
    }
}
