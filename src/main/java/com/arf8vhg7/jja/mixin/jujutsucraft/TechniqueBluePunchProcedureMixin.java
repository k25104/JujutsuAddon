package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.TechniqueBluePunchProcedureHook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.TechniqueBluePunchProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TechniqueBluePunchProcedure.class, remap = false)
@SuppressWarnings("deprecation")
public abstract class TechniqueBluePunchProcedureMixin {
    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt5') >= 5.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), remap = false, require = 1)
    private static boolean jja$removeGetoHoldLimit(boolean original, @Local(argsOnly = true) Entity entity) {
        return TechniqueBluePunchProcedureHook.isHoldLimitReached(entity, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/LogicAttackProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$filterGetoTargets(
        LevelAccessor world,
        Entity entity,
        Entity entityiterator,
        Operation<Boolean> original
    ) {
        return TechniqueBluePunchProcedureHook.resolveAttackResult(world, entity, entityiterator, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$skipGetoSlowness(
        LivingEntity livingEntity,
        MobEffectInstance effectInstance,
        Operation<Boolean> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return TechniqueBluePunchProcedureHook.addEffect(entity, livingEntity, effectInstance, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;m_5594_(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$skipGetoFrameSetServerSound(
        Level level,
        Player player,
        BlockPos blockPos,
        SoundEvent soundEvent,
        SoundSource soundSource,
        float volume,
        float pitch,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (TechniqueBluePunchProcedureHook.shouldPlayFrameSetSound(entity)) {
            original.call(level, player, blockPos, soundEvent, soundSource, volume, pitch);
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;m_7785_(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$skipGetoFrameSetClientSound(
        Level level,
        double x,
        double y,
        double z,
        SoundEvent soundEvent,
        SoundSource soundSource,
        float volume,
        float pitch,
        boolean distanceDelay,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (TechniqueBluePunchProcedureHook.shouldPlayFrameSetSound(entity)) {
            original.call(level, x, y, z, soundEvent, soundSource, volume, pitch, distanceDelay);
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/EntityVectorProcedure;execute(Lnet/minecraft/world/entity/Entity;DDD)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$autoCaptureRecoverableTargets(
        Entity entityiterator,
        double x,
        double y,
        double z,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        TechniqueBluePunchProcedureHook.applyVector(entity, entityiterator, x, y, z, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/GrabProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$skipGetoGrab(LevelAccessor world, Entity entity, Operation<Void> original) {
        if (TechniqueBluePunchProcedureHook.shouldRunGrab(entity)) {
            original.call(world, entity);
        }
    }
}
