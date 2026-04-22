package com.arf8vhg7.jja.mixin.minecraft.client;

import com.arf8vhg7.jja.compat.minecraft.HumanoidModelTwinnedBodyCarrierAccess;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin implements HumanoidModelTwinnedBodyCarrierAccess {
    @Unique
    private boolean jja$twinnedBodyCarrier;

    @Override
    public void jja$setTwinnedBodyCarrier(boolean carrier) {
        this.jja$twinnedBodyCarrier = carrier;
    }

    @Override
    public boolean jja$isTwinnedBodyCarrier() {
        return this.jja$twinnedBodyCarrier;
    }

    @WrapOperation(
        method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;isUsingItem()Z",
            ordinal = 0
        ),
        require = 1
    )
    private boolean jja$maskFirstIsUsingItem(LivingEntity livingEntity, Operation<Boolean> original) {
        return this.jja$twinnedBodyCarrier ? false : original.call(livingEntity);
    }

    @WrapOperation(
        method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;isUsingItem()Z",
            ordinal = 1
        ),
        require = 1
    )
    private boolean jja$maskSecondIsUsingItem(LivingEntity livingEntity, Operation<Boolean> original) {
        return this.jja$twinnedBodyCarrier ? false : original.call(livingEntity);
    }

    @Inject(method = "m_102875_", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void jja$skipRightHeldItemPoseIfCarrier(LivingEntity livingEntity, CallbackInfo ci) {
        if (this.jja$twinnedBodyCarrier) {
            ci.cancel();
        }
    }

    @Inject(method = "m_102878_", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void jja$skipLeftHeldItemPoseIfCarrier(LivingEntity livingEntity, CallbackInfo ci) {
        if (this.jja$twinnedBodyCarrier) {
            ci.cancel();
        }
    }
}