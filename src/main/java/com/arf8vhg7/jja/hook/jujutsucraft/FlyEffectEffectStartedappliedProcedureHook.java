package com.arf8vhg7.jja.hook.jujutsucraft;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class FlyEffectEffectStartedappliedProcedureHook {
    private FlyEffectEffectStartedappliedProcedureHook() {
    }

    public static boolean shouldCancel(Entity entity) {
        return entity instanceof Player player && !player.isCreative() && !player.isSpectator();
    }
}
