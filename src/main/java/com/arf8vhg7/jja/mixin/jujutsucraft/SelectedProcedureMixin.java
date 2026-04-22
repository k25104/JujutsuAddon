package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SelectedProcedureHook;
import net.mcreator.jujutsucraft.procedures.SelectedProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SelectedProcedure.class, remap = false)
public abstract class SelectedProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$beginFirstAidReset(
        net.minecraft.world.level.LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        SelectedProcedureHook.beginFirstAidReset(entity);
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 1)
    private static void jja$finishSelectionHooks(
        net.minecraft.world.level.LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        SelectedProcedureHook.finishFirstAidReset(entity);
        SelectedProcedureHook.applyTwinnedBodySelectionReward(entity);
    }
}
