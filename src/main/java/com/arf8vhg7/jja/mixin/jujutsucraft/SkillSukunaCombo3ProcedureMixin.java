package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SkillSukunaCombo3ProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.SkillSukunaCombo3Procedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SkillSukunaCombo3Procedure.class, remap = false)
public abstract class SkillSukunaCombo3ProcedureMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/LogicStartPassiveProcedure;execute(Lnet/minecraft/world/entity/Entity;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$disableProjectileSlashWhenHeldItem(
        boolean original,
        @Local(argsOnly = true) Entity entity,
        @Local(ordinal = 1) ItemStack item_a
    ) {
        return SkillSukunaCombo3ProcedureHook.allowProjectileSlash(entity, item_a, original);
    }
}
