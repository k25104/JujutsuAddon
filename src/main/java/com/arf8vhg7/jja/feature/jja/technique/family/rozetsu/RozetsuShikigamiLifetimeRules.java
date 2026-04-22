package com.arf8vhg7.jja.feature.jja.technique.family.rozetsu;

import net.mcreator.jujutsucraft.entity.RozetsuShikigamiEntity;
import net.mcreator.jujutsucraft.entity.RozetsuShikigamiVessel2Entity;
import net.mcreator.jujutsucraft.entity.RozetsuShikigamiVesselEntity;
import net.minecraft.world.entity.Entity;

public final class RozetsuShikigamiLifetimeRules {
    public static final int ROZETSU_SHIKIGAMI_LIFETIME_TICKS = 72_000;

    private RozetsuShikigamiLifetimeRules() {
    }

    public static int resolveLifetimeLimit(Entity entity, int original) {
        return isRozetsuShikigami(entity) ? ROZETSU_SHIKIGAMI_LIFETIME_TICKS : original;
    }

    public static boolean isRozetsuShikigami(Entity entity) {
        return entity instanceof RozetsuShikigamiEntity
            || entity instanceof RozetsuShikigamiVesselEntity
            || entity instanceof RozetsuShikigamiVessel2Entity;
    }
}
