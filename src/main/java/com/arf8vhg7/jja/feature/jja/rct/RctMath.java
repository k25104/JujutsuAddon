package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class RctMath {
    private RctMath() {
    }

    public static boolean isCursedSpirit(Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean("CursedSpirit");
    }

    public static double getFatigueFactor(LivingEntity entity) {
        if (entity == null) {
            return 1.0;
        }
        MobEffectInstance effect = entity.getEffect((MobEffect) JujutsucraftModMobEffects.FATIGUE.get());
        int duration = effect == null ? 0 : Math.max(effect.getDuration(), 0);
        return 1.0 + Math.min(duration / 600.0, 10.0) * 0.4;
    }

    public static double getBaseRctAmount(LivingEntity entity) {
        if (entity == null) {
            return 0.0;
        }
        return entity.getMaxHealth() / 160.0F * 12.5 * 0.1;
    }

    public static double getSelfRctFinalAmount(LivingEntity entity, int effectLevel, boolean affectedByFatigue) {
        if (entity == null) {
            return 0.0;
        }
        double amount = entity.getMaxHealth() / 160.0F * (12.5 + Math.abs(effectLevel) * 7.5) * 0.1;
        if (affectedByFatigue) {
            amount /= getFatigueFactor(entity);
        }
        return amount;
    }

    public static double getRctMultiplier(LivingEntity entity, int effectLevel, boolean affectedByFatigue) {
        double base = getBaseRctAmount(entity);
        if (base <= 0.0) {
            return 0.0;
        }
        return getSelfRctFinalAmount(entity, effectLevel, affectedByFatigue) / base;
    }

    public static double getActiveCeCost(Entity entity) {
        if (!(entity instanceof Player player)) {
            return 0.0;
        }
        double baseCost = isCursedSpirit(player) ? 5.0 : 10.0;
        return baseCost / getFatigueFactor(player);
    }

    public static boolean hasCurseEnergy(Entity entity) {
        if (!(entity instanceof Player)) {
            return true;
        }
        return JjaCursePowerAccountingService.hasEffectivePower(entity);
    }
}
