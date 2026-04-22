package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ReturnEnergyColorProcedureHook;
import net.mcreator.jujutsucraft.procedures.ReturnEnergyColorProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ReturnEnergyColorProcedure.class, remap = false)
public abstract class ReturnEnergyColorProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$applyCeColorOverride(Entity entity, CallbackInfoReturnable<Double> cir) {
        Integer overrideColor = ReturnEnergyColorProcedureHook.resolveOverrideColor(entity);
        if (overrideColor != null) {
            cir.setReturnValue(overrideColor.doubleValue());
        }
    }
}
