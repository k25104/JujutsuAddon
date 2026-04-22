package com.arf8vhg7.jja.feature.jja.technique.family.okkotsu;

import net.mcreator.jujutsucraft.entity.Rika2Entity;
import net.minecraft.world.entity.Entity;

public final class RikaCursedSpiritAdjustment {
    private static final String KEY_CURSED_SPIRIT = "CursedSpirit";

    private RikaCursedSpiritAdjustment() {
    }

    public static void clearRika2CursedSpiritTag(Entity entity) {
        if (entity instanceof Rika2Entity) {
            entity.getPersistentData().putBoolean(KEY_CURSED_SPIRIT, false);
        }
    }
}
