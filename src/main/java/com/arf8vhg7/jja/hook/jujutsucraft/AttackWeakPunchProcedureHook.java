package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.strike.JjaAttackStrikeVisualService;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

public final class AttackWeakPunchProcedureHook {
    private AttackWeakPunchProcedureHook() {
    }

    public static @Nullable Entity replaceAttackStrikeEntity(
        ServerLevel level,
        Entity entity,
        double xPos,
        double yPos,
        double zPos,
        double range,
        boolean combo
    ) {
        JjaAttackStrikeVisualService.preserveOwnerRangedIdentity(entity);
        JjaAttackStrikeVisualService.spawn(
            level,
            JjaAttackStrikeVisualService.createWeakDescriptor(
                xPos,
                yPos,
                zPos,
                range,
                entity.getYRot(),
                entity.getXRot(),
                entity.getPersistentData().getDouble("cnt4"),
                combo,
                level.getRandom()::nextFloat,
                JjaAttackStrikeVisualService.UPSTREAM_JITTER_SAMPLER
            )
        );
        return null;
    }
}
