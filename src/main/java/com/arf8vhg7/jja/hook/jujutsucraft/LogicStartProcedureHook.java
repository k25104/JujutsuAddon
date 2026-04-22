package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniqueStartGate;
import net.minecraft.world.entity.Entity;

public final class LogicStartProcedureHook {
    private LogicStartProcedureHook() {
    }

    public static boolean applyPlayerTechniqueStartRules(Entity entity, boolean original) {
        return TechniqueStartGate.applyHeldItemRule(entity, original);
    }
}
