package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.jja.technique.family.mahoraga.MahoragaAdaptation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class CursedToolsAbilityProcedureHook {
    private CursedToolsAbilityProcedureHook() {
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }

    public static double resolveAdaptationProgress(Entity entity, String key, double originalValue) {
        return MahoragaAdaptation.resolveRegistrationProgress(entity, key, originalValue, null, false);
    }

    public static boolean shouldDisplayAdaptationStart(Entity entity) {
        return MahoragaAdaptation.consumeStartMessageFlag(entity);
    }
}
