package com.arf8vhg7.jja.hook.minecraft;

import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyCombatPassContext;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public final class EntityHook {
    private EntityHook() {
    }

    public static boolean shouldBypassInvulnerability(Entity target, @Nullable DamageSource damageSource) {
        return shouldBypassActiveEchoPair(target, damageSource);
    }

    private static boolean shouldBypassActiveEchoPair(Entity target, @Nullable DamageSource damageSource) {
        if (damageSource == null || !TwinnedBodyCombatPassContext.isExtraArmAttack()) {
            return false;
        }

        Entity attacker = damageSource.getEntity();
        return attacker != null && TwinnedBodyCombatPassContext.matches(attacker, target);
    }
}