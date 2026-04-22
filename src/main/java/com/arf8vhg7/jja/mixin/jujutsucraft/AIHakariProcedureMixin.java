package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AIHakariProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.mcreator.jujutsucraft.procedures.AIHakariProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = AIHakariProcedure.class, remap = false)
public abstract class AIHakariProcedureMixin {
    @ModifyExpressionValue(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z"
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/mcreator/jujutsucraft/procedures/LogicConfilmDomainProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)Z"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/mcreator/jujutsucraft/procedures/CalculateAttackProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V"
            )
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$ignoreJackpotDomainBlock(boolean original) {
        return AIHakariProcedureHook.ignoreJackpotDomainBlock(original);
    }
}
