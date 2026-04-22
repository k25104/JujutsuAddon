package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.KeyStartTechniqueOnKeyReleasedProcedureHook;
import net.mcreator.jujutsucraft.procedures.KeyStartTechniqueOnKeyReleasedProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyStartTechniqueOnKeyReleasedProcedure.class, remap = false)
public abstract class KeyStartTechniqueOnKeyReleasedProcedureMixin {
    @Inject(method = "execute(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$clearPressedSlot(Entity entity, CallbackInfo ci) {
        KeyStartTechniqueOnKeyReleasedProcedureHook.clearPressedSlot(entity);
    }
}
