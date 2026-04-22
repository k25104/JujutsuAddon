package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.WhenBeforeEntityisHurtNoSourceProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.WhenBeforeEntityisHurtNoSourceProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WhenBeforeEntityisHurtNoSourceProcedure.class, remap = false)
public abstract class WhenBeforeEntityisHurtNoSourceProcedureMixin {
    @WrapOperation(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;random()D", ordinal = 0),
        remap = false,
        require = 1
    )
    private static double jja$scaleReverseCursedTechniqueGrantChance(Operation<Double> original, @Local(argsOnly = true) Entity entity) {
        return WhenBeforeEntityisHurtNoSourceProcedureHook.scaleReverseCursedTechniqueGrantRandom(original.call(), entity);
    }

    @WrapOperation(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;random()D", ordinal = 1),
        remap = false,
        require = 1
    )
    private static double jja$suppressLegacyDomainAmplificationGrant(Operation<Double> original) {
        return WhenBeforeEntityisHurtNoSourceProcedureHook.suppressLegacyDomainAmplificationGrantRandom(original.call());
    }
}
