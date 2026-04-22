package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.InstantSpiritBodyofDistortedKillingEffectEffectExpiresProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.InstantSpiritBodyofDistortedKillingEffectEffectExpiresProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = InstantSpiritBodyofDistortedKillingEffectEffectExpiresProcedure.class, remap = false)
public abstract class InstantSpiritBodyofDistortedKillingEffectEffectExpiresProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;m_230957_(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"
        ),
        remap = false,
        require = 1
    )
    private static int jja$runArmorCommand(
        Commands commands,
        CommandSourceStack commandSourceStack,
        String command,
        Operation<Integer> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return InstantSpiritBodyofDistortedKillingEffectEffectExpiresProcedureHook.runArmorCommand(
            command,
            entity,
            () -> original.call(commands, commandSourceStack, command)
        );
    }
}
