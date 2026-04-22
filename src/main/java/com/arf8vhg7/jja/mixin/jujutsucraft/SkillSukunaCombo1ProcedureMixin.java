package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SkillSukunaCombo1ProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.SkillSukunaCombo1Procedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SkillSukunaCombo1Procedure.class, remap = false)
public abstract class SkillSukunaCombo1ProcedureMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/LogicStartPassiveProcedure;execute(Lnet/minecraft/world/entity/Entity;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$disableProjectileSlashWhenHeldItem(boolean original, @Local(argsOnly = true) Entity entity) {
        return SkillSukunaCombo1ProcedureHook.allowProjectileSlash(entity, original);
    }
}
