package com.arf8vhg7.jja.feature.jja.rct.client;

import com.arf8vhg7.jja.feature.jja.rct.RctBrainService;
import net.minecraft.network.chat.Component;

public final class RctUiRenderer {
    private static final String PROGRESS_BLOCK = "\u25a0";
    private static final double BLOCKS_PER_TICK = 0.25D;

    private RctUiRenderer() {
    }

    public static Component buildBrainDestructionMessage(int holdTicks) {
        int clampedTicks = Math.max(0, Math.min(holdTicks, RctBrainService.HOLD_REQUIRED_TICKS));
        int power = (int) Math.round((RctBrainService.HOLD_REQUIRED_TICKS - clampedTicks) * BLOCKS_PER_TICK);
        String message = Component.translatable("jujutsu.message.long_press").getString();
        for (int i = 0; i < power; i++) {
            message = PROGRESS_BLOCK + message + PROGRESS_BLOCK;
        }
        return Component.literal(message);
    }
}
