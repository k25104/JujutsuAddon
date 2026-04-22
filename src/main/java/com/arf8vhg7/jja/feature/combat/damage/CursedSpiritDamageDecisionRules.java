package com.arf8vhg7.jja.feature.combat.damage;

final class CursedSpiritDamageDecisionRules {
    private CursedSpiritDamageDecisionRules() {
    }

    static boolean shouldCancelAttack(boolean targetCursedSpirit, boolean outOfWorld, boolean cursedEnergyDamage) {
        return shouldCancelAttack(targetCursedSpirit, outOfWorld, cursedEnergyDamage, !cursedEnergyDamage);
    }

    static boolean shouldCancelAttack(boolean targetCursedSpirit, boolean administrativeOrVoid, boolean cursedEnergyDamage, boolean naturalDamage) {
        return targetCursedSpirit && !administrativeOrVoid && !cursedEnergyDamage && naturalDamage;
    }

    static boolean isCursedEnergyAttacker(
        boolean player,
        boolean playerHasCursePower,
        boolean heldCursedToolHasPower,
        boolean manualTechniqueProjectile,
        boolean jujutsucraftEntity,
        boolean noCursePowerTagged
    ) {
        if (manualTechniqueProjectile || heldCursedToolHasPower) {
            return true;
        }
        if (player) {
            return playerHasCursePower;
        }
        return jujutsucraftEntity && !noCursePowerTagged;
    }
}
