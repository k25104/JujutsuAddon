package com.arf8vhg7.jja.feature.jja.technique.family.geto;

public final class GetoCursedSpiritRecoveryRules {
    private GetoCursedSpiritRecoveryRules() {
    }

    public static boolean isRecoverable(RecoveryContext context) {
        if (!context.sourceIsPlayer()
            || context.inManipulationDimension()
            || !context.sourceHasGetoTechnique()
            || context.sourceUnstable()
            || context.targetIsPlayer()
            || !context.targetAlive()
            || !context.targetIsCursedSpirit()
            || context.targetSelect() != 0.0D
            || context.friend()) {
            return false;
        }
        if (context.sourceCreative()) {
            return true;
        }
        return isLogicARecoverable(
            context.sourceCapturePower(),
            context.targetStrengthResistance(),
            context.targetCurrentHealth(),
            context.targetMaxHealth(),
            context.targetFriendNumWorker()
        );
    }

    static boolean isLogicARecoverable(
        double sourceCapturePower,
        double targetStrengthResistance,
        double targetCurrentHealth,
        double targetMaxHealth,
        double targetFriendNumWorker
    ) {
        if (targetFriendNumWorker != 0.0D) {
            return false;
        }

        double effectiveSourcePower = sourceCapturePower;
        if (effectiveSourcePower >= 13.0D) {
            effectiveSourcePower = 13.0D + (effectiveSourcePower - 13.0D) * 0.3D;
        }
        if (effectiveSourcePower - 8.0D >= targetStrengthResistance) {
            return true;
        }

        double healthRatio = Math.max(targetCurrentHealth, 0.01D) / Math.max(targetMaxHealth, 0.01D);
        double effectiveTargetPower = (targetStrengthResistance + 4.0D) * healthRatio;
        return effectiveSourcePower * 0.5D >= effectiveTargetPower;
    }

    public record RecoveryContext(
        boolean sourceIsPlayer,
        boolean inManipulationDimension,
        boolean sourceHasGetoTechnique,
        boolean sourceUnstable,
        boolean targetIsPlayer,
        boolean targetAlive,
        boolean targetIsCursedSpirit,
        double targetSelect,
        double sourceCapturePower,
        double targetStrengthResistance,
        double targetCurrentHealth,
        double targetMaxHealth,
        boolean sourceCreative,
        boolean friend,
        double targetFriendNumWorker
    ) {
    }
}
