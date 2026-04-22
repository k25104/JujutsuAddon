package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.RikaRCTLimitBypass;
import net.minecraft.world.entity.Entity;

public final class AIActive2ProcedureHook {
    private AIActive2ProcedureHook() {
    }

    public static double bypassLimitIncrement(Entity entity, double original) {
        return RikaRCTLimitBypass.bypassLimitIncrement(entity, original);
    }
}
