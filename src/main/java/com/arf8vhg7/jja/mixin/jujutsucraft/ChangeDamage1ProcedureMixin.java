package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ChangeDamage1ProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.ChangeDamage1Procedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChangeDamage1Procedure.class, remap = false)
public abstract class ChangeDamage1ProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/ReturnInsideItemProcedure;execute(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/item/ItemStack;"
        ),
        remap = false,
        require = 1
    )
    private static ItemStack jja$gateIdleTransfigurationSukunaFingerCheck(
        Entity target,
        Operation<ItemStack> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        return ChangeDamage1ProcedureHook.resolveSukunaFingerCheckItem(entity, target, original.call(target));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$gateIdleTransfigurationSukunaEffectCheck(
        LivingEntity target,
        MobEffect effect,
        Operation<Boolean> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        return ChangeDamage1ProcedureHook.resolveSukunaEffectCheck(original.call(target, effect), entity, target);
    }
}
