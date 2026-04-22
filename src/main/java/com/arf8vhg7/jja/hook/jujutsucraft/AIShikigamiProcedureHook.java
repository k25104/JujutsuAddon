package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.rozetsu.RozetsuShikigamiLifetimeRules;
import net.minecraft.world.entity.Entity;

public final class AIShikigamiProcedureHook {
    private AIShikigamiProcedureHook() {
    }

    public static int resolveRozetsuLifetimeLimit(Entity entity, int original) {
        return RozetsuShikigamiLifetimeRules.resolveLifetimeLimit(entity, original);
    }
}
