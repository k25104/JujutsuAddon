package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AISukunaProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.NonNullList;
import net.mcreator.jujutsucraft.procedures.AISukunaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(value = AISukunaProcedure.class, remap = false)
@SuppressWarnings("deprecation")
public abstract class AISukunaProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21124_(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"
        ),
        remap = false
    ,
        require = 1
    )
    private static MobEffectInstance jja$replaceDomainDuration(LivingEntity livingEntity, MobEffect effect, Operation<MobEffectInstance> original) {
        return AISukunaProcedureHook.getEffect(livingEntity, effect);
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
        return AISukunaProcedureHook.resolveEquipmentRead(livingEntity, equipmentSlot, original.call(livingEntity, equipmentSlot));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"
        ),
        remap = false,
        require = 1
    )
    private static Object jja$equipManagedViaCuriosOnPlayerSet(
        NonNullList<?> stacks,
        int armorIndex,
        Object stack,
        Operation<Object> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (stack instanceof ItemStack itemStack
            && AISukunaProcedureHook.handlePlayerArmorInventorySet(entity, armorIndex, itemStack)) {
            return stack;
        }

        return original.call(stacks, armorIndex, stack);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_8061_(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$equipManagedViaCuriosOnLivingSet(
        LivingEntity livingEntity,
        EquipmentSlot equipmentSlot,
        ItemStack stack,
        Operation<Void> original
    ) {
        if (!AISukunaProcedureHook.handleLivingArmorSet(livingEntity, equipmentSlot, stack)) {
            original.call(livingEntity, equipmentSlot, stack);
        }
    }
}
