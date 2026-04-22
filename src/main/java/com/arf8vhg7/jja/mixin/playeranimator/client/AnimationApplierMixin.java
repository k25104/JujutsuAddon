package com.arf8vhg7.jja.mixin.playeranimator.client;

import com.arf8vhg7.jja.hook.playeranimator.client.AnimationApplierHook;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AnimationApplier.class, remap = false)
public abstract class AnimationApplierMixin {
    @Inject(
        method = "updatePart(Ljava/lang/String;Lnet/minecraft/client/model/geom/ModelPart;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1
    )
    private void jja$skipTwinnedBodyMainArmUpdate(String partName, ModelPart modelPart, CallbackInfo ci) {
        if (AnimationApplierHook.jja$shouldSuppressPlayerAnimatorUpdatePart(partName)) {
            ci.cancel();
        }
    }
}