package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.MythicalBeastAmberEffectEffectStartedappliedProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.procedures.MythicalBeastAmberEffectEffectStartedappliedProcedure;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MythicalBeastAmberEffectEffectStartedappliedProcedure.class, remap = false)
@SuppressWarnings("deprecation")
public abstract class MythicalBeastAmberEffectEffectStartedappliedProcedureMixin {
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
        return MythicalBeastAmberEffectEffectStartedappliedProcedureHook.resolveEquipmentRead(
            livingEntity,
            equipmentSlot,
            original.call(livingEntity, equipmentSlot)
        );
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V",
            ordinal = 2
        ),
        remap = false,
        require = 1
    )
    private static void jja$returnPreviousHeadArmor(
        Player player,
        ItemStack stack,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (MythicalBeastAmberEffectEffectStartedappliedProcedureHook.shouldReturnPreviousArmor(
            entity,
            3,
            new ItemStack((ItemLike) JujutsucraftModItems.MYTHICAL_BEAST_AMBER_HELMET.get())
        )) {
            original.call(player, stack);
        }
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
            && MythicalBeastAmberEffectEffectStartedappliedProcedureHook.handlePlayerArmorInventorySet(entity, armorIndex, itemStack)) {
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
        if (!MythicalBeastAmberEffectEffectStartedappliedProcedureHook.handleLivingArmorSet(livingEntity, equipmentSlot, stack)) {
            original.call(livingEntity, equipmentSlot, stack);
        }
    }
}
