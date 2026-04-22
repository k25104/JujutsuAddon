package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.display.JjaTechniqueNameKeyResolver;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class KeyChangeTechniqueSelectionProceduresHook {
    private KeyChangeTechniqueSelectionProceduresHook() {
    }

    public static String jjaGetKeyOrString(Component component) {
        return JjaTechniqueNameKeyResolver.jjaGetKeyOrString(component);
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }
}
