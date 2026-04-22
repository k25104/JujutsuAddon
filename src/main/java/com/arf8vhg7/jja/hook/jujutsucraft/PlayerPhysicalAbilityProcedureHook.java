package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.family.gojo.GojoProgressionService;
import com.arf8vhg7.jja.feature.player.mobility.fly.FlyEffectGrantRules;
import com.arf8vhg7.jja.feature.player.mobility.fly.ObservedDoubleJumpUnlockService;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidMutationService;
import com.arf8vhg7.jja.feature.player.physical.PhysicalAbilityTuning;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.LogicStartPassiveProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class PlayerPhysicalAbilityProcedureHook {
    private static final int COMPLETE_PHYSICAL_GIFTED_AMPLIFIER = 4;
    private static final int JJA_INFINITE_REGEN_DURATION = -1;
    private static final int ORIGINAL_PROGRESSIVE_BUFF_REAPPLY_THRESHOLD = 5;
    private static final int DEFAULT_AIRBORNE_JUMP_DURATION = 10;

    private PlayerPhysicalAbilityProcedureHook() {
    }

    public static long highLevelHealthBoost(JujutsucraftModVariables.PlayerVariables playerVars) {
        return PhysicalAbilityTuning.highLevelHealthBoost(playerVars.PlayerLevel);
    }

    public static double uncapArmor(double value, double max) {
        return PhysicalAbilityTuning.uncapArmor(value, max);
    }

    public static double uncapArmorToughness(double value, double max) {
        return PhysicalAbilityTuning.uncapArmorToughness(value, max);
    }

    public static void applyHealthBoostSetHealth(LivingEntity livingEntity, float health, Operation<Void> original) {
        if (!FirstAidMutationService.applyPlayerPhysicalAbilityHealthBoostRestore(livingEntity)) {
            original.call(livingEntity, health);
        }
    }

    public static int bypassOuterTickParity(int tickCount) {
        return PhysicalAbilityTuning.isEvenTick(tickCount) ? tickCount : tickCount - 1;
    }

    public static boolean shouldApplyFlyEffectThisTick(int tickCount) {
        return PhysicalAbilityTuning.isEvenTick(tickCount);
    }

    public static boolean shouldApplyFlyEffectThisTick(Entity entity) {
        return entity != null && shouldApplyFlyEffectThisTick(entity.tickCount);
    }

    public static int resolveSpeedDuration(int interval) {
        return PhysicalAbilityTuning.toProgressiveBuffDuration(interval);
    }

    public static int resolveSpeedDuration() {
        return resolveSpeedDuration(PhysicalAbilityTuning.getBuffIncreaseInterval());
    }

    public static int resolveJumpDuration(boolean crouching, int interval) {
        return crouching ? resolveSpeedDuration(interval) : DEFAULT_AIRBORNE_JUMP_DURATION;
    }

    public static int resolveJumpDuration(Entity entity) {
        return resolveJumpDuration(entity != null && entity.isCrouching(), PhysicalAbilityTuning.getBuffIncreaseInterval());
    }

    public static int adjustProgressiveBuffRemainingDuration(int remainingDuration) {
        return remainingDuration + (ORIGINAL_PROGRESSIVE_BUFF_REAPPLY_THRESHOLD - PhysicalAbilityTuning.getProgressiveBuffReapplyThreshold());
    }

    public static MobEffectInstance configureSpeedEffect(MobEffectInstance effectInstance) {
        return withDuration(effectInstance, resolveSpeedDuration());
    }

    public static MobEffectInstance configureJumpEffect(Entity entity, MobEffectInstance effectInstance) {
        return withDuration(effectInstance, resolveJumpDuration(entity));
    }

    public static void applyKaoriFlyEffect(Entity entity) {
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(entity);
        if (!(entity instanceof LivingEntity livingEntity)
            || playerVariables == null
            || !shouldApplyFlyEffectThisTick(entity)
            || !FlyEffectGrantRules.shouldApplyKaoriPassiveFlySupplement(
                playerVariables.PlayerCurseTechnique,
                playerVariables.PlayerCurseTechnique2,
                LogicStartPassiveProcedure.execute(entity)
            )) {
            return;
        }
        FlyEffectGrantRules.applyGroundedFlyEffect(livingEntity, FlyEffectGrantRules.PASSIVE_FLY_INCREMENT);
    }

    public static void applyGojoProgression(Entity entity) {
        GojoProgressionService.tick(entity);
    }

    public static void applyObservedDoubleJumpEffect(Entity entity) {
        ObservedDoubleJumpUnlockService.applyUnlockedEffect(entity);
    }

    public static boolean hasCompletePhysicalGifted(MobEffectInstance physicalGiftedEffect) {
        return physicalGiftedEffect != null && physicalGiftedEffect.getAmplifier() >= COMPLETE_PHYSICAL_GIFTED_AMPLIFIER;
    }

    public static boolean shouldSupplementCompletePhysicalGiftedRegen(
        MobEffectInstance physicalGiftedEffect,
        MobEffectInstance regenerationEffect
    ) {
        if (!hasCompletePhysicalGifted(physicalGiftedEffect)) {
            return false;
        }
        if (regenerationEffect == null) {
            return true;
        }
        if (regenerationEffect.getAmplifier() > 0) {
            return false;
        }
        return regenerationEffect.getDuration() != JJA_INFINITE_REGEN_DURATION;
    }

    public static void applyCompletePhysicalGiftedRegeneration(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity.level().isClientSide()) {
            return;
        }
        MobEffectInstance physicalGiftedEffect = livingEntity.getEffect(JujutsucraftModMobEffects.PHYSICAL_GIFTED_EFFECT.get());
        MobEffectInstance regenerationEffect = livingEntity.getEffect(MobEffects.REGENERATION);
        if (!shouldSupplementCompletePhysicalGiftedRegen(physicalGiftedEffect, regenerationEffect)) {
            return;
        }
        livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, JJA_INFINITE_REGEN_DURATION, 0, false, false));
    }

    private static MobEffectInstance withDuration(MobEffectInstance effectInstance, int duration) {
        if (effectInstance.getDuration() == duration) {
            return effectInstance;
        }
        return new MobEffectInstance(
            effectInstance.getEffect(),
            duration,
            effectInstance.getAmplifier(),
            effectInstance.isAmbient(),
            effectInstance.isVisible(),
            effectInstance.showIcon()
        );
    }
}
