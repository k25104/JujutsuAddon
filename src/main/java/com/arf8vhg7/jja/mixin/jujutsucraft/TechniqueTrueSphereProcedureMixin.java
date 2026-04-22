package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.TechniqueTrueSphereProcedureHook;
import com.arf8vhg7.jja.util.JjaCommandHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.TechniqueTrueSphereProcedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TechniqueTrueSphereProcedure.class, remap = false)
public abstract class TechniqueTrueSphereProcedureMixin {
    @WrapOperation(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;m_7967_(Lnet/minecraft/world/entity/Entity;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$makeTrueSphereInvulnerableBeforeSpawn(
        ServerLevel serverLevel,
        Entity summoned,
        Operation<Boolean> original
    ) {
        JjaCommandHelper.executeAsEntity(summoned, TechniqueTrueSphereProcedureHook.getTrueSphereInvulnerableCommand());
        return original.call(serverLevel, summoned);
    }
}