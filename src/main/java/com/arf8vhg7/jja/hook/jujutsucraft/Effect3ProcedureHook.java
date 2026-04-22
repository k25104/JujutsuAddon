package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentMutationService;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class Effect3ProcedureHook {
    private Effect3ProcedureHook() {
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }

    public static boolean handlePlayerArmorInventorySet(Entity entity, int armorIndex, ItemStack stack) {
        return CuriosEquipmentMutationService.handlePlayerArmorInventorySet(entity, armorIndex, stack);
    }

    public static boolean handleLivingArmorSet(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack stack) {
        return CuriosEquipmentMutationService.handleLivingArmorSet(livingEntity, equipmentSlot, stack);
    }
}
