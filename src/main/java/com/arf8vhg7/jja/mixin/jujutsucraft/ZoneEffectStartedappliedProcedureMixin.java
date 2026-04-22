package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ZoneEffectStartedappliedProcedureHook;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.ZoneEffectStartedappliedProcedure;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(value = ZoneEffectStartedappliedProcedure.class, remap = false)
public abstract class ZoneEffectStartedappliedProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/util/LazyOptional;ifPresent(Lnet/minecraftforge/common/util/NonNullConsumer;)V"
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$skipCursePowerRecovery(
        LazyOptional<JujutsucraftModVariables.PlayerVariables> optional,
        NonNullConsumer<JujutsucraftModVariables.PlayerVariables> consumer,
        Operation<Void> original
    ) {
        ZoneEffectStartedappliedProcedureHook.applyCursePowerRecovery(optional, consumer);
    }
}
