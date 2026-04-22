package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SkillDhruv1ProcedureHook;
import net.mcreator.jujutsucraft.procedures.SkillDhruv1Procedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkillDhruv1Procedure.class, remap = false)
public abstract class SkillDhruv1ProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$handleCustomSummon(LevelAccessor world, Entity entity, CallbackInfo ci) {
        if (SkillDhruv1ProcedureHook.handleCustomSummon(world, entity)) {
            ci.cancel();
        }
    }
}
