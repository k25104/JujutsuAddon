package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentMutationService;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class MythicalBeastAmberEffectOnEffectActiveTickProcedureHook {
    private MythicalBeastAmberEffectOnEffectActiveTickProcedureHook() {
    }

    public static int modifyTickInterval(int original) {
        return 1;
    }

    public static double modifyCursePowerDrain(double original) {
        return 1.0;
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }

    public static boolean shouldReturnPreviousArmor(Entity entity, int armorIndex, ItemStack stack) {
        return !CuriosEquipmentMutationService.willHandlePlayerArmorInventorySet(entity, armorIndex, stack);
    }

    public static boolean handlePlayerArmorInventorySet(Entity entity, int armorIndex, ItemStack stack) {
        return CuriosEquipmentMutationService.handlePlayerArmorInventorySet(entity, armorIndex, stack);
    }

    public static boolean handleLivingArmorSet(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack stack) {
        return CuriosEquipmentMutationService.handleLivingArmorSet(livingEntity, equipmentSlot, stack);
    }

    public static int clearMatchingManagedItems(Entity entity, Predicate<ItemStack> predicate, int maxCount) {
        return entity instanceof LivingEntity livingEntity
            ? CuriosEquipmentMutationService.clearMatchingManagedItems(livingEntity, predicate, maxCount)
            : 0;
    }
}
