package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.bf.BlackFlashDebugLogger;
import com.arf8vhg7.jja.hook.jujutsucraft.RangeAttackProcedureHook;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyCombatPassContext;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyCombatPassContext.PassKind;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.mcreator.jujutsucraft.procedures.RangeAttackProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RangeAttackProcedure.class, remap = false)
public abstract class RangeAttackProcedureMixin {
    private static final ThreadLocal<Boolean> JJA_COMBAT_REPLAY_ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> JJA_COMBAT_ECHO_PRIMARY_HIT = ThreadLocal.withInitial(() -> false);

    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1
    )
    private static void jja$runTwinnedBodyCombatEcho(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        if (entity == null || JJA_COMBAT_REPLAY_ACTIVE.get() || !RangeAttackProcedureHook.shouldReplayCombatEcho(entity)) {
            return;
        }

        ci.cancel();
        JJA_COMBAT_ECHO_PRIMARY_HIT.set(false);
        CompoundTag combatSnapshot = RangeAttackProcedureHook.captureCombatRuntimeSnapshot(entity);
        JJA_COMBAT_REPLAY_ACTIVE.set(true);
        try {
            try {
                TwinnedBodyCombatPassContext.withScope(entity, null, PassKind.PRIMARY, () -> RangeAttackProcedure.execute(world, x, y, z, entity));
            } finally {
                RangeAttackProcedureHook.restoreCombatRuntimeSnapshot(entity, combatSnapshot);
            }
            if (!JJA_COMBAT_ECHO_PRIMARY_HIT.get()) {
                return;
            }

            RangeAttackProcedureHook.markCombatEchoConsumed(entity);
            TwinnedBodyCombatPassContext.withScope(entity, null, PassKind.ECHO, () -> RangeAttackProcedure.execute(world, x, y, z, entity));
        } finally {
            JJA_COMBAT_REPLAY_ACTIVE.remove();
            JJA_COMBAT_ECHO_PRIMARY_HIT.remove();
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z"
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$skipBrainDamageRemoval(LivingEntity livingEntity, MobEffect effect, Operation<Boolean> original) {
        return RangeAttackProcedureHook.removeEffect(livingEntity, effect);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$skipBrainDamageReapply(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        return RangeAttackProcedureHook.addEffect(livingEntity, effectInstance);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            ordinal = 0
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$syncFirstAidProbeDamage(
        Entity entity,
        DamageSource damageSource,
        float amount,
        Operation<Boolean> original,
        @Share("jjaFirstAidDamageApplied") LocalBooleanRef firstAidDamageApplied
    ) {
        firstAidDamageApplied.set(false);
        CompoundTag snapshot = RangeAttackProcedureHook.captureTrackedHurtSnapshot(entity);
        boolean result = original.call(entity, damageSource, amount);
        firstAidDamageApplied.set(RangeAttackProcedureHook.resolveTrackedHurtDamageApplied(entity, damageSource, amount, snapshot, false));
        return result;
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            ordinal = 1
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$syncFirstAidAppliedDamage(
        Entity target,
        DamageSource damageSource,
        float amount,
        Operation<Boolean> original,
        @Share("jjaFirstAidDamageApplied") LocalBooleanRef firstAidDamageApplied,
        @Share("jjaBlackFlashDamage") LocalDoubleRef blackFlashDamage,
        @Local(argsOnly = true) Entity entity
    ) {
        CompoundTag snapshot = RangeAttackProcedureHook.captureTrackedHurtSnapshot(target);
        Entity attackSource = damageSource.getEntity();
        PassKind passKind = TwinnedBodyCombatPassContext.currentPassKind();
        float healthBefore = target instanceof LivingEntity livingTarget ? livingTarget.getHealth() : Float.NaN;
        if (attackSource != null && passKind != null && TwinnedBodyCombatPassContext.hasContext()) {
            try (TwinnedBodyCombatPassContext.Scope ignored = TwinnedBodyCombatPassContext.enter(attackSource, target, passKind)) {
                boolean result = original.call(target, damageSource, amount);
                boolean trackedDamageApplied = RangeAttackProcedureHook.resolveTrackedHurtDamageApplied(target, damageSource, amount, snapshot, true);
                firstAidDamageApplied.set(trackedDamageApplied);
                double dealtDamage = resolveDealtDamage(healthBefore, target, amount);
                recordCombatEchoPrimaryHit(passKind, trackedDamageApplied, dealtDamage);
                logCombatDamage(entity, passKind, amount, dealtDamage, target, blackFlashDamage);
                return result;
            }
        }

        boolean result = original.call(target, damageSource, amount);
        boolean trackedDamageApplied = RangeAttackProcedureHook.resolveTrackedHurtDamageApplied(target, damageSource, amount, snapshot, true);
        firstAidDamageApplied.set(trackedDamageApplied);
        double dealtDamage = resolveDealtDamage(healthBefore, target, amount);
        recordCombatEchoPrimaryHit(passKind, trackedDamageApplied, dealtDamage);
        logCombatDamage(entity, passKind, amount, dealtDamage, target, blackFlashDamage);
        return result;
    }

    private static void recordCombatEchoPrimaryHit(PassKind passKind, boolean trackedDamageApplied, double dealtDamage) {
        if (passKind == PassKind.PRIMARY && (trackedDamageApplied || dealtDamage > 0.0D)) {
            JJA_COMBAT_ECHO_PRIMARY_HIT.set(true);
        }
    }

    private static double resolveDealtDamage(float healthBefore, Entity target, float rawAmount) {
        float healthAfter = target instanceof LivingEntity livingTarget ? livingTarget.getHealth() : Float.NaN;
        return !Float.isNaN(healthBefore) && !Float.isNaN(healthAfter)
            ? Math.max(0.0D, healthBefore - healthAfter)
            : rawAmount;
    }

    private static void logCombatDamage(
        Entity attacker,
        PassKind passKind,
        float rawAmount,
        double dealtDamage,
        Entity target,
        LocalDoubleRef blackFlashDamage
    ) {
        if (dealtDamage <= 0.0D) {
            return;
        }

        blackFlashDamage.set(dealtDamage);
        BlackFlashDebugLogger.logDamage(attacker, target, passKind, rawAmount, dealtDamage);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 0
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareHealthForBlackFlash(float currentHealth, @Local(argsOnly = true) Entity entity) {
        return RangeAttackProcedureHook.getEffectiveHealth(entity, currentHealth);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 1
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareHealthForProbeOldHealth(float currentHealth, @Local(name = "entityiterator") Entity entityIterator) {
        return RangeAttackProcedureHook.getEffectiveHealth(entityIterator, currentHealth);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 2
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$respectFirstAidAppliedDamageForProbeHitSuccess(
        float currentHealth,
        @Local(name = "old_health") double oldHealth,
        @Local(name = "entityiterator") Entity entityIterator,
        @Share("jjaFirstAidDamageApplied") LocalBooleanRef firstAidDamageApplied
    ) {
        float effectiveHealth = RangeAttackProcedureHook.getEffectiveHealth(entityIterator, currentHealth);
        return RangeAttackProcedureHook.forceChangedHealthForComparison(effectiveHealth, oldHealth, firstAidDamageApplied.get());
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 3
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareHealthForDamageOldHealth(float currentHealth, @Local(name = "entityiterator") Entity entityIterator) {
        return RangeAttackProcedureHook.getEffectiveHealth(entityIterator, currentHealth);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 4
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$respectFirstAidAppliedDamageForHitSuccess(
        float currentHealth,
        @Local(name = "old_health") double oldHealth,
        @Local(name = "entityiterator") Entity entityIterator,
        @Share("jjaFirstAidDamageApplied") LocalBooleanRef firstAidDamageApplied
    ) {
        float effectiveHealth = RangeAttackProcedureHook.getEffectiveHealth(entityIterator, currentHealth);
        return RangeAttackProcedureHook.forceChangedHealthForComparison(effectiveHealth, oldHealth, firstAidDamageApplied.get());
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 5
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareTargetHealthForBlastGameKnockback(float currentHealth, @Local(name = "entityiterator") Entity entityIterator) {
        return RangeAttackProcedureHook.getEffectiveHealth(entityIterator, currentHealth);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$MapVariables;DomainExpansionRadius:D",
            opcode = Opcodes.GETFIELD
        ),
        remap = false,
        require = 1
    )
    private static double jja$resolveCurrentDomainAttackRadius(
        double radius,
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity
    ) {
        return RangeAttackProcedureHook.resolveCurrentRadius(entity, radius);
    }

    @ModifyArg(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;round(D)J"
        ),
        index = 0,
        remap = false,
        require = 1
    )
    private static double jja$adjustBlackFlashChance(
        double original,
        @Local(argsOnly = true) Entity entity
    ) {
        return RangeAttackProcedureHook.adjustBlackFlashChance(entity, original);
    }

    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerTechniqueUsedNumber:D",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
        ),
        remap = false,
        require = 1
    )
    private static void jja$refreshCursePowerFormerAfterBlackFlashUsage(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo callbackInfo
    ) {
        RangeAttackProcedureHook.refreshPlayerCursePowerFormer(entity);
    }

    @Inject(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/EntityType;m_262496_(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;)Lnet/minecraft/world/entity/Entity;"
        ),
        remap = false,
        require = 1
    )
    private static void jja$incrementBfRandedOnSuccess(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo callbackInfo) {
        RangeAttackProcedureHook.incrementBfRanded(entity);
    }

    @Inject(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/EntityType;m_262496_(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;)Lnet/minecraft/world/entity/Entity;"
        ),
        remap = false,
        require = 1
    )
    private static void jja$logBlackFlashOnSuccess(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo callbackInfo,
        @Share("jjaBlackFlashDamage") LocalDoubleRef blackFlashDamage,
        @Local(name = "entityiterator") Entity entityIterator
    ) {
        BlackFlashDebugLogger.logBlackFlash(
            entity,
            entityIterator,
            TwinnedBodyCombatPassContext.currentPassKind(),
            blackFlashDamage.get()
        );
    }
}
