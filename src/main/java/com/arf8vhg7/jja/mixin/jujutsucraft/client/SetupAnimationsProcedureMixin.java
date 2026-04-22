package com.arf8vhg7.jja.mixin.jujutsucraft.client;

import com.arf8vhg7.jja.hook.jujutsucraft.client.SetupAnimationsProcedureHook;
import net.mcreator.jujutsucraft.procedures.SetupAnimationsProcedure;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SetupAnimationsProcedure.class, remap = false)
public abstract class SetupAnimationsProcedureMixin {
    @Inject(method = "setAnimationClientside(Lnet/minecraft/world/entity/player/Player;Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private static void jja$applyTwinnedBodyExtraArmAnimation(Player player, String anim, boolean override, CallbackInfo ci) {
        if (SetupAnimationsProcedureHook.applyTwinnedBodyAnimation(player, anim, override)) {
            ci.cancel();
        }
    }
}