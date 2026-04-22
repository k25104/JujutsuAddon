package com.arf8vhg7.jja.hook.jujutsucraft;

import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class ChangeDamage1ProcedureHook {
    static final double IDLE_TRANSFIGURATION_SKILL_ID = 1505.0D;
    static final double SUKUNA_COUNTER_RATIO_THRESHOLD = 1.2D;
    private static final int DEFAULT_STRENGTH_LEVEL = 1;

    private ChangeDamage1ProcedureHook() {
    }

    public static ItemStack resolveSukunaFingerCheckItem(@Nullable Entity entity, @Nullable Entity target, ItemStack original) {
        if (shouldSuppressIdleTransfigurationSukunaCondition(entity, target)) {
            return ItemStack.EMPTY;
        }
        return original;
    }

    public static boolean resolveSukunaEffectCheck(boolean original, @Nullable Entity entity, @Nullable LivingEntity target) {
        return shouldKeepOriginalSukunaCondition(
            original,
            resolveSkill(entity),
            resolveStrengthLevel(entity),
            resolveStrengthLevel(target)
        );
    }

    static boolean shouldKeepOriginalSukunaCondition(
        boolean originalSukunaCondition,
        double skill,
        int entityStrengthLevel,
        int targetStrengthLevel
    ) {
        if (!originalSukunaCondition) {
            return false;
        }
        return !shouldSuppressIdleTransfigurationSukunaCondition(skill, entityStrengthLevel, targetStrengthLevel);
    }

    static boolean shouldSuppressIdleTransfigurationSukunaCondition(
        double skill,
        int entityStrengthLevel,
        int targetStrengthLevel
    ) {
        return skill == IDLE_TRANSFIGURATION_SKILL_ID
            && computeStrengthRatio(entityStrengthLevel, targetStrengthLevel) >= SUKUNA_COUNTER_RATIO_THRESHOLD;
    }

    private static boolean shouldSuppressIdleTransfigurationSukunaCondition(@Nullable Entity entity, @Nullable Entity target) {
        return shouldSuppressIdleTransfigurationSukunaCondition(
            resolveSkill(entity),
            resolveStrengthLevel(entity),
            resolveStrengthLevel(target)
        );
    }

    private static double resolveSkill(@Nullable Entity entity) {
        return entity == null ? 0.0D : entity.getPersistentData().getDouble("skill");
    }

    private static int resolveStrengthLevel(@Nullable Entity entity) {
        LivingEntity livingEntity = entity instanceof LivingEntity living ? living : null;
        if (livingEntity == null) {
            return DEFAULT_STRENGTH_LEVEL;
        }
        MobEffectInstance strengthEffect = livingEntity.getEffect(MobEffects.DAMAGE_BOOST);
        if (strengthEffect == null) {
            return DEFAULT_STRENGTH_LEVEL;
        }
        return normalizeStrengthLevel(strengthEffect.getAmplifier() + 1);
    }

    private static double computeStrengthRatio(int entityStrengthLevel, int targetStrengthLevel) {
        return normalizeStrengthLevel(entityStrengthLevel) / (double) normalizeStrengthLevel(targetStrengthLevel);
    }

    private static int normalizeStrengthLevel(int strengthLevel) {
        return Math.max(strengthLevel, DEFAULT_STRENGTH_LEVEL);
    }
}
