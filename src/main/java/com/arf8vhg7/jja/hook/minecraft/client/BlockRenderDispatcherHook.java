package com.arf8vhg7.jja.hook.minecraft.client;

import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainBlocks;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.client.CurtainClientState;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockRenderDispatcherHook {
    private BlockRenderDispatcherHook() {
    }

    public static boolean shouldSkipCurtainShellRender(BlockState state) {
        return state.is(CurtainBlocks.CURTAIN_SHELL.get()) && !CurtainClientState.shouldRenderShellBlocks();
    }
}
