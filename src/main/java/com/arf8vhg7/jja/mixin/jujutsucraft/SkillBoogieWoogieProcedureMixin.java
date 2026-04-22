package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SkillBoogieWoogieProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.SkillBoogieWoogieProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkillBoogieWoogieProcedure.class, remap = false)
public abstract class SkillBoogieWoogieProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$enterCeParticleContext(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        SkillBoogieWoogieProcedureHook.enterCeParticleContext(entity);
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 1)
    private static void jja$exitCeParticleContext(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        SkillBoogieWoogieProcedureHook.exitCeParticleContext();
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
    private static void jja$syncBoogieWoogieFacing(
        ServerGamePacketListenerImpl connection,
        double x,
        double y,
        double z,
        float yRot,
        float xRot,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity,
        @Local ServerPlayer serverPlayer
    ) {
        original.call(
            connection,
            x,
            y,
            z,
            SkillBoogieWoogieProcedureHook.resolveSwapTeleportYaw(serverPlayer, entity, yRot),
            SkillBoogieWoogieProcedureHook.resolveSwapTeleportPitch(serverPlayer, entity, xRot)
        );
    }
}
