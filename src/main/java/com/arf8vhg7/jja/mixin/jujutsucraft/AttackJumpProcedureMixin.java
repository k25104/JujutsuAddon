package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AttackJumpProcedureHook;
import net.mcreator.jujutsucraft.procedures.AttackJumpProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = AttackJumpProcedure.class, remap = false)
public abstract class AttackJumpProcedureMixin {
    @ModifyConstant(method = "execute", constant = @Constant(doubleValue = 6.0D), require = 1)
    private static double jja$scaleTargetingStep(double original, LevelAccessor world, double x, double y, double z, Entity entity) {
        return AttackJumpProcedureHook.scaleTargetingStep(entity, original);
    }
}
