package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.KeyReverseCursedTechniqueOnKeyPressedProcedureHook;
import net.mcreator.jujutsucraft.procedures.KeyReverseCursedTechniqueOnKeyPressedProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyReverseCursedTechniqueOnKeyPressedProcedure.class, remap = false)
public abstract class KeyReverseCursedTechniqueOnKeyPressedProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$cancelManualStartAtFullHeal(Entity entity, CallbackInfo ci) {
        if (KeyReverseCursedTechniqueOnKeyPressedProcedureHook.shouldCancelStart(entity)) {
            ci.cancel();
        }
    }
}
