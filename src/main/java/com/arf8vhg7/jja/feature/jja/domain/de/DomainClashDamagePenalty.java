package com.arf8vhg7.jja.feature.jja.domain.de;

import net.minecraft.nbt.CompoundTag;

public final class DomainClashDamagePenalty {
    private static final double OPEN_BARRIER_CLASH_DAMAGE_FRACTION_PER_CHECK = 0.01D;

    private DomainClashDamagePenalty() {
    }

    public static double remove(double originalPenaltyFactor) {
        return originalPenaltyFactor;
    }

    public static double resolveAccumulatedTotalDamage(
        CompoundTag tag,
        double originalValue,
        double currentHealth,
        double maxHealth,
        boolean isPlayer
    ) {
        double oldHealth = tag.getDouble("oldHealth");
        if (oldHealth < currentHealth) {
            return tag.getDouble("totalDamage");
        }
        if (oldHealth == currentHealth) {
            return originalValue;
        }

        return tag.getDouble("totalDamage") + computeEffectiveDamage(oldHealth, currentHealth, maxHealth, isPlayer);
    }

    public static double computeEffectiveDamage(double oldHealth, double currentHealth, double maxHealth, boolean isPlayer) {
        double damage = oldHealth - currentHealth;
        if (damage <= 0.0D) {
            return 0.0D;
        }

        return Math.max(damage - maxHealth * (isPlayer ? 0.1D : 0.05D), 0.0D);
    }

    public static double computeOpenBarrierClashPressureDamage(double maxHealth) {
        return Math.max(maxHealth, 1.0D) * OPEN_BARRIER_CLASH_DAMAGE_FRACTION_PER_CHECK;
    }
}
