package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.FirstAidEffectiveHealthProceduresHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.CursedTechniqueOgiProcedure;
import net.mcreator.jujutsucraft.procedures.HakariKinjiEntityIsHurtProcedure;
import net.mcreator.jujutsucraft.procedures.SkillRantaEyeProcedure;
import net.mcreator.jujutsucraft.procedures.TodoAoiEntityIsHurtProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
    value = {
        CursedTechniqueOgiProcedure.class,
        HakariKinjiEntityIsHurtProcedure.class,
        SkillRantaEyeProcedure.class,
        TodoAoiEntityIsHurtProcedure.class
    },
    remap = false
)
public abstract class FirstAidEffectiveHealthProceduresMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F"
        ),
        remap = false,
        require = 1
    )
    private static float jja$useFirstAidAwareHealth(float original, @Local(argsOnly = true) Entity entity) {
        return FirstAidEffectiveHealthProceduresHook.getEffectiveHealth(entity, original);
    }
}
