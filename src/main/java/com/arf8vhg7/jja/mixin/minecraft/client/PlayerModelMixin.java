package com.arf8vhg7.jja.mixin.minecraft.client;

import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client.JjaTwinnedBodyPlayerAnimationExecutionContext;
import com.arf8vhg7.jja.hook.minecraft.client.PlayerModelHook;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin {
    @Inject(
        method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
        at = @At("HEAD"),
        require = 1
    )
    private void jja$enterTwinnedBodyAnimationContext(
        LivingEntity livingEntity,
        float limbSwing,
        float limbSwingAmount,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        CallbackInfo ci
    ) {
        JjaTwinnedBodyPlayerAnimationExecutionContext.jja$enterSetupAnimContext(livingEntity, (PlayerModel<?>) (Object) this);
    }

    @Inject(
        method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
        at = @At("RETURN"),
        require = 1
    )
    private void jja$exitTwinnedBodyAnimationContext(
        LivingEntity livingEntity,
        float limbSwing,
        float limbSwingAmount,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        CallbackInfo ci
    ) {
        JjaTwinnedBodyPlayerAnimationExecutionContext.jja$exitSetupAnimContext();
        PlayerModelHook.syncTwinnedBodyArmPoses(livingEntity, (PlayerModel<?>) (Object) this);
    }
}