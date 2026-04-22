package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.NyoiStaffItemHook;
import net.mcreator.jujutsucraft.item.NyoiStaffItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NyoiStaffItem.class, remap = false)
public abstract class NyoiStaffItemMixin {
    @Inject(method = "m_6225_", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void jja$restrictPlacementToKashimo(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!NyoiStaffItemHook.shouldPlace(context)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
