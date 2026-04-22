package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class CursedTechniqueOnPotionActiveTickProcedureHook {
    private CursedTechniqueOnPotionActiveTickProcedureHook() {
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }
}
