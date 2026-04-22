package com.arf8vhg7.jja.hook.jujutsucraft;

import net.minecraft.world.entity.Entity;

public final class BlueEntityHook {
    private BlueEntityHook() {
    }

    public static boolean isPushable() {
        return false;
    }

    public static boolean shouldCancelEntityPush(Entity entity) {
        return true;
    }

    public static boolean shouldCancelPushEntities() {
        return true;
    }
}
