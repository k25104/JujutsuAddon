package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AIActive2ProcedureHook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AIActive2Procedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AIActive2Procedure.class, remap = false)
public abstract class AIActive2ProcedureMixin {
    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt_reverse_lim') + 1.0")
    @ModifyExpressionValue(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0), require = 1)
    private static double jja$bypassRikaRctLimitCheck(double original, @Local(argsOnly = true) Entity entity) {
        return AIActive2ProcedureHook.bypassLimitIncrement(entity, original);
    }

    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt_reverse_lim') + 1.0")
    @ModifyExpressionValue(method = "execute", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1), require = 1)
    private static double jja$bypassRikaRctLimitConsumption(double original, @Local(argsOnly = true) Entity entity) {
        return AIActive2ProcedureHook.bypassLimitIncrement(entity, original);
    }
}
