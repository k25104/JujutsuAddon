package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.WhenEntityTakesDamageNoSourceProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.WhenEntityTakesDamageNoSourceProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WhenEntityTakesDamageNoSourceProcedure.class, remap = false)
public abstract class WhenEntityTakesDamageNoSourceProcedureMixin {
    private static final String JJA_DAMAGE_NO_SOURCE_EXECUTE_METHOD =
        "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/entity/Entity;)V";

    @WrapOperation(
        method = JJA_DAMAGE_NO_SOURCE_EXECUTE_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$allowShadowImmersionInfinityProtection(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        return WhenEntityTakesDamageNoSourceProcedureHook.resolveInfinityProtection(original.call(livingEntity, effect), livingEntity);
    }
}
