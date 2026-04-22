package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.sd.SimpleDomainHoldService;
import net.minecraft.world.entity.Entity;

public final class KeySimpleDomainOnKeyReleasedProcedureHook {
    private KeySimpleDomainOnKeyReleasedProcedureHook() {
    }

    public static void onKeyReleased(Entity entity) {
        SimpleDomainHoldService.onRelease(entity);
    }
}
