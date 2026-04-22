package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.MahoragaCutTheWorldProcedureHook;
import net.mcreator.jujutsucraft.procedures.MahoragaCutTheWorldProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MahoragaCutTheWorldProcedure.class, remap = false)
public abstract class MahoragaCutTheWorldProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$observeCutTheWorld(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        MahoragaCutTheWorldProcedureHook.observeCutTheWorld(entity);
    }
}
