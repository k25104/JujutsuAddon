package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ItemMasterPhysicalGiftedItemHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.item.ItemMasterPhysicalGiftedItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemMasterPhysicalGiftedItem.class, remap = false)
public abstract class ItemMasterPhysicalGiftedItemMixin {
    @WrapOperation(
        method = "m_7203_",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/MasterSkillsProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V"
        ),
        remap = false,
        require = 1
    )
    private void jja$prependMakiSelection(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        ItemStack itemStack,
        Operation<Void> original
    ) {
        ItemMasterPhysicalGiftedItemHook.handleMasterSkills(world, x, y, z, entity, itemStack, original);
    }
}
