package com.arf8vhg7.jja.mixin.minecraft.client;

import com.arf8vhg7.jja.hook.minecraft.client.HumanoidArmorLayerHook;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin {
    @Inject(
        method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 1
    )
    private void jja$suppressVanillaArmorPiece(
        PoseStack poseStack,
        MultiBufferSource multiBufferSource,
        LivingEntity livingEntity,
        EquipmentSlot equipmentSlot,
        int packedLight,
        HumanoidModel<?> armorModel,
        CallbackInfo ci
    ) {
        if (HumanoidArmorLayerHook.shouldSuppressVanillaArmorRender(livingEntity, equipmentSlot)) {
            ci.cancel();
        }
    }
}