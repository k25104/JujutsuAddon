package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.PlayAnimationIfPossibleProcedureHook;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.PlayAnimationIfPossibleProcedure;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = PlayAnimationIfPossibleProcedure.class, remap = false)
public abstract class PlayAnimationIfPossibleProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;isEmpty()Z",
            shift = At.Shift.BEFORE
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        remap = false,
        require = 1
    )
    private static void jja$updateTwinnedBodyTechniqueAnimationState(
        Event event,
        LevelAccessor world,
        DamageSource damagesource,
        Entity entity,
        CallbackInfo ci,
        @Local(name = "animation_name") String animationName
    ) {
        PlayAnimationIfPossibleProcedureHook.updateTwinnedBodyTechniqueAnimationState(world, entity, animationName);
    }
}