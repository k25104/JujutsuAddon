package com.arf8vhg7.jja.hook.minecraft.client;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosArmorOverrideService;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public final class HumanoidArmorLayerHook {
    private HumanoidArmorLayerHook() {
    }

    public static boolean shouldSuppressVanillaArmorRender(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        return CuriosArmorOverrideService.shouldSuppressVanillaArmorRender(livingEntity, equipmentSlot);
    }
}