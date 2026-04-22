package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.CalculateAttackProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.CalculateAttackProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CalculateAttackProcedure.class, remap = false)
public abstract class CalculateAttackProcedureMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;max(DD)D",
            ordinal = 1
        ),
        remap = false
    ,
        require = 1
    )
    private static double jja$scaleCombatCooldownAttackSpeedPenalty(double original, @Local(argsOnly = true) Entity entity) {
        return CalculateAttackProcedureHook.scaleCombatCooldownAttackSpeedPenalty(entity, original);
    }
}
