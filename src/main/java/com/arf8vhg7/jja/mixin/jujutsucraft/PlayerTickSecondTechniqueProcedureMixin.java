package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.PlayerTickSecondTechniqueProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.PlayerTickSecondTechniqueProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerTickSecondTechniqueProcedure.class, remap = false)
public abstract class PlayerTickSecondTechniqueProcedureMixin {
    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21205_()Lnet/minecraft/world/item/ItemStack;"
        ),
        remap = false,
        require = 1
    )
    private static ItemStack jja$allowOffhandMegaphoneTemporaryTechnique(
        ItemStack original,
        @Local(argsOnly = true) Entity entity
    ) {
        return PlayerTickSecondTechniqueProcedureHook.resolveHeldMegaphoneAwareStack(entity, original);
    }
}
