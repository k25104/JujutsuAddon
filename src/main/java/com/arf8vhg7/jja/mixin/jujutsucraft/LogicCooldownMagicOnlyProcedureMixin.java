package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.LogicCooldownMagicOnlyProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.LogicCooldownMagicOnlyProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LogicCooldownMagicOnlyProcedure.class, remap = false)
public abstract class LogicCooldownMagicOnlyProcedureMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;m_19564_()I"
        ),
        remap = false,
        require = 1
    )
    private static int jja$resolveMagicCooldownSimpleDomainDuration(int originalDuration, @Local(argsOnly = true) Entity entity) {
        return LogicCooldownMagicOnlyProcedureHook.resolveSimpleDomainDuration(entity, originalDuration);
    }
}
