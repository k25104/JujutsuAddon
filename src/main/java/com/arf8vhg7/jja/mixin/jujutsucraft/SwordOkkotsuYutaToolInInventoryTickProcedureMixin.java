package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SwordOkkotsuYutaToolInInventoryTickProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.SwordOkkotsuYutaToolInInventoryTickProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SwordOkkotsuYutaToolInInventoryTickProcedure.class, remap = false)
@SuppressWarnings("deprecation")
public abstract class SwordOkkotsuYutaToolInInventoryTickProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;m_41774_(I)V",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static void jja$consumeCopiedTechniqueOnlyWhenAllowed(ItemStack itemStack, int count, Operation<Void> original) {
        if (SwordOkkotsuYutaToolInInventoryTickProcedureHook.shouldShrinkCopiedTechnique(itemStack)) {
            original.call(itemStack, count);
        }
    }

    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$clearStaleCopiedTechniquePreserveMarker(
        LevelAccessor world,
        Entity entity,
        ItemStack itemstack,
        CallbackInfo ci
    ) {
        SwordOkkotsuYutaToolInInventoryTickProcedureHook.clearStaleCopiedTechniquePreserveMarker(itemstack);
    }
}
