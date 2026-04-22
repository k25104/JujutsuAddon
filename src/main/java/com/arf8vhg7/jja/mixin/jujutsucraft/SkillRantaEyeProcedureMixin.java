package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SkillRantaEyeProcedureHook;
import com.arf8vhg7.jja.util.JjaCommandHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.SkillRantaEyeProcedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = SkillRantaEyeProcedure.class, remap = false)
public abstract class SkillRantaEyeProcedureMixin {
    @ModifyConstant(
        method = "execute",
        constant = @Constant(doubleValue = 200.0D),
        remap = false,
        require = 1
    )
    private static double jja$makeRantaEyeDurationInfinite(double original) {
        return SkillRantaEyeProcedureHook.getInfiniteActivationLimit();
    }

    @WrapOperation(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;m_7967_(Lnet/minecraft/world/entity/Entity;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$makeRantaEyeInvulnerableBeforeSpawn(
        ServerLevel serverLevel,
        Entity summoned,
        Operation<Boolean> original
    ) {
        JjaCommandHelper.executeAsEntity(summoned, SkillRantaEyeProcedureHook.getRantaEyeInvulnerableCommand());
        return original.call(serverLevel, summoned);
    }
}