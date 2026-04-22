package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SummonNueProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.SummonNueProcedure;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SummonNueProcedure.class, remap = false)
public abstract class SummonNueProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;getString()Ljava/lang/String;"),
        remap = false
    ,
        require = 1
    )
    private static String jja$getKeyOrString(MutableComponent component, Operation<String> original) {
        return SummonNueProcedureHook.jjaGetKeyOrString(component);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerSelectCurseTechniqueName:Ljava/lang/String;",
            opcode = Opcodes.GETFIELD
        ),
        remap = false
    ,
        require = 1
    )
    private static String jja$resolveSelectTechniqueName(String original, @Local(argsOnly = true) Entity entity) {
        return SummonNueProcedureHook.resolveSelectTechniqueName(entity, original);
    }
}
