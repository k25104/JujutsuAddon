package com.arf8vhg7.jja.feature.equipment.curios;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;

public enum CuriosLogicalSlot {
    HEAD("jja_head", EquipmentSlot.HEAD, 3),
    BODY("jja_chest", EquipmentSlot.CHEST, 2),
    LEGS("jja_legs", EquipmentSlot.LEGS, 1);

    private static final Map<String, CuriosLogicalSlot> BY_IDENTIFIER = Map.of(
        HEAD.curiosIdentifier, HEAD,
        BODY.curiosIdentifier, BODY,
        LEGS.curiosIdentifier, LEGS
    );
    private static final Map<EquipmentSlot, CuriosLogicalSlot> BY_EQUIPMENT_SLOT = Map.of(
        HEAD.equipmentSlot, HEAD,
        BODY.equipmentSlot, BODY,
        LEGS.equipmentSlot, LEGS
    );
    private static final Map<Integer, CuriosLogicalSlot> BY_ARMOR_INDEX = Map.of(
        Integer.valueOf(HEAD.armorInventoryIndex), HEAD,
        Integer.valueOf(BODY.armorInventoryIndex), BODY,
        Integer.valueOf(LEGS.armorInventoryIndex), LEGS
    );

    private final String curiosIdentifier;
    private final EquipmentSlot equipmentSlot;
    private final int armorInventoryIndex;

    CuriosLogicalSlot(String curiosIdentifier, EquipmentSlot equipmentSlot, int armorInventoryIndex) {
        this.curiosIdentifier = curiosIdentifier;
        this.equipmentSlot = equipmentSlot;
        this.armorInventoryIndex = armorInventoryIndex;
    }

    public String curiosIdentifier() {
        return this.curiosIdentifier;
    }

    public EquipmentSlot equipmentSlot() {
        return this.equipmentSlot;
    }

    @Nullable
    public static CuriosLogicalSlot fromCuriosIdentifier(String curiosIdentifier) {
        return BY_IDENTIFIER.get(curiosIdentifier);
    }

    @Nullable
    public static CuriosLogicalSlot fromEquipmentSlot(EquipmentSlot equipmentSlot) {
        return BY_EQUIPMENT_SLOT.get(equipmentSlot);
    }

    @Nullable
    public static CuriosLogicalSlot fromArmorInventoryIndex(int armorInventoryIndex) {
        return BY_ARMOR_INDEX.get(Integer.valueOf(armorInventoryIndex));
    }

    @Nullable
    public static CuriosLogicalSlot fromArmorCommandToken(String armorToken) {
        return switch (armorToken) {
            case "armor.head" -> HEAD;
            case "armor.chest" -> BODY;
            case "armor.legs" -> LEGS;
            default -> null;
        };
    }
}
