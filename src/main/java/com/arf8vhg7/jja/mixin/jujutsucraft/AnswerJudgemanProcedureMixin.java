package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AnswerJudgemanProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AnswerJudgemanProcedure;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = AnswerJudgemanProcedure.class, remap = false)
public abstract class AnswerJudgemanProcedureMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$MapVariables;DomainExpansionRadius:D",
            opcode = Opcodes.GETFIELD
        ),
        remap = false,
        require = 1
    )
    private static double jja$resolveCurrentRadius(double radius, @Local(argsOnly = true) Entity entity) {
        return AnswerJudgemanProcedureHook.resolveCurrentRadius(entity, radius);
    }

    @ModifyVariable(
        method = "execute",
        at = @At(value = "STORE", ordinal = 1),
        index = 5,
        remap = false,
        require = 1
    )
    private static double jja$useFeetAnchorDistanceForPlayerNotification(
        double originalDistance,
        @Local(name = "to_entity") Entity owner,
        @Local(name = "entityiterator") Entity target
    ) {
        return AnswerJudgemanProcedureHook.resolveFeetAnchorDistanceSquared(owner, target, originalDistance);
    }
}
