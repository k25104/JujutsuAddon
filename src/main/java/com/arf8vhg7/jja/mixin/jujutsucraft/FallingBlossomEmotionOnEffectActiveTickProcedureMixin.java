package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.FallingBlossomEmotionOnEffectActiveTickProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.FallingBlossomEmotionOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FallingBlossomEmotionOnEffectActiveTickProcedure.class, remap = false)
public abstract class FallingBlossomEmotionOnEffectActiveTickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$countFbeTick(LevelAccessor world, Entity entity, CallbackInfo ci) {
        FallingBlossomEmotionOnEffectActiveTickProcedureHook.onActiveTick(entity);
    }

    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$enterCeParticleContext(LevelAccessor world, Entity entity, CallbackInfo ci) {
        FallingBlossomEmotionOnEffectActiveTickProcedureHook.enterCeParticleContext(entity);
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 1)
    private static void jja$exitCeParticleContext(LevelAccessor world, Entity entity, CallbackInfo ci) {
        FallingBlossomEmotionOnEffectActiveTickProcedureHook.exitCeParticleContext();
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 3
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$skipFbeInfinityRemoval(
        net.minecraft.world.entity.LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        if (FallingBlossomEmotionOnEffectActiveTickProcedureHook.shouldSkipInfinityRemoval()) {
            return false;
        }

        return original.call(livingEntity, effect);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/util/LazyOptional;ifPresent(Lnet/minecraftforge/common/util/NonNullConsumer;)V",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static void jja$skipUpstreamFbeBurstDrain(
        LazyOptional<JujutsucraftModVariables.PlayerVariables> optional,
        NonNullConsumer<JujutsucraftModVariables.PlayerVariables> consumer,
        Operation<Void> original,
        LevelAccessor world,
        Entity entity
    ) {
        if (!FallingBlossomEmotionOnEffectActiveTickProcedureHook.shouldSkipUpstreamBurstDrain(entity)) {
            original.call(optional, consumer);
        }
    }
}
