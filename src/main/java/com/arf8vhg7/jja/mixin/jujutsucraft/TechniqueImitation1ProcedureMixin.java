package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.TechniqueImitation1ProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.TechniqueImitation1Procedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TechniqueImitation1Procedure.class, remap = false)
public abstract class TechniqueImitation1ProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;m_230957_(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static int jja$grantMegaphoneToOffhandWhenAvailable(
        Commands commands,
        CommandSourceStack sourceStack,
        String command,
        Operation<Integer> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (TechniqueImitation1ProcedureHook.shouldReplaceMegaphoneGrant(entity, command) && entity instanceof Player player) {
            TechniqueImitation1ProcedureHook.grantMegaphone(player);
            return 1;
        }
        return original.call(commands, sourceStack, command);
    }
}
