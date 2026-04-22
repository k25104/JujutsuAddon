package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.TechniqueInsectArmorProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.TechniqueInsectArmorProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TechniqueInsectArmorProcedure.class, remap = false)
public abstract class TechniqueInsectArmorProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;m_230957_(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"
        ),
        remap = false,
        require = 1
    )
    private static int jja$applyHiddenInsectArmorEffect(
        Commands commands,
        CommandSourceStack commandSourceStack,
        String command,
        Operation<Integer> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return TechniqueInsectArmorProcedureHook.runEffectCommand(
            command,
            entity,
            () -> original.call(commands, commandSourceStack, command)
        );
    }
}
