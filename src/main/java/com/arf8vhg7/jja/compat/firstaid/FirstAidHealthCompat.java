package com.arf8vhg7.jja.compat.firstaid;

import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class FirstAidHealthCompat {
    private FirstAidHealthCompat() {
    }

    public static float calculateCurrentVanillaHealth(@Nullable Player player) {
        return FirstAidCompatRuntime.calculateCurrentVanillaHealth(player);
    }

    public static float getEffectiveHealth(@Nullable LivingEntity entity) {
        return FirstAidCompatRuntime.getEffectiveHealth(entity);
    }

    public static float getEffectiveHealthRatio(@Nullable LivingEntity entity) {
        return FirstAidCompatRuntime.getEffectiveHealthRatio(entity);
    }

    public static void syncVanillaHealth(@Nullable Player player) {
        FirstAidCompatRuntime.syncVanillaHealth(player);
    }

    public static void stripOnHitDebuffs(@Nullable Player player) {
        FirstAidCompatRuntime.stripOnHitDebuffs(player);
    }

    public static boolean isFullyHealed(@Nullable LivingEntity entity) {
        return FirstAidCompatRuntime.isFullyHealed(entity);
    }

    public static boolean isEffectivelyAtFullHealth(@Nullable LivingEntity entity) {
        return FirstAidCompatRuntime.isEffectivelyAtFullHealth(entity);
    }

    @Nullable
    public static DamageModelInspection inspectDamageModel(@Nullable Player player, boolean stripOnHitDebuffs) {
        FirstAidCompatRuntime.DamageModelInspection inspection = FirstAidCompatRuntime.inspectDamageModel(player, stripOnHitDebuffs);
        return inspection == null ? null : new DamageModelInspection(inspection.vanillaHealth());
    }

    public static final class DamageModelInspection {
        private final float vanillaHealth;

        private DamageModelInspection(float vanillaHealth) {
            this.vanillaHealth = vanillaHealth;
        }

        public float vanillaHealth() {
            return vanillaHealth;
        }
    }
}
