package com.arf8vhg7.jja.hook.playeranimator.client;

import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client.JjaTwinnedBodyPlayerAnimationExecutionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class AnimationApplierHook {
    private AnimationApplierHook() {
    }

    public static boolean jja$shouldSuppressPlayerAnimatorUpdatePart(String partName) {
        return JjaTwinnedBodyPlayerAnimationExecutionContext.jja$shouldSuppressPlayerAnimatorUpdatePart(partName);
    }
}