package com.arf8vhg7.jja.feature.player.revive;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class ReviveRightClickGuardService {
    private ReviveRightClickGuardService() {
    }

    public static boolean shouldBlockJujutsucraftEntityRightClick(@Nullable Entity target) {
        return target instanceof Player && ReviveFlowService.isWaiting(target);
    }
}
