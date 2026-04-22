package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.procedures.InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedure;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import java.util.function.Predicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedure.class, remap = false)
@SuppressWarnings("deprecation")
public abstract class InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedureMixin {
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
        return InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedureHook.resolveEquipmentRead(
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
            ordinal = 0
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
        if (InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedureHook.shouldReturnPreviousArmor(
            entity,
            3,
            new ItemStack((ItemLike) JujutsucraftModItems.ARMOR_INSTANT_SPIRIT_BODYOF_DISTORTED_KILLING_HELMET.get())
        )) {
            original.call(player, stack);
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static void jja$returnPreviousChestArmor(
        Player player,
        ItemStack stack,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedureHook.shouldReturnPreviousArmor(
            entity,
            2,
            new ItemStack((ItemLike) JujutsucraftModItems.ARMOR_INSTANT_SPIRIT_BODYOF_DISTORTED_KILLING_CHESTPLATE.get())
        )) {
            original.call(player, stack);
        }
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
    private static void jja$returnPreviousLegsArmor(
        Player player,
        ItemStack stack,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedureHook.shouldReturnPreviousArmor(
            entity,
            1,
            new ItemStack((ItemLike) JujutsucraftModItems.ARMOR_INSTANT_SPIRIT_BODYOF_DISTORTED_KILLING_LEGGINGS.get())
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
            && InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedureHook.handlePlayerArmorInventorySet(entity, armorIndex, itemStack)) {
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
        if (!InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedureHook.handleLivingArmorSet(livingEntity, equipmentSlot, stack)) {
            original.call(livingEntity, equipmentSlot, stack);
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;m_36022_(Ljava/util/function/Predicate;ILnet/minecraft/world/Container;)I"
        ),
        remap = false,
        require = 1
    )
    private static int jja$clearManagedCuriosCopies(
        Inventory inventory,
        Predicate<ItemStack> predicate,
        int maxCount,
        Container container,
        Operation<Integer> original,
        @Local(argsOnly = true) Entity entity
    ) {
        int removed = InstantSpiritBodyofDistortedKillingEffectOnEffectActiveTickProcedureHook.clearMatchingManagedItems(entity, predicate, maxCount);
        if (removed >= maxCount) {
            return removed;
        }
        return removed + original.call(inventory, predicate, maxCount - removed, container);
    }
}
