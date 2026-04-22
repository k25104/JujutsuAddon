package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.LogicStartProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.LogicStartProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LogicStartProcedure.class, remap = false)
public abstract class LogicStartProcedureMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;m_19564_()I",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static int jja$resolveTechniqueStartSimpleDomainDuration(int originalDuration, @Local(argsOnly = true) Entity entity) {
        return com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniqueStartGate.resolveSimpleDomainDurationForPlayer(entity, originalDuration);
    }

    @Inject(method = "execute", at = @At("RETURN"), cancellable = true, remap = false, require = 1)
    private static void jja$applyPlayerTechniqueStartRules(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(LogicStartProcedureHook.applyPlayerTechniqueStartRules(entity, cir.getReturnValue()));
    }
}
