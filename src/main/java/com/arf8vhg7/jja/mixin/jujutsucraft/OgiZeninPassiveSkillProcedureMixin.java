package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.OgiZeninPassiveSkillProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.OgiZeninPassiveSkillProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = OgiZeninPassiveSkillProcedure.class, remap = false)
public abstract class OgiZeninPassiveSkillProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/util/LazyOptional;orElse(Ljava/lang/Object;)Ljava/lang/Object;"
        ),
        remap = false,
        require = 1
    )
    private static Object jja$defaultMissingPlayerVariables(
        LazyOptional<?> optional,
        Object fallback,
        Operation<Object> original
    ) {
        return OgiZeninPassiveSkillProcedureHook.resolvePlayerVariablesOrDefault(
            (JujutsucraftModVariables.PlayerVariables) original.call(optional, fallback)
        );
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F"
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareHealth(float original, @Local(argsOnly = true) Entity entity) {
        return OgiZeninPassiveSkillProcedureHook.getEffectiveHealth(entity, original);
    }
}
