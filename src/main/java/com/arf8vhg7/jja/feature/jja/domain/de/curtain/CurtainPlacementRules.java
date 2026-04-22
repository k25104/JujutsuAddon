package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class CurtainPlacementRules {
    private CurtainPlacementRules() {
    }

    public static boolean canReplaceShellBlock(boolean air, boolean emptyCollisionShape) {
        return air || emptyCollisionShape;
    }

    public static boolean canReplaceShellBlock(@Nullable BlockGetter level, @Nullable BlockPos pos, @Nullable BlockState state) {
        if (level == null || pos == null || state == null) {
            return false;
        }
        return canReplaceShellBlock(state.isAir(), state.getCollisionShape(level, pos).isEmpty());
    }
}
