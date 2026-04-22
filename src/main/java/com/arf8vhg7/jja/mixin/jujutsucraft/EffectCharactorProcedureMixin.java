package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.EffectCharactorProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.EffectCharactorProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EffectCharactorProcedure.class, remap = false)
@SuppressWarnings("deprecation")
public abstract class EffectCharactorProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;m_41714_(Lnet/minecraft/network/chat/Component;)Lnet/minecraft/world/item/ItemStack;"),
        remap = false
    ,
        require = 1
    )
    private static ItemStack jja$setHoverName(
        ItemStack itemStack,
        Component component,
        Operation<ItemStack> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        Component resolved = EffectCharactorProcedureHook.buildCopiedTechniqueHoverName(world, entity, itemStack, component);
        return original.call(itemStack, resolved);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128347_(Ljava/lang/String;D)V",
            ordinal = 8
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$resolveAdaptationProgress(
        CompoundTag tag,
        String key,
        double value,
        Operation<Void> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity,
        @Local(argsOnly = true, ordinal = 1) Entity entityiterator
    ) {
        original.call(tag, key, EffectCharactorProcedureHook.resolveAdaptationProgress(entityiterator, entity, key, value));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V"
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$displayAdaptationStart(
        Player player,
        Component component,
        boolean overlay,
        Operation<Void> original
    ) {
        if (EffectCharactorProcedureHook.shouldDisplayAdaptationStart(player)) {
            original.call(player, component, overlay);
        }
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
        return EffectCharactorProcedureHook.resolveEquipmentRead(
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
    private static void jja$limitCopiedTechniqueStackGrowth(Player player, ItemStack stack, Operation<Void> original) {
        if (EffectCharactorProcedureHook.shouldGiveCopiedTechnique(player, stack)) {
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
    private static void jja$limitCopiedTechniqueNewGrant(Player player, ItemStack stack, Operation<Void> original) {
        if (EffectCharactorProcedureHook.shouldGiveCopiedTechnique(player, stack)) {
            original.call(player, stack);
        }
    }
}
