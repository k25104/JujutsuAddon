package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.KeyChangeTechniqueOnKeyPressed2ProcedureHook;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressed2Procedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyChangeTechniqueOnKeyPressed2Procedure.class, remap = false)
public abstract class KeyChangeTechniqueOnKeyPressed2ProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;DD)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1
    )
    private static void jja$handleCustomSelection(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double playerSelect,
        CallbackInfo ci
    ) {
        if (KeyChangeTechniqueOnKeyPressed2ProcedureHook.handleCustomSelection(world, x, y, z, entity, playerCt, playerSelect)) {
            ci.cancel();
        }
    }
}
