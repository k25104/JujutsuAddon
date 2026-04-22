package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.strike.JjaAttackStrikeVisualService;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public final class AttackStrongPunchProcedureHook {
    private AttackStrongPunchProcedureHook() {
    }

    public static @Nullable Entity replaceAttackStrikeEntity(
        ServerLevel level,
        Entity entity,
        double xPos,
        double yPos,
        double zPos,
        double range
    ) {
        JjaAttackStrikeVisualService.preserveOwnerRangedIdentity(entity);
        JjaAttackStrikeVisualService.spawn(
            level,
            JjaAttackStrikeVisualService.createStrongDescriptor(
                xPos,
                yPos,
                zPos,
                range,
                entity.getYRot(),
                entity.getXRot(),
                level.getRandom()::nextFloat
            )
        );
        return null;
    }
}
