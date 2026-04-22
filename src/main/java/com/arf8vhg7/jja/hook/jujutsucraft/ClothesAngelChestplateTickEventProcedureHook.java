package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.player.mobility.fly.FlyChestplateCharacterGate;
import com.arf8vhg7.jja.feature.player.mobility.fly.FlyEffectGrantRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class ClothesAngelChestplateTickEventProcedureHook {
    private ClothesAngelChestplateTickEventProcedureHook() {
    }

    public static boolean shouldCancel(Entity entity) {
        boolean chestplateBlocked = FlyChestplateCharacterGate.shouldBlockTick(entity);
        boolean hitenHeld = entity instanceof LivingEntity livingEntity && FlyEffectGrantRules.hasHitenInHands(livingEntity);
        return FlyEffectGrantRules.shouldCancelCharacterGate(chestplateBlocked, hitenHeld);
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        if (equipmentSlot == EquipmentSlot.CHEST) {
            boolean chestplateBlocked = FlyChestplateCharacterGate.shouldBlockTick(livingEntity);
            boolean hitenHeld = FlyEffectGrantRules.hasHitenInHands(livingEntity);
            if (FlyEffectGrantRules.shouldSuppressChestplateRead(chestplateBlocked, hitenHeld)) {
                return ItemStack.EMPTY;
            }
        }
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }

    public static void applyHitenFlyEffect(Entity entity) {
        FlyEffectGrantRules.applyHitenGroundedFlyEffect(entity);
    }
}
