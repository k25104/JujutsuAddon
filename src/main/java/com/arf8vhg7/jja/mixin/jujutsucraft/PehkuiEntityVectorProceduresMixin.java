package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.PehkuiEntityVectorProceduresHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AvoidanceProcedure;
import net.mcreator.jujutsucraft.procedures.LariatProcedure;
import net.mcreator.jujutsucraft.procedures.SkillSukunaCombo1Procedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = {AvoidanceProcedure.class, LariatProcedure.class, SkillSukunaCombo1Procedure.class}, remap = false)
public abstract class PehkuiEntityVectorProceduresMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/EntityVectorProcedure;execute(Lnet/minecraft/world/entity/Entity;DDD)V"
        ),
        require = 1
    )
    private static void jja$fixPehkuiEntityVector(Entity entity, double x, double y, double z, Operation<Void> original) {
        Vec3 adjusted = PehkuiEntityVectorProceduresHook.adjustTargetedVelocity(entity, new Vec3(x, y, z));
        original.call(entity, adjusted.x, adjusted.y, adjusted.z);
    }

    @ModifyArg(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;m_20256_(Lnet/minecraft/world/phys/Vec3;)V"),
        index = 0,
        require = 1
    )
    private static Vec3 jja$fixPehkuiTargetedVelocity(Vec3 original, @Local(argsOnly = true) Entity entity) {
        return PehkuiEntityVectorProceduresHook.adjustTargetedVelocity(entity, original);
    }
}
