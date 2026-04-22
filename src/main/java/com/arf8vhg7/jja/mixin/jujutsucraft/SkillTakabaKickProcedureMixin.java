package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SkillTakabaKickProcedureHook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.SkillTakabaKickProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = SkillTakabaKickProcedure.class, remap = false)
public abstract class SkillTakabaKickProcedureMixin {
    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt6') < 5.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$stopChargeAtScaledFullThreshold(boolean original, @Local(argsOnly = true) Entity entity) {
        return SkillTakabaKickProcedureHook.isStillCharging(entity, original);
    }

    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('cnt6') >= 5.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$recognizeScaledFullCharge(boolean original, @Local(argsOnly = true) Entity entity) {
        return SkillTakabaKickProcedureHook.isFullChargeReached(entity, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/EntityVectorProcedure;execute(Lnet/minecraft/world/entity/Entity;DDD)V"
        ),
        require = 1
    )
    private static void jja$fixPehkuiEntityVector(Entity entity, double x, double y, double z, Operation<Void> original) {
        Vec3 adjusted = SkillTakabaKickProcedureHook.adjustTargetedVelocity(entity, new Vec3(x, y, z));
        original.call(entity, adjusted.x, adjusted.y, adjusted.z);
    }

    @ModifyArg(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;m_20256_(Lnet/minecraft/world/phys/Vec3;)V"),
        index = 0,
        require = 1
    )
    private static Vec3 jja$fixPehkuiTargetedVelocity(Vec3 original, @Local(argsOnly = true) Entity entity) {
        return SkillTakabaKickProcedureHook.adjustTargetedVelocity(entity, original);
    }
}
