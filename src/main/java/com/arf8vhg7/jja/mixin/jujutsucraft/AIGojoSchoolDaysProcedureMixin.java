package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AIGojoSchoolDaysProcedureHook;
import net.mcreator.jujutsucraft.procedures.AIGojoSchoolDaysProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(value = AIGojoSchoolDaysProcedure.class, remap = false)
public abstract class AIGojoSchoolDaysProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21124_(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"
        ),
        remap = false
    ,
        require = 1
    )
    private static MobEffectInstance jja$replaceDomainDuration(LivingEntity livingEntity, MobEffect effect, Operation<MobEffectInstance> original) {
        return AIGojoSchoolDaysProcedureHook.getEffect(livingEntity, effect);
    }
}
