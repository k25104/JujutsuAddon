package com.arf8vhg7.jja.feature.combat.zone;

import com.arf8vhg7.jja.util.JjaZoneChargeDivisor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class ZoneChargeScalingService {
    private static final double FULL_CHARGE_THRESHOLD = 5.0D;
    private static final double CHANT_STEP_THRESHOLD = 20.0D;

    private ZoneChargeScalingService() {
    }

    public static boolean isCnt6FullChargeOverflow(Entity entity, boolean original) {
        if (!(entity instanceof Player player)) {
            return original;
        }

        return original || isScaledFullChargeReached(entity.getPersistentData().getDouble("cnt6"), player);
    }

    public static boolean isCnt6FullChargeReached(Entity entity, boolean original) {
        if (!(entity instanceof Player player)) {
            return original;
        }

        return original || isScaledFullChargeReached(entity.getPersistentData().getDouble("cnt6"), player);
    }

    public static boolean isCnt6StillCharging(Entity entity, boolean original) {
        if (!(entity instanceof Player player)) {
            return original;
        }

        return entity.getPersistentData().getDouble("cnt6") < scaleThreshold(player, FULL_CHARGE_THRESHOLD);
    }

    public static double clampCnt6ChargeStep(Entity entity, double original) {
        if (!(entity instanceof Player player)) {
            return original;
        }

        return original >= scaleThreshold(player, FULL_CHARGE_THRESHOLD) ? FULL_CHARGE_THRESHOLD : original;
    }

    public static boolean isCnt5ChantStepReady(Entity entity, boolean original) {
        if (!(entity instanceof Player player)) {
            return original;
        }

        return entity.getPersistentData().getDouble("cnt5") > scaleThreshold(player, CHANT_STEP_THRESHOLD);
    }

    public static boolean isCnt1ChargeWindowReady(Entity entity, boolean original) {
        if (!(entity instanceof Player player)) {
            return original;
        }

        return entity.getPersistentData().getDouble("cnt1") >= scaleThreshold(player, CHANT_STEP_THRESHOLD);
    }

    public static boolean isCnt1ChargeWindowExpired(Entity entity, boolean original) {
        if (!(entity instanceof Player player)) {
            return original;
        }

        return entity.getPersistentData().getDouble("cnt1") > scaleThreshold(player, CHANT_STEP_THRESHOLD);
    }

    public static double scaleThreshold(Entity entity, double threshold) {
        return entity instanceof LivingEntity livingEntity ? scaleThreshold(livingEntity, threshold) : threshold;
    }

    public static double scaleThreshold(LivingEntity livingEntity, double threshold) {
        return threshold / JjaZoneChargeDivisor.get(livingEntity);
    }

    public static int scaleCooldownDuration(Entity entity, int originalDuration) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return originalDuration;
        }

        double divisor = JjaZoneChargeDivisor.get(livingEntity);
        if (divisor <= 1.0D) {
            return originalDuration;
        }

        return Math.max(1, (int) Math.round(originalDuration / divisor));
    }

    public static double scaleCurseCost(LivingEntity livingEntity, double originalCost) {
        double divisor = JjaZoneChargeDivisor.get(livingEntity);
        return divisor > 1.0D ? originalCost / divisor : originalCost;
    }

    public static double scaleResolvedTechniqueCost(LivingEntity livingEntity, double resolvedCost) {
        return scaleCurseCost(livingEntity, resolvedCost);
    }

    private static boolean isScaledFullChargeReached(double cnt6, Player player) {
        return cnt6 >= scaleThreshold(player, FULL_CHARGE_THRESHOLD) && cnt6 < FULL_CHARGE_THRESHOLD;
    }
}
