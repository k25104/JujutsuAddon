package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SpawnLevel3ProcedureHook;
import net.mcreator.jujutsucraft.procedures.SpawnLevel3Procedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SpawnLevel3Procedure.class, remap = false)
public abstract class SpawnLevel3ProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$overrideSpawnChance(LevelAccessor world, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(SpawnLevel3ProcedureHook.execute(world));
    }
}
