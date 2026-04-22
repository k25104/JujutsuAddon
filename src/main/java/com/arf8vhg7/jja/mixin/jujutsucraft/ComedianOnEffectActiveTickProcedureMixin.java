package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ComedianOnEffectActiveTickProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.ComedianOnEffectActiveTickProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ComedianOnEffectActiveTickProcedure.class, remap = false)
public abstract class ComedianOnEffectActiveTickProcedureMixin {
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
    private static float jja$useFirstAidAwareHealth(float currentHealth, @Local(argsOnly = true) Entity entity) {
        return ComedianOnEffectActiveTickProcedureHook.getEffectiveHealth(entity, currentHealth);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21153_(F)V"
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$useFirstAidAwareSelfHeal(LivingEntity livingEntity, float health, Operation<Void> original) {
        ComedianOnEffectActiveTickProcedureHook.applySelfHeal(livingEntity, health, original);
    }
}
