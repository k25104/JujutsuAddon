package com.arf8vhg7.jja.feature.equipment.curios;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CuriosEquipmentReadService {
    private CuriosEquipmentReadService() {
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentMutationService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }

    public static boolean isManagedItem(ItemStack stack) {
        return CuriosEquipmentMutationService.isManagedItem(stack);
    }

    @Nullable
    public static CuriosLogicalSlot resolveManagedLogicalSlot(ItemStack stack) {
        return CuriosEquipmentMutationService.resolveManagedLogicalSlot(stack);
    }

    public static void forEachManagedItem(Consumer<Item> consumer) {
        CuriosEquipmentMutationService.forEachManagedItem(consumer);
    }

    public static void visitManagedCuriosStacks(LivingEntity livingEntity, Consumer<ItemStack> consumer) {
        CuriosEquipmentMutationService.visitManagedCuriosStacks(livingEntity, consumer);
    }

    public static boolean isManagedForEquipmentSlot(ItemStack stack, EquipmentSlot equipmentSlot) {
        return CuriosEquipmentMutationService.isManagedForEquipmentSlot(stack, equipmentSlot);
    }

    public static boolean isManagedForLogicalSlot(ItemStack stack, CuriosLogicalSlot logicalSlot) {
        return CuriosEquipmentMutationService.isManagedForLogicalSlot(stack, logicalSlot);
    }

    public static boolean isArmorOverrideItem(ItemStack stack) {
        return CuriosManagedItemRegistry.isArmorOverrideItem(stack);
    }

    public static boolean isArmorOverrideForLogicalSlot(ItemStack stack, CuriosLogicalSlot logicalSlot) {
        return CuriosManagedItemRegistry.isArmorOverrideForLogicalSlot(stack, logicalSlot);
    }

    public static void forEachArmorOverrideItem(Consumer<Item> consumer) {
        CuriosManagedItemRegistry.forEachArmorOverrideItem(consumer);
    }
}
