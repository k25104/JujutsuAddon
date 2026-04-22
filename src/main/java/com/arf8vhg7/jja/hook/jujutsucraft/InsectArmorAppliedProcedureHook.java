package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentCommandService;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import java.util.function.IntSupplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class InsectArmorAppliedProcedureHook {
    private InsectArmorAppliedProcedureHook() {
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }

    public static boolean shouldReturnPreviousArmor(Entity entity, String command) {
        return !CuriosEquipmentCommandService.willHandleEquipCommand(entity, command);
    }

    public static int runArmorCommand(String command, Entity entity, IntSupplier fallback) {
        if (CuriosEquipmentCommandService.tryHandleEquipCommand(entity, command)) {
            return 1;
        }
        int result = fallback.getAsInt();
        CuriosEquipmentCommandService.handlePostCommandCleanup(entity, command);
        return result;
    }
}
