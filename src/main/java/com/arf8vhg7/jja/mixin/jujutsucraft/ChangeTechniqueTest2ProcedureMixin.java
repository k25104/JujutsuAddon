package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ChangeTechniqueTest2ProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.ChangeTechniqueTest2Procedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChangeTechniqueTest2Procedure.class, remap = false)
public abstract class ChangeTechniqueTest2ProcedureMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21205_()Lnet/minecraft/world/item/ItemStack;"
        ),
        remap = false,
        require = 1
    )
    private static ItemStack jja$keepCopiedCursedSpeechTechniqueFromOffhandMegaphone(
        ItemStack original,
        @Local(argsOnly = true) Entity entity
    ) {
        return ChangeTechniqueTest2ProcedureHook.resolveHeldMegaphoneAwareStack(entity, original);
    }
}
