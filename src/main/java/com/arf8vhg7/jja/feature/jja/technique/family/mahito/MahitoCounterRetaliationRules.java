package com.arf8vhg7.jja.feature.jja.technique.family.mahito;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class MahitoCounterRetaliationRules {
    static final double IDLE_TRANSFIGURATION_SKILL_ID = 1505.0D;
    static final double COUNTER_SUPPRESSION_RATIO_THRESHOLD = 1.2D;
    private static final int DEFAULT_STRENGTH_LEVEL = 1;

    private MahitoCounterRetaliationRules() {
    }

    public static boolean shouldSuppressCounter(
        @Nullable LevelAccessor world,
        @Nullable Entity defender,
        @Nullable Entity immediateSourceEntity,
        @Nullable Entity sourceEntity
    ) {
        Entity mahitoAttacker = resolveMahitoIdleTransfigurationAttacker(world, sourceEntity);
        if (mahitoAttacker == null) {
            mahitoAttacker = resolveMahitoIdleTransfigurationAttacker(world, immediateSourceEntity);
        }
        if (mahitoAttacker == null) {
            return false;
        }

        return shouldSuppressCounter(true, resolveStrengthLevel(defender), resolveStrengthLevel(mahitoAttacker));
    }

    public static boolean shouldSuppressCounter(boolean mahitoIdleTransfigurationAttack, int defenderStrengthLevel, int attackerStrengthLevel) {
        if (!mahitoIdleTransfigurationAttack) {
            return false;
        }
        return computeStrengthRatio(defenderStrengthLevel, attackerStrengthLevel) <= COUNTER_SUPPRESSION_RATIO_THRESHOLD;
    }

    @Nullable
    private static Entity resolveMahitoIdleTransfigurationAttacker(@Nullable LevelAccessor world, @Nullable Entity candidate) {
        if (candidate == null) {
            return null;
        }
        if (isIdleTransfigurationAttacker(candidate)) {
            return candidate;
        }

        Entity rootOwner = JjaJujutsucraftDataAccess.jjaResolveRootLivingOwner(world, candidate);
        if (rootOwner != null && isIdleTransfigurationAttacker(rootOwner)) {
            return rootOwner;
        }
        return null;
    }

    private static boolean isIdleTransfigurationAttacker(@Nullable Entity entity) {
        return JjaJujutsucraftDataAccess.jjaGetCurrentSkillValue(entity) == IDLE_TRANSFIGURATION_SKILL_ID;
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

    private static double computeStrengthRatio(int defenderStrengthLevel, int attackerStrengthLevel) {
        return normalizeStrengthLevel(defenderStrengthLevel) / (double) normalizeStrengthLevel(attackerStrengthLevel);
    }

    private static int normalizeStrengthLevel(int strengthLevel) {
        return Math.max(strengthLevel, DEFAULT_STRENGTH_LEVEL);
    }
}
