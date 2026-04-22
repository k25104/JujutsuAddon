package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SimpleDomainEffectStartedappliedProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.mcreator.jujutsucraft.procedures.SimpleDomainEffectStartedappliedProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SimpleDomainEffectStartedappliedProcedure.class, remap = false)
@SuppressWarnings("deprecation")
public abstract class SimpleDomainEffectStartedappliedProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;m_230957_(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"
        ),
        remap = false,
        require = 1
    )
    private static int jja$emitRingParticlesOnlyWhenAllowed(
        Commands commands,
        CommandSourceStack commandSourceStack,
        String command,
        Operation<Integer> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return SimpleDomainEffectStartedappliedProcedureHook.shouldEmitRingParticles(entity)
            ? original.call(commands, commandSourceStack, command)
            : 0;
    }

    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(DD)D"),
        remap = false,
        require = 1
    )
    private static double jja$capSimpleDomainRadiusAtSixteen(double left, double right, Operation<Double> original) {
        return SimpleDomainEffectStartedappliedProcedureHook.resolveSimpleDomainRadius(left);
    }
}
