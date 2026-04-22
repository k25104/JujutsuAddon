package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainBlocks;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainRuntimeService;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockDestroyAllDirectionProcedureHook {
    private BlockDestroyAllDirectionProcedureHook() {
    }

    public static boolean shouldTreatAsBreakableBarrier(
        boolean original,
        BlockState state,
        TagKey<Block> tagKey,
        @Nullable LevelAccessor world,
        @Nullable BlockPos pos,
        @Nullable Entity sourceEntity
    ) {
        if (!original || state == null || !state.is(CurtainBlocks.CURTAIN_SHELL.get())) {
            return original;
        }
        return !CurtainRuntimeService.shouldBlockBarrierBreak(world, pos, sourceEntity);
    }
}
