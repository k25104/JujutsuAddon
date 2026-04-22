package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentMutationService;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class AISukunaProcedureHook {
    private AISukunaProcedureHook() {
    }

    public static MobEffectInstance getEffect(LivingEntity livingEntity, MobEffect effect) {
        return DomainExpansionHookSupport.getThresholdDurationEffect(livingEntity, effect);
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
