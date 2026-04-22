package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ReverseCursedTechniqueOnEffectActiveTickProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.ReverseCursedTechniqueOnEffectActiveTickProcedure;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ReverseCursedTechniqueOnEffectActiveTickProcedure.class, remap = false)
public abstract class ReverseCursedTechniqueOnEffectActiveTickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$executeCustomPlayerTick(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (ReverseCursedTechniqueOnEffectActiveTickProcedureHook.executePlayerTick(world, x, y, z, entity)) {
            ci.cancel();
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 6
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$skipStunReduction(LivingEntity livingEntity, MobEffect effect, Operation<Boolean> original) {
        return ReverseCursedTechniqueOnEffectActiveTickProcedureHook.skipStunReduction(livingEntity, effect, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$useJjcHealForRctDamage(Entity entity, DamageSource damageSource, float amount, Operation<Boolean> original) {
        return ReverseCursedTechniqueOnEffectActiveTickProcedureHook.applyCursedSpiritDamage(entity, damageSource, amount, original);
    }
}
