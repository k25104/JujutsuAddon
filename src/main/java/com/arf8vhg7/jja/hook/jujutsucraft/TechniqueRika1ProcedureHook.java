package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.okkotsu.OkkotsuRikaRules;
import net.minecraft.world.entity.Entity;

public final class TechniqueRika1ProcedureHook {
    private TechniqueRika1ProcedureHook() {
    }

    public static void onRikaSummoned(Entity owner, Entity summoned) {
        OkkotsuRikaRules.queueFullCursePowerRecoveryOnRikaSummon(owner, summoned);
    }
}
