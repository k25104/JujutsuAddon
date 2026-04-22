package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.ChangeTechniqueTestProcedureHook;
import net.mcreator.jujutsucraft.procedures.ChangeTechniqueTestProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChangeTechniqueTestProcedure.class, remap = false)
public abstract class ChangeTechniqueTestProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;DD)Z",
        at = @At("RETURN"),
        cancellable = true,
        remap = false
    ,
        require = 1
    )
    private static void jja$applyHiddenSkip(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        double playerCt,
        double playerSelect,
        CallbackInfoReturnable<Boolean> cir
    ) {
        cir.setReturnValue(ChangeTechniqueTestProcedureHook.applySelectionRules(entity, playerCt, playerSelect, cir.getReturnValue()));
    }
}
