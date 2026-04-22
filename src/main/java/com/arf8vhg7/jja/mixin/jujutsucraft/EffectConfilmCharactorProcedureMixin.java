package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.EffectConfilmCharactorProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.mcreator.jujutsucraft.procedures.EffectConfilmCharactorProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EffectConfilmCharactorProcedure.class, remap = false)
public abstract class EffectConfilmCharactorProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$addInfinityEffect(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        return EffectConfilmCharactorProcedureHook.addInfinityEffect(livingEntity, effectInstance);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 2
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$allowDomainTechniquesToBypassInfinityBlock(
        boolean targetHasInfinity,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true, ordinal = 0) Entity entity,
        @Local(argsOnly = true, ordinal = 1) Entity entityIterator
    ) {
        return EffectConfilmCharactorProcedureHook.shouldProcessInfinityBlock(world, entity, entityIterator, targetHasInfinity);
    }

    @WrapOperation(
        method = "execute",
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
        return EffectConfilmCharactorProcedureHook.resolveEquipmentRead(
            livingEntity,
            equipmentSlot,
            original.call(livingEntity, equipmentSlot)
        );
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static float jja$useFirstAidAwareProbeOldHealth(float currentHealth, @Local(argsOnly = true, ordinal = 1) Entity entityIterator) {
        return EffectConfilmCharactorProcedureHook.getEffectiveHealth(entityIterator, currentHealth);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$syncFirstAidProbeDamage(
        Entity entity,
        DamageSource damageSource,
        float amount,
        Operation<Boolean> original,
        @Share("jjaFirstAidDamageApplied") LocalBooleanRef firstAidDamageApplied
    ) {
        CompoundTag snapshot = EffectConfilmCharactorProcedureHook.captureTrackedHurtSnapshot(entity);
        boolean result = original.call(entity, damageSource, amount);
        firstAidDamageApplied.set(EffectConfilmCharactorProcedureHook.resolveTrackedHurtDamageApplied(entity, damageSource, amount, snapshot));
        return result;
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static float jja$respectFirstAidAppliedDamageForProbeCompare(
        float currentHealth,
        @Local(name = "old_health") double oldHealth,
        @Local(argsOnly = true, ordinal = 1) Entity entityIterator,
        @Share("jjaFirstAidDamageApplied") LocalBooleanRef firstAidDamageApplied
    ) {
        float effectiveHealth = EffectConfilmCharactorProcedureHook.getEffectiveHealth(entityIterator, currentHealth);
        return EffectConfilmCharactorProcedureHook.forceChangedHealthForComparison(effectiveHealth, oldHealth, firstAidDamageApplied.get());
    }
}
