package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.KeyReverseCursedTechniqueOnKeyReleasedProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.KeyReverseCursedTechniqueOnKeyReleasedProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KeyReverseCursedTechniqueOnKeyReleasedProcedure.class, remap = false)
public abstract class KeyReverseCursedTechniqueOnKeyReleasedProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z"
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$keepSpecialBranchRctOnRelease(LivingEntity livingEntity, MobEffect effect, Operation<Boolean> original) {
        if (KeyReverseCursedTechniqueOnKeyReleasedProcedureHook.shouldSuppressRctRemoval(livingEntity, effect)) {
            return false;
        }
        return original.call(livingEntity, effect);
    }
}
