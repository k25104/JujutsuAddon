package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.BulletDomainHit2ProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.BulletDomainHit2Procedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BulletDomainHit2Procedure.class, remap = false)
public abstract class BulletDomainHit2ProcedureMixin {
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
        Entity entity,
        @Local(name = "entity_a") Entity entityA
    ) {
        return BulletDomainHit2ProcedureHook.resolveCurrentRadius(entityA, radius);
    }
}
