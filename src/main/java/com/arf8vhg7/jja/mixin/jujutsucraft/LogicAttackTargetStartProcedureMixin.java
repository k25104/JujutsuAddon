package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.LogicAttackTargetStartProcedureHook;
import net.mcreator.jujutsucraft.procedures.LogicAttackTargetStartProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LogicAttackTargetStartProcedure.class, remap = false)
public abstract class LogicAttackTargetStartProcedureMixin {
    @Inject(method = "execute", at = @At("RETURN"), cancellable = true, remap = false, require = 1)
    private static void jja$restrictUnregisteredPlayerTargets(
        LevelAccessor world,
        Entity entity,
        CallbackInfoReturnable<Boolean> cir
    ) {
        cir.setReturnValue(LogicAttackTargetStartProcedureHook.allowTargetStart(cir.getReturnValue(), world, entity));
    }
}
