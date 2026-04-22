package com.arf8vhg7.jja.feature.jja.rct.client;

import com.arf8vhg7.jja.feature.jja.rct.RctBrainService;

public final class RctClientState {
    private static boolean brainDestructionHolding = false;
    private static int brainDestructionTicks = 0;

    private RctClientState() {
    }

    public static boolean isBrainDestructionHolding() {
        return brainDestructionHolding;
    }

    public static int getBrainDestructionTicks() {
        return brainDestructionTicks;
    }

    public static void startBrainDestructionHold() {
        brainDestructionHolding = true;
        brainDestructionTicks = 0;
    }

    public static void stopBrainDestructionHold() {
        brainDestructionHolding = false;
        brainDestructionTicks = 0;
    }

    public static void tickBrainDestructionHold() {
        if (!brainDestructionHolding) {
            return;
        }
        brainDestructionTicks = Math.min(brainDestructionTicks + 1, RctBrainService.HOLD_REQUIRED_TICKS);
    }
}
