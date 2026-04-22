package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedure.class, remap = false)
@SuppressWarnings("deprecation")
public abstract class InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedureMixin {
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
        return InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedureHook.resolveEquipmentRead(
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
        if (InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedureHook.shouldReturnPreviousArmor(
            entity,
            "item replace entity @s armor.head with jujutsucraft:armor_instant_spirit_bodyof_distorted_killing_helmet"
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
        if (InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedureHook.shouldReturnPreviousArmor(
            entity,
            "item replace entity @s armor.chest with jujutsucraft:armor_instant_spirit_bodyof_distorted_killing_chestplate"
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
        if (InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedureHook.shouldReturnPreviousArmor(
            entity,
            "item replace entity @s armor.legs with jujutsucraft:armor_instant_spirit_bodyof_distorted_killing_leggings"
        )) {
            original.call(player, stack);
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;m_230957_(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"
        ),
        remap = false,
        require = 1
    )
    private static int jja$runArmorCommand(
        Commands commands,
        CommandSourceStack commandSourceStack,
        String command,
        Operation<Integer> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return InstantSpiritBodyofDistortedKillingEffectEffectStartedappliedProcedureHook.runArmorCommand(
            command,
            entity,
            () -> original.call(commands, commandSourceStack, command)
        );
    }
}
