package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.GrabProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.mcreator.jujutsucraft.procedures.GrabProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GrabProcedure.class, remap = false)
public abstract class GrabProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_6021_(DDD)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$adjustGrabDestination(
        Entity grabbedEntity,
        double x,
        double y,
        double z,
        Operation<Void> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true) Entity caster
    ) {
        Vec3 safeDestination = GrabProcedureHook.resolveSafeGrabDestination(world, caster, grabbedEntity);
        original.call(grabbedEntity, safeDestination.x, safeDestination.y, safeDestination.z);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;m_9774_(DDDFF)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$adjustGrabTeleport(
        ServerGamePacketListenerImpl connection,
        double x,
        double y,
        double z,
        float yRot,
        float xRot,
        Operation<Void> original,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true) Entity caster,
        @Local ServerPlayer serverPlayer
    ) {
        Vec3 safeDestination = GrabProcedureHook.resolveSafeGrabDestination(world, caster, serverPlayer);
        original.call(connection, safeDestination.x, safeDestination.y, safeDestination.z, yRot, xRot);
    }
}
