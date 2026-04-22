package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniqueStartGate;
import net.minecraft.world.entity.Entity;

public final class LogicCooldownMagicOnlyProcedureHook {
    private LogicCooldownMagicOnlyProcedureHook() {
    }

    public static int resolveSimpleDomainDuration(Entity entity, int originalDuration) {
        return TechniqueStartGate.resolveSimpleDomainDurationForPlayer(entity, originalDuration);
    }

    static boolean shouldShowMagicCooldown(
        boolean cooldownTime,
        boolean cursedTechnique,
        boolean fallingBlossomEmotion,
        boolean domainAmplification,
        boolean unstable
    ) {
        return TechniqueStartGate.shouldShowMagicCooldown(
            cooldownTime,
            cursedTechnique,
            fallingBlossomEmotion,
            domainAmplification,
            unstable
        );
    }
}
