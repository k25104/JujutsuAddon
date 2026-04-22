package com.arf8vhg7.jja.hook.minecraft;

import com.arf8vhg7.jja.feature.jja.rct.RctMath;
import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import net.minecraft.world.entity.player.Player;

public final class FoodDataHook {
    private FoodDataHook() {
    }

    public static boolean shouldAllowNaturalHeal(Player player) {
        return !ReviveFlowService.isWaiting(player) && !RctMath.isCursedSpirit(player);
    }
}
