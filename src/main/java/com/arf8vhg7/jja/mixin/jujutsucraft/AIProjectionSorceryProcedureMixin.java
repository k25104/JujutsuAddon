package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AIProjectionSorceryProcedureHook;
import net.mcreator.jujutsucraft.procedures.AIProjectionSorceryProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AIProjectionSorceryProcedure.class, remap = false)
public abstract class AIProjectionSorceryProcedureMixin {
    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$accelerateProjectionFrames(LevelAccessor world, Entity entity, CallbackInfo ci) {
        AIProjectionSorceryProcedureHook.accelerateProjectionFrames(world, entity);
    }
}
