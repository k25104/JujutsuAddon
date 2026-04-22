package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AttackStrongMahitoProcedureHook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AttackStrongMahitoProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AttackStrongMahitoProcedure.class, remap = false)
public abstract class AttackStrongMahitoProcedureMixin {
    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt6') > 5.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$treatScaledFullChargeAsOverflow(boolean original, @Local(argsOnly = true) Entity entity) {
        return AttackStrongMahitoProcedureHook.isFullChargeOverflow(entity, original);
    }

    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt6') >= 5.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$recognizeScaledFullCharge(boolean original, @Local(argsOnly = true) Entity entity) {
        return AttackStrongMahitoProcedureHook.isFullChargeReached(entity, original);
    }
}
