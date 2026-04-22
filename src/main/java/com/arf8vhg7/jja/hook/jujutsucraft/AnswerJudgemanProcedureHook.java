package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import net.minecraft.world.entity.Entity;

public final class AnswerJudgemanProcedureHook {
    private AnswerJudgemanProcedureHook() {
    }

    public static double resolveCurrentRadius(Entity entity, double radius) {
        return DomainExpansionHookSupport.resolveMovableRadius(entity, radius);
    }

    public static double resolveFeetAnchorDistanceSquared(Entity owner, Entity target, double originalDistance) {
        return DomainExpansionHookSupport.resolveFeetAnchorDistanceSquared(owner, target, originalDistance);
    }
}
