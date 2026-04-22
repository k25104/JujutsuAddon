package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.PehkuiTargetedVelocityProceduresHook;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AttackOverheadHigurumaProcedure;
import net.mcreator.jujutsucraft.procedures.AttackOverheadSuperProcedure;
import net.mcreator.jujutsucraft.procedures.AttackTackleFlyingProcedure;
import net.mcreator.jujutsucraft.procedures.AttackTackleProcedure;
import net.mcreator.jujutsucraft.procedures.HighSpeedOnEffectActiveTickProcedure;
import net.mcreator.jujutsucraft.procedures.SkillSukunaCombo2Procedure;
import net.mcreator.jujutsucraft.procedures.SkillSukunaCombo3Procedure;
import net.mcreator.jujutsucraft.procedures.SpeedIsPowerProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(
    value = {
        AttackOverheadHigurumaProcedure.class,
        AttackOverheadSuperProcedure.class,
        AttackTackleFlyingProcedure.class,
        AttackTackleProcedure.class,
        HighSpeedOnEffectActiveTickProcedure.class,
        SkillSukunaCombo2Procedure.class,
        SkillSukunaCombo3Procedure.class,
        SpeedIsPowerProcedure.class
    },
    remap = false
)
public abstract class PehkuiTargetedVelocityProceduresMixin {
    @ModifyArg(
        method = "execute",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;m_20256_(Lnet/minecraft/world/phys/Vec3;)V"),
        index = 0,
        require = 1
    )
    private static Vec3 jja$fixPehkuiTargetedVelocity(Vec3 original, @Local(argsOnly = true) Entity entity) {
        return PehkuiTargetedVelocityProceduresHook.adjustTargetedVelocity(entity, original);
    }
}
