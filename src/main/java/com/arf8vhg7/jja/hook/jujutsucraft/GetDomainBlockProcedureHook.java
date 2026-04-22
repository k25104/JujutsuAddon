package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowService;
import net.minecraft.world.entity.Entity;

public final class GetDomainBlockProcedureHook {
    private GetDomainBlockProcedureHook() {
    }

    public static void replaceChimeraShadowGardenFloor(Entity entity) {
        MegumiShadowService.useShadowFloorForDomain(entity);
    }
}
