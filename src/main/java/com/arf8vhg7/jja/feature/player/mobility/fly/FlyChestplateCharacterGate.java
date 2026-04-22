package com.arf8vhg7.jja.feature.player.mobility.fly;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class FlyChestplateCharacterGate {
    private static final Map<ResourceLocation, Integer> REQUIRED_CT_BY_CHESTPLATE = Map.of(
        ResourceLocation.fromNamespaceAndPath("jujutsucraft", "clothes_dagon_chestplate"), 8,
        ResourceLocation.fromNamespaceAndPath("jujutsucraft", "clothes_angel_chestplate"), 28,
        ResourceLocation.fromNamespaceAndPath("jujutsucraft", "wing_king_chestplate"), 10,
        ResourceLocation.fromNamespaceAndPath("jujutsucraft", "insect_armor_chestplate"), 39
    );

    private FlyChestplateCharacterGate() {
    }

    public static boolean shouldBlockTick(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        ItemStack chestplate = CuriosEquipmentReadService.resolveEquipmentRead(
            livingEntity,
            EquipmentSlot.CHEST,
            livingEntity.getItemBySlot(EquipmentSlot.CHEST)
        );
        if (chestplate.isEmpty()) {
            return false;
        }
        ResourceLocation chestplateId = ForgeRegistries.ITEMS.getKey(chestplate.getItem());
        Integer requiredCt = REQUIRED_CT_BY_CHESTPLATE.get(chestplateId);
        if (requiredCt == null) {
            return false;
        }
        return JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(entity) != requiredCt;
    }
}
