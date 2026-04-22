package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SkillProjectionSorceryProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.SkillProjectionSorceryProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SkillProjectionSorceryProcedure.class, remap = false)
public abstract class SkillProjectionSorceryProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$handleFastFreezeFollowStop(
        LivingEntity livingEntity,
        MobEffectInstance effectInstance,
        Operation<Boolean> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true) Entity entity
    ) {
        return SkillProjectionSorceryProcedureHook.applyProjectionSorceryEffect(world, entity, livingEntity, effectInstance, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128471_(Ljava/lang/String;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$handleProjectionFollowStartGate(
        CompoundTag persistentData,
        String key,
        Operation<Boolean> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return SkillProjectionSorceryProcedureHook.jjaShouldKeepProjectionFollowLocked(entity, persistentData, key, original);
    }
}
