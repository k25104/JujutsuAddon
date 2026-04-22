package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SetRangedAmmoProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.SetRangedAmmoProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SetRangedAmmoProcedure.class, remap = false)
public abstract class SetRangedAmmoProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$stripZoneStrengthBonusFromRangedAmmo(
        LivingEntity livingEntity,
        MobEffectInstance effectInstance,
        Operation<Boolean> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        return original.call(livingEntity, SetRangedAmmoProcedureHook.adjustEffectInstance(entity, effectInstance));
    }

    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$propagateManualTechniqueAttackFlag(
        Entity entity,
        Entity entityiterator,
        CallbackInfo ci
    ) {
        SetRangedAmmoProcedureHook.propagateManualTechniqueAttack(entity, entityiterator);
    }
}
