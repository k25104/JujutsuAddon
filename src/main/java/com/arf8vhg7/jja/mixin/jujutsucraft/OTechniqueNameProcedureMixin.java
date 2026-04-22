package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.OTechniqueNameProcedureHook;
import net.mcreator.jujutsucraft.procedures.OTechniqueNameProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = OTechniqueNameProcedure.class, remap = false)
public abstract class OTechniqueNameProcedureMixin {
    @Inject(method = "execute", at = @At("RETURN"), cancellable = true, remap = false, require = 1)
    private static void jja$translateTechniqueName(Entity entity, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(OTechniqueNameProcedureHook.resolveDisplayName(entity, cir.getReturnValue()));
    }
}
