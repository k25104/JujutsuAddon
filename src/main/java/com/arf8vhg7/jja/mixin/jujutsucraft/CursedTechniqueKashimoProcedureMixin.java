package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.CursedTechniqueKashimoProcedureHook;
import net.mcreator.jujutsucraft.procedures.CursedTechniqueKashimoProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CursedTechniqueKashimoProcedure.class, remap = false)
public abstract class CursedTechniqueKashimoProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1
    )
    private static void jja$handleCustomSkill(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        if (CursedTechniqueKashimoProcedureHook.handleCustomSkill(world, x, y, z, entity)) {
            ci.cancel();
        }
    }
}
