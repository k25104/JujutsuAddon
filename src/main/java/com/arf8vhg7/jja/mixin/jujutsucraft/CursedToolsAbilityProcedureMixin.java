package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.CursedToolsAbilityProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.CursedToolsAbilityProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CursedToolsAbilityProcedure.class, remap = false)
public abstract class CursedToolsAbilityProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128347_(Ljava/lang/String;D)V"
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
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        original.call(tag, key, CursedToolsAbilityProcedureHook.resolveAdaptationProgress(entity, key, value));
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
        if (CursedToolsAbilityProcedureHook.shouldDisplayAdaptationStart(player)) {
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
        return CursedToolsAbilityProcedureHook.resolveEquipmentRead(
            livingEntity,
            equipmentSlot,
            original.call(livingEntity, equipmentSlot)
        );
    }
}
