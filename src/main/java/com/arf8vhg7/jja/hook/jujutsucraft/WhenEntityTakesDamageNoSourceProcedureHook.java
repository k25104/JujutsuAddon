package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowImmersionService;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public final class WhenEntityTakesDamageNoSourceProcedureHook {
    private WhenEntityTakesDamageNoSourceProcedureHook() {
    }

    public static boolean resolveInfinityProtection(boolean original, @Nullable Entity entity) {
        return original || MegumiShadowImmersionService.isShadowInvulnerable(entity);
    }
}
