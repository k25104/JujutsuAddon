package com.arf8vhg7.jja.hook.minecraft;

import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public final class LivingEntityHook {
    private LivingEntityHook() {
    }

    public static boolean shouldBypassDamageCooldown(Entity target, @Nullable DamageSource damageSource) {
        return EntityHook.shouldBypassInvulnerability(target, damageSource);
    }
}