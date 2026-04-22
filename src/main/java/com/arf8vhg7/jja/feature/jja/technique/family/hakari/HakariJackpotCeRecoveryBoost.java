package com.arf8vhg7.jja.feature.jja.technique.family.hakari;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class HakariJackpotCeRecoveryBoost {
    private static final double HAKARI_JACKPOT_CE_RECOVERY_MULTIPLIER = 10.0;

    private HakariJackpotCeRecoveryBoost() {
    }

    public static double modifyHealCursePower(Entity entity, double healCursePower) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return healCursePower;
        }
        if (!livingEntity.hasEffect((MobEffect) JujutsucraftModMobEffects.JACKPOT.get())) {
            return healCursePower;
        }
        return healCursePower * HAKARI_JACKPOT_CE_RECOVERY_MULTIPLIER;
    }
}
