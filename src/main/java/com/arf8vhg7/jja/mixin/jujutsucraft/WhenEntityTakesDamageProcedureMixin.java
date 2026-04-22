package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.WhenEntityTakesDamageProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.mcreator.jujutsucraft.procedures.WhenEntityTakesDamageProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WhenEntityTakesDamageProcedure.class, remap = false)
public abstract class WhenEntityTakesDamageProcedureMixin {
    private static final String JJA_DAMAGE_EXECUTE_METHOD =
        "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;D)V";

    @WrapOperation(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            ordinal = 1
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$syncFirstAidCombatDamageWithMinepiece(
        Entity entity,
        DamageSource damageSource,
        float amount,
        Operation<Boolean> original,
        @Share("jjaFirstAidDamageApplied") LocalBooleanRef firstAidDamageApplied
    ) {
        CompoundTag snapshot = WhenEntityTakesDamageProcedureHook.captureTrackedHurtSnapshot(entity);
        boolean result = original.call(entity, damageSource, amount);
        firstAidDamageApplied.set(WhenEntityTakesDamageProcedureHook.resolveTrackedHurtDamageApplied(entity, damageSource, amount, snapshot, true));
        return result;
    }

    @WrapOperation(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            ordinal = 2
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$syncFirstAidCombatDamage(
        Entity entity,
        DamageSource damageSource,
        float amount,
        Operation<Boolean> original,
        @Share("jjaFirstAidDamageApplied") LocalBooleanRef firstAidDamageApplied
    ) {
        CompoundTag snapshot = WhenEntityTakesDamageProcedureHook.captureTrackedHurtSnapshot(entity);
        boolean result = original.call(entity, damageSource, amount);
        firstAidDamageApplied.set(WhenEntityTakesDamageProcedureHook.resolveTrackedHurtDamageApplied(entity, damageSource, amount, snapshot, true));
        return result;
    }

    @ModifyExpressionValue(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 0
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareOldHealthCapture(float currentHealth, @Local(argsOnly = true, ordinal = 0) Entity entity) {
        return WhenEntityTakesDamageProcedureHook.getEffectiveHealth(entity, currentHealth);
    }

    @ModifyExpressionValue(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 1
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$respectFirstAidAppliedDamageForDamageSuccess(
        float currentHealth,
        @Local(name = "old_health") double oldHealth,
        @Local(argsOnly = true, ordinal = 0) Entity entity,
        @Share("jjaFirstAidDamageApplied") LocalBooleanRef firstAidDamageApplied
    ) {
        float effectiveHealth = WhenEntityTakesDamageProcedureHook.getEffectiveHealth(entity, currentHealth);
        return WhenEntityTakesDamageProcedureHook.forceChangedHealthForComparison(effectiveHealth, oldHealth, firstAidDamageApplied.get());
    }

    @ModifyExpressionValue(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 2
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareDamageDelta(float currentHealth, @Local(argsOnly = true, ordinal = 0) Entity entity) {
        return WhenEntityTakesDamageProcedureHook.getEffectiveHealth(entity, currentHealth);
    }

    @ModifyExpressionValue(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 3
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$respectFirstAidAppliedDamageForDamageEffect(
        float currentHealth,
        @Local(name = "old_health") double oldHealth,
        @Local(argsOnly = true, ordinal = 0) Entity entity,
        @Share("jjaFirstAidDamageApplied") LocalBooleanRef firstAidDamageApplied
    ) {
        float effectiveHealth = WhenEntityTakesDamageProcedureHook.getEffectiveHealth(entity, currentHealth);
        return WhenEntityTakesDamageProcedureHook.forceChangedHealthForComparison(effectiveHealth, oldHealth, firstAidDamageApplied.get());
    }

    @WrapOperation(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/CounterProcedure;execute(Lnet/minecraft/world/entity/Entity;)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$suppressMahitoCounterRetaliation(
        Entity defender,
        Operation<Void> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true, ordinal = 1) Entity immediateSourceEntity,
        @Local(argsOnly = true, ordinal = 2) Entity sourceEntity
    ) {
        if (WhenEntityTakesDamageProcedureHook.shouldSuppressCounterForMahitoAttack(
            world,
            defender,
            immediateSourceEntity,
            sourceEntity
        )) {
            return;
        }

        original.call(defender);
    }

    @WrapOperation(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 16
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$allowFbeDefenseOutsideDomains(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        return WhenEntityTakesDamageProcedureHook.allowFbeOutsideDomain(original.call(livingEntity, effect), entity);
    }

    @WrapOperation(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 18
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$allowFbeDefenseWithoutNeutralization(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        return WhenEntityTakesDamageProcedureHook.allowFbeWithoutNeutralization(original.call(livingEntity, effect), entity);
    }

    @WrapOperation(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21124_(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;",
            ordinal = 4
        ),
        remap = false,
        require = 1
    )
    private static MobEffectInstance jja$ensureFbeNeutralizationGateCanPass(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<MobEffectInstance> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        return WhenEntityTakesDamageProcedureHook.resolveFbeNeutralizationGateEffect(original.call(livingEntity, effect), effect, entity);
    }

    @WrapOperation(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21124_(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;",
            ordinal = 10
        ),
        remap = false,
        require = 1
    )
    private static MobEffectInstance jja$restoreDomainBreakDurationThreshold(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<MobEffectInstance> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        return WhenEntityTakesDamageProcedureHook.resolveDomainBreakDuration(original.call(livingEntity, effect), effect, entity);
    }

    @WrapOperation(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 31
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$ignoreInfinityForDomainSureHit(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true, ordinal = 0) Entity entity,
        @Local(argsOnly = true, ordinal = 2) Entity sourceEntity
    ) {
        if (WhenEntityTakesDamageProcedureHook.shouldIgnoreInfinityForDomainAttack(world, sourceEntity, entity)) {
            return false;
        }

        return WhenEntityTakesDamageProcedureHook.resolveInfinityProtection(original.call(livingEntity, effect), entity);
    }

    @WrapOperation(
        method = JJA_DAMAGE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_6844_(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
        ),
        remap = false,
        require = 1
    )
    private static ItemStack jja$resolveCuriosEquipmentRead(
        LivingEntity livingEntity,
        EquipmentSlot equipmentSlot,
        Operation<ItemStack> original
    ) {
        return WhenEntityTakesDamageProcedureHook.resolveEquipmentRead(
            livingEntity,
            equipmentSlot,
            original.call(livingEntity, equipmentSlot)
        );
    }

    @Inject(method = JJA_DAMAGE_EXECUTE_METHOD, at = @At("TAIL"), remap = false, require = 1)
    private static void jja$breakMahoragaDomainOnRegisteredAttack(
        Event event,
        LevelAccessor world,
        DamageSource damageSource,
        Entity entity,
        Entity immediateSourceEntity,
        Entity sourceEntity,
        double amount,
        CallbackInfo callbackInfo
    ) {
        WhenEntityTakesDamageProcedureHook.breakMahoragaDomainOnRegisteredAttack(world, entity, immediateSourceEntity, sourceEntity);
    }
}
