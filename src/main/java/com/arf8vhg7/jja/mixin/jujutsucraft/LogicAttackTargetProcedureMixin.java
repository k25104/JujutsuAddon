package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.LogicAttackTargetProcedureHook;
import net.mcreator.jujutsucraft.procedures.LogicAttackTargetProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LogicAttackTargetProcedure.class, remap = false)
public abstract class LogicAttackTargetProcedureMixin {
    @Unique
    private static final ThreadLocal<LevelAccessor> JJA_CONTEXT_WORLD = new ThreadLocal<>();

    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$captureWorldFromEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        JJA_CONTEXT_WORLD.set(entity == null ? null : entity.level());
    }

    @Inject(method = "execute", at = @At("RETURN"), cancellable = true, remap = false, require = 1)
    private static void jja$restrictUnregisteredPlayerTargets(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        try {
            cir.setReturnValue(LogicAttackTargetProcedureHook.allowCurrentTarget(cir.getReturnValue(), JJA_CONTEXT_WORLD.get(), entity));
        } finally {
            JJA_CONTEXT_WORLD.remove();
        }
    }
}
