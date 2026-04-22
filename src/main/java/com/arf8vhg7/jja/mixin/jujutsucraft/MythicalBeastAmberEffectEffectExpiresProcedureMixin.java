package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.MythicalBeastAmberEffectEffectExpiresProcedureHook;
import net.mcreator.jujutsucraft.procedures.MythicalBeastAmberEffectEffectExpiresProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MythicalBeastAmberEffectEffectExpiresProcedure.class, remap = false)
public abstract class MythicalBeastAmberEffectEffectExpiresProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;D)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/JujutsucraftMod;queueServerWork(ILjava/lang/Runnable;)V"
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$markForceDeathBypass(LevelAccessor world, Entity entity, double amplifier, CallbackInfo ci) {
        MythicalBeastAmberEffectEffectExpiresProcedureHook.markForceDeathBypass(entity);
        MythicalBeastAmberEffectEffectExpiresProcedureHook.scheduleManagedCleanup(entity);
    }
}
