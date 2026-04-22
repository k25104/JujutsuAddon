package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.MalevolentShrineProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.MalevolentShrineProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(value = MalevolentShrineProcedure.class, remap = false)
public abstract class MalevolentShrineProcedureMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F"
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareHealthForShrineThreshold(float currentHealth, @Local(argsOnly = true) Entity entity) {
        return MalevolentShrineProcedureHook.getEffectiveHealth(entity, currentHealth);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128347_(Ljava/lang/String;D)V"
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$adjustYPosDoma(CompoundTag tag, String key, double value, Operation<Void> original) {
        original.call(tag, key, MalevolentShrineProcedureHook.jjaAdjustYPosDoma(key, value));
    }
}
