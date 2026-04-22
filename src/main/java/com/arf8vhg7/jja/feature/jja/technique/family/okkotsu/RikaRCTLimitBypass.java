package com.arf8vhg7.jja.feature.jja.technique.family.okkotsu;

import net.mcreator.jujutsucraft.entity.RikaEntity;
import net.minecraft.world.entity.Entity;

public final class RikaRCTLimitBypass {
    private static final String KEY_RCT_LIMIT = "cnt_reverse_lim";

    private RikaRCTLimitBypass() {
    }

    public static double bypassLimitIncrement(Entity entity, double original) {
        if (entity instanceof RikaEntity) {
            return entity.getPersistentData().getDouble(KEY_RCT_LIMIT);
        }
        return original;
    }
}
