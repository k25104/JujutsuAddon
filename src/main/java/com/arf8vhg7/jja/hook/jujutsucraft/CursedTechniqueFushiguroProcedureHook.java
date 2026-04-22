package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.display.JjaTechniqueNameKeyResolver;
import com.arf8vhg7.jja.feature.jja.technique.shared.slot.RegisteredCurseTechniqueSlots;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentMutationService;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiCancelRecallService;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowService;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class CursedTechniqueFushiguroProcedureHook {
    private CursedTechniqueFushiguroProcedureHook() {
    }

    public static String jjaGetKeyOrString(Component component) {
        return JjaTechniqueNameKeyResolver.jjaGetKeyOrString(component);
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

    public static String resolveSelectTechniqueName(Entity entity, String currentName) {
        return RegisteredCurseTechniqueSlots.resolveRegisteredTechniqueName(entity, currentName);
    }

    public static void handleReturnShadow(Entity entity, Runnable fallback) {
        if (!MegumiCancelRecallService.tryHandleManualCancel(entity)) {
            fallback.run();
        }
    }

    public static boolean handleShadowTechnique(LevelAccessor world, double x, double y, double z, Entity entity) {
        return MegumiShadowService.tryHandleTechnique(world, x, y, z, entity);
    }
}
