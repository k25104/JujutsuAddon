package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.jja.rct.RctEffectSupport;
import com.arf8vhg7.jja.feature.jja.rct.RctContextService;
import com.arf8vhg7.jja.feature.jja.rct.RctPlayerTick;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;

public final class ReverseCursedTechniqueOnEffectActiveTickProcedureHook {
    private ReverseCursedTechniqueOnEffectActiveTickProcedureHook() {
    }

    public static boolean executePlayerTick(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!(JjaCommonConfig.RCT_OUTPUT_ENABLED.get()
            || JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get()
            || JjaCommonConfig.AUTO_RCT_ENABLED.get()) || !(entity instanceof Player player)) {
            return false;
        }
        RctPlayerTick.execute(world, x, y, z, player);
        return true;
    }

    public static boolean applyCursedSpiritDamage(Entity entity, DamageSource damageSource, float amount, Operation<Boolean> original) {
        if (!JjaCommonConfig.RCT_OUTPUT_ENABLED.get() || !(entity instanceof LivingEntity livingEntity)) {
            return original.call(entity, damageSource, amount);
        }
        return RctEffectSupport.applyJjcHealthDelta(
            livingEntity,
            -resolveOutputDamageAmount(livingEntity.getMaxHealth())
        );
    }

    static double resolveOutputDamageAmount(double maxHealth) {
        return Math.max(0.0D, maxHealth * 0.25D);
    }

    static boolean shouldSkipStunReduction(boolean stunEffect, boolean reviveWaiting) {
        return reviveWaiting && stunEffect;
    }

    public static boolean skipStunReduction(LivingEntity livingEntity, MobEffect effect, Operation<Boolean> original) {
        boolean stunEffect = effect == JujutsucraftModMobEffects.STUN.get();
        boolean reviveWaiting = livingEntity instanceof ServerPlayer serverPlayer && RctContextService.isReviveWaiting(serverPlayer);
        if (!shouldSkipStunReduction(stunEffect, reviveWaiting)) {
            return original.call(livingEntity, effect);
        }
        return false;
    }
}
