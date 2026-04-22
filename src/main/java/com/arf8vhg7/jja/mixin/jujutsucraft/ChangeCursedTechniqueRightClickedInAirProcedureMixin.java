package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ChangeCursedTechniqueRightClickedInAirProcedureHook;
import net.mcreator.jujutsucraft.procedures.ChangeCursedTechniqueRightClickedInAirProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChangeCursedTechniqueRightClickedInAirProcedure.class, remap = false)
public abstract class ChangeCursedTechniqueRightClickedInAirProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$redirectForceRandomSelection(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (ChangeCursedTechniqueRightClickedInAirProcedureHook.handle(world, x, y, z, entity)) {
            ci.cancel();
        }
    }

    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$beginFirstAidReset(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        ChangeCursedTechniqueRightClickedInAirProcedureHook.beginFirstAidReset(world, entity);
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 1)
    private static void jja$finishFirstAidReset(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        ChangeCursedTechniqueRightClickedInAirProcedureHook.finishFirstAidReset(world, entity);
    }
}
