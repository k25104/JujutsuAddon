package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AttackOverheadProcedureHook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AttackOverheadProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = AttackOverheadProcedure.class, remap = false)
public abstract class AttackOverheadProcedureMixin {
    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt6') + 0.125")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static double jja$clampScaledFullChargeStep(double original, @Local(argsOnly = true) Entity entity) {
        return AttackOverheadProcedureHook.clampChargeStep(entity, original);
    }

    @ModifyArg(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;m_20256_(Lnet/minecraft/world/phys/Vec3;)V"),
        index = 0,
        require = 1
    )
    private static Vec3 jja$fixPehkuiTargetedVelocity(Vec3 original, @Local(argsOnly = true) Entity entity) {
        return AttackOverheadProcedureHook.adjustTargetedVelocity(entity, original);
    }
}
