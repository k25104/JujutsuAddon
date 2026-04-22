package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SpeedIsPowerProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.mcreator.jujutsucraft.procedures.SpeedIsPowerProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = SpeedIsPowerProcedure.class, remap = false)
public abstract class SpeedIsPowerProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128347_(Ljava/lang/String;D)V",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static void jja$skipTopSpeedChargeWhenAlreadyFast(
        CompoundTag persistentData,
        String key,
        double value,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        original.call(persistentData, key, SpeedIsPowerProcedureHook.resolveTopSpeedPunchChargeCounter(entity, key, value));
    }

    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;m_6021_(DDD)V"),
        remap = false,
        require = 1
    )
    private static void jja$skipClientSideTeleport(
        Entity entity,
        double x,
        double y,
        double z,
        Operation<Void> original,
        @Local(argsOnly = true) LevelAccessor world
    ) {
        if (SpeedIsPowerProcedureHook.shouldApplyServerMovement(world)) {
            original.call(entity, x, y, z);
        }
    }

    @ModifyArg(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;m_20256_(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 1),
        index = 0,
        remap = false,
        require = 1
    )
    private static Vec3 jja$skipClientSideVelocity(Vec3 original, @Local(argsOnly = true) LevelAccessor world) {
        return SpeedIsPowerProcedureHook.resolveServerMovementVelocity(world, original);
    }
}
