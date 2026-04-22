package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SkillJudgementProcedureHook;
import net.mcreator.jujutsucraft.procedures.SkillJudgementProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(value = SkillJudgementProcedure.class, remap = false)
public abstract class SkillJudgementProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$skipDomainExtension(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        return SkillJudgementProcedureHook.addEffect(livingEntity, effectInstance);
    }
}
