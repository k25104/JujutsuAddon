package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DomainExpansionEffectExpiresProcedureHook;
import net.mcreator.jujutsucraft.procedures.DomainExpansionEffectExpiresProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(value = DomainExpansionEffectExpiresProcedure.class, remap = false)
public abstract class DomainExpansionEffectExpiresProcedureMixin {
    @WrapOperation(
        method = "lambda$execute$0",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            ordinal = 0
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$adjustUnstableDurationOnRelease(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        return DomainExpansionEffectExpiresProcedureHook.addEffect(livingEntity, effectInstance);
    }

    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$clearCounter(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo callbackInfo
    ) {
        DomainExpansionEffectExpiresProcedureHook.clearCounter(entity);
    }
}
