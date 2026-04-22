package com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client;

import com.arf8vhg7.jja.compat.minecraft.HumanoidModelTwinnedBodyCarrierAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class TwinnedBodyRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    @Nullable
    private static PlayerModel<AbstractClientPlayer> playerCarrierModel;

    @Nullable
    private static PlayerModel<AbstractClientPlayer> playerSlimCarrierModel;

    public TwinnedBodyRenderLayer(net.minecraft.client.renderer.entity.RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(
        @Nonnull PoseStack poseStack,
        @Nonnull MultiBufferSource renderTypeBuffer,
        int packedLight,
        @Nonnull AbstractClientPlayer player,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        if (!TwinnedBodyClientState.isActive(player)) {
            return;
        }

        @Nonnull PlayerModel<AbstractClientPlayer> parentModel = Objects.requireNonNull(this.getParentModel());
        @Nonnull PlayerModel<AbstractClientPlayer> carrierModel = getCarrierModel(player);
        prepareCarrierModel(parentModel, carrierModel, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        TwinnedBodyTechniqueAnimationState.applyToCarrier(player, carrierModel, partialTicks);
        @Nonnull RenderType renderType = Objects.requireNonNull(RenderType.entityCutoutNoCull(Objects.requireNonNull(player.getSkinTextureLocation())));
        @Nonnull VertexConsumer vertexConsumer = Objects.requireNonNull(renderTypeBuffer.getBuffer(renderType));
        int overlayCoords = LivingEntityRenderer.getOverlayCoords(player, 0.0F);

        renderDuplicatedArm(
            Objects.requireNonNull(carrierModel.rightArm),
            poseStack,
            vertexConsumer,
            packedLight,
            overlayCoords,
            TwinnedBodyModelParams.EXTRA_ARM_Y_OFFSET,
            TwinnedBodyModelParams.EXTRA_ARM_Z_OFFSET,
            TwinnedBodyModelParams.EXTRA_ARM_X_ROT_OFFSET,
            TwinnedBodyModelParams.EXTRA_ARM_Z_ROT_OFFSET
        );
        renderDuplicatedArm(
            Objects.requireNonNull(carrierModel.leftArm),
            poseStack,
            vertexConsumer,
            packedLight,
            overlayCoords,
            TwinnedBodyModelParams.EXTRA_ARM_Y_OFFSET,
            TwinnedBodyModelParams.EXTRA_ARM_Z_OFFSET,
            TwinnedBodyModelParams.EXTRA_ARM_X_ROT_OFFSET,
            TwinnedBodyModelParams.EXTRA_ARM_Z_ROT_OFFSET
        );
    }

    @Nonnull
    private static PlayerModel<AbstractClientPlayer> getCarrierModel(@Nonnull AbstractClientPlayer player) {
        boolean slim = "slim".equals(player.getModelName());
        if (slim) {
            if (playerSlimCarrierModel == null) {
                playerSlimCarrierModel = bakeCarrierModel(ModelLayers.PLAYER_SLIM, true);
            }
            return Objects.requireNonNull(playerSlimCarrierModel);
        }

        if (playerCarrierModel == null) {
            playerCarrierModel = bakeCarrierModel(ModelLayers.PLAYER, false);
        }
        return Objects.requireNonNull(playerCarrierModel);
    }

    @Nonnull
    private static PlayerModel<AbstractClientPlayer> bakeCarrierModel(net.minecraft.client.model.geom.ModelLayerLocation layerLocation, boolean slim) {
        return new PlayerModel<>(
            Objects.requireNonNull(Minecraft.getInstance().getEntityModels().bakeLayer(Objects.requireNonNull(layerLocation))),
            slim
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void prepareCarrierModel(
        @Nonnull PlayerModel<AbstractClientPlayer> parentModel,
        @Nonnull PlayerModel<AbstractClientPlayer> carrierModel,
        @Nonnull AbstractClientPlayer player,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        ForgeHooksClient.copyModelProperties((HumanoidModel) parentModel, carrierModel);
        carrierModel.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
        carrierModel.attackTime = 0.0F;
        carrierModel.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        carrierModel.leftArmPose = HumanoidModel.ArmPose.EMPTY;

        if (carrierModel instanceof HumanoidModelTwinnedBodyCarrierAccess access) {
            access.jja$setTwinnedBodyCarrier(true);
            try {
                carrierModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            } finally {
                access.jja$setTwinnedBodyCarrier(false);
            }
            return;
        }

        carrierModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    private static void renderDuplicatedArm(
        @Nonnull ModelPart arm,
        @Nonnull PoseStack poseStack,
        @Nonnull VertexConsumer vertexConsumer,
        int packedLight,
        int packedOverlay,
        float yOffset,
        float zOffset,
        float xRotOffset,
        float zRotOffset
    ) {
        PartPose oldArmPose = Objects.requireNonNull(arm.storePose());

        try {
            arm.y += yOffset;
            arm.z += zOffset;
            arm.xRot += xRotOffset;
            arm.zRot += zRotOffset;

            arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        } finally {
            arm.loadPose(oldArmPose);
        }
    }
}
