package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SkillRozetsuShikigami3ProcedureHook;
import net.mcreator.jujutsucraft.procedures.SkillRozetsuShikigami3Procedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkillRozetsuShikigami3Procedure.class, remap = false)
public abstract class SkillRozetsuShikigami3ProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$handleCustomSummon(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (SkillRozetsuShikigami3ProcedureHook.handleCustomSummon(world, x, y, z, entity)) {
            ci.cancel();
        }
    }
}
