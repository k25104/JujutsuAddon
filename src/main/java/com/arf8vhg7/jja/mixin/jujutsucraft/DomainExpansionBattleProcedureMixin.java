package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DomainExpansionBattleProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.DomainExpansionBattleProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = DomainExpansionBattleProcedure.class, remap = false)
public abstract class DomainExpansionBattleProcedureMixin {
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
    private static double jja$resolveCurrentRadius(
        double radius,
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity
    ) {
        return DomainExpansionBattleProcedureHook.resolveCurrentRadius(entity, radius);
    }

    @ModifyVariable(
        method = "execute",
        at = @At(value = "STORE", ordinal = 1),
        index = 46,
        remap = false
    ,
        require = 1
    )
    private static double jja$scaleBarrierSpeedWithStrength(double original, @Local(argsOnly = true) Entity entity) {
        return DomainExpansionBattleProcedureHook.scaleBarrierSpeedWithStrength(entity, original);
    }
}
