package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.TechniqueOgi2ProcedureHook;
import net.mcreator.jujutsucraft.procedures.TechniqueOgi2Procedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TechniqueOgi2Procedure.class, remap = false)
public abstract class TechniqueOgi2ProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$enterCeParticleContext(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        TechniqueOgi2ProcedureHook.enterCeParticleContext(entity);
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 1)
    private static void jja$exitCeParticleContext(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        TechniqueOgi2ProcedureHook.exitCeParticleContext();
    }
}
