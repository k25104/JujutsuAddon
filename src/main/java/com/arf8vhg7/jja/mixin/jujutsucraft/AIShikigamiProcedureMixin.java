package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AIShikigamiProcedureHook;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AIShikigamiProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = AIShikigamiProcedure.class, remap = false)
public abstract class AIShikigamiProcedureMixin {
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 400), remap = false, require = 1)
    private static int jja$extendRozetsuShikigamiLifetime(int original, @Local(argsOnly = true) Entity entity) {
        return AIShikigamiProcedureHook.resolveRozetsuLifetimeLimit(entity, original);
    }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = 600), remap = false, require = 1)
    private static int jja$extendRozetsuShikigamiVesselLifetime(int original, @Local(argsOnly = true) Entity entity) {
        return AIShikigamiProcedureHook.resolveRozetsuLifetimeLimit(entity, original);
    }
}
