package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.PlayerSetProfessionProcedureHook;
import net.mcreator.jujutsucraft.procedures.PlayerSetProfessionProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerSetProfessionProcedure.class, remap = false)
public abstract class PlayerSetProfessionProcedureMixin {
    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 1)
    private static void jja$applySelectableRandomProfession(Entity entity, CallbackInfo ci) {
        PlayerSetProfessionProcedureHook.applySelectableRandomProfession(entity);
    }
}
