package com.arf8vhg7.jja.mixin.minecraft;

import com.arf8vhg7.jja.hook.minecraft.CommandsHook;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Commands.class)
public abstract class CommandsMixin {
    @ModifyVariable(
        method = "performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I",
        at = @At("HEAD"),
        argsOnly = true,
        index = 2,
        require = 1
    )
    private String jja$remapCeParticleCommand(String command) {
        return CommandsHook.remapCeParticleCommand(command);
    }
}
