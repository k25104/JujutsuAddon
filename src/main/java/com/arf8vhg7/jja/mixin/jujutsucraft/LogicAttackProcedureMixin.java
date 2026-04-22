package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.LogicAttackProcedureHook;
import net.mcreator.jujutsucraft.procedures.LogicAttackProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LogicAttackProcedure.class, remap = false)
public abstract class LogicAttackProcedureMixin {
    @Inject(method = "execute", at = @At("RETURN"), cancellable = true, remap = false, require = 1)
    private static void jja$resolveAttackResult(
        LevelAccessor world,
        Entity entity,
        Entity entityiterator,
        CallbackInfoReturnable<Boolean> cir
    ) {
        cir.setReturnValue(LogicAttackProcedureHook.resolveAttackResult(cir.getReturnValue(), world, entity, entityiterator));
    }
}
