package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class CursedTechniquePotionExpiresProcedureHook {
    private CursedTechniquePotionExpiresProcedureHook() {
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }

    public static JujutsucraftModVariables.PlayerVariables resolvePlayerVariablesOrDefault(
        JujutsucraftModVariables.PlayerVariables variables
    ) {
        return JjaJujutsucraftCompat.jjaResolvePlayerVariablesOrDefault(variables);
    }
}
