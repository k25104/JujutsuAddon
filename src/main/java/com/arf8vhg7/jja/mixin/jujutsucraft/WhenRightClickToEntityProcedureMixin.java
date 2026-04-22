package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.WhenRightClickToEntityProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.WhenRightClickToEntityProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WhenRightClickToEntityProcedure.class, remap = false)
public abstract class WhenRightClickToEntityProcedureMixin {
    @Inject(method = "onRightClickEntity", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$blockReviveWaitingTargetEffects(EntityInteract event, CallbackInfo ci) {
        if (WhenRightClickToEntityProcedureHook.shouldCancel(event.getEntity(), event.getTarget())) {
            ci.cancel();
        }
    }

    @WrapOperation(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$handleRightClickProjectionSorceryEffect(
        LivingEntity target,
        MobEffectInstance effectInstance,
        Operation<Boolean> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true, ordinal = 0) Entity entity,
        @Local(argsOnly = true, ordinal = 1) Entity sourceentity
    ) {
        return WhenRightClickToEntityProcedureHook.applyProjectionSorceryEffect(world, sourceentity, target, effectInstance, original);
    }
}
