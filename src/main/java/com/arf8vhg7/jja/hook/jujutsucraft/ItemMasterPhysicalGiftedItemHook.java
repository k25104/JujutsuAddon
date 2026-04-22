package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.progression.physicalgifted.MasterPhysicalGiftedItemHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class ItemMasterPhysicalGiftedItemHook {
    private ItemMasterPhysicalGiftedItemHook() {
    }

    public static void handleMasterSkills(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack, Operation<Void> original) {
        MasterPhysicalGiftedItemHandler.handle(world, x, y, z, entity);
        original.call(world, x, y, z, entity, itemStack);
    }
}
