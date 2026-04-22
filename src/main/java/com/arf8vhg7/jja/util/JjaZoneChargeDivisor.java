package com.arf8vhg7.jja.util;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class JjaZoneChargeDivisor {
    private JjaZoneChargeDivisor() {
    }

    public static double get(LivingEntity livingEntity) {
        MobEffectInstance zoneEffect = livingEntity.getEffect(JujutsucraftModMobEffects.ZONE.get());
        if (zoneEffect == null) {
            return 1.0D;
        }

        return 1.2D + 0.1D * zoneEffect.getAmplifier();
    }

    public static double get(Player player) {
        return get((LivingEntity) player);
    }
}
