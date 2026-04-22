package com.arf8vhg7.jja.feature.equipment.curios.client;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosLogicalSlot;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class CuriosArmorRenderService {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();

    @Nullable
    private static HumanoidArmorModel<LivingEntity> playerInnerModel;
    @Nullable
    private static HumanoidArmorModel<LivingEntity> playerOuterModel;
    @Nullable
    private static HumanoidArmorModel<LivingEntity> playerSlimInnerModel;
    @Nullable
    private static HumanoidArmorModel<LivingEntity> playerSlimOuterModel;

    private CuriosArmorRenderService() {
    }

    public static void renderManagedArmor(
        ItemStack stack,
        LivingEntity livingEntity,
        String identifier,
        PoseStack poseStack,
        RenderLayerParent<?, ?> renderLayerParent,
        MultiBufferSource renderTypeBuffer,
        int light,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        CuriosLogicalSlot slot = CuriosLogicalSlot.fromCuriosIdentifier(identifier);
        CuriosLogicalSlot managedSlot = CuriosEquipmentReadService.resolveManagedLogicalSlot(stack);

        if (slot == null || managedSlot != slot || !(stack.getItem() instanceof ArmorItem armorItem)) {
            return;
        }

        EquipmentSlot equipmentSlot = slot.equipmentSlot();
        if (armorItem.getEquipmentSlot() != equipmentSlot) {
            return;
        }

        if (!(renderLayerParent.getModel() instanceof HumanoidModel<?> parentModel)) {
            return;
        }

        HumanoidArmorModel<LivingEntity> defaultModel = getDefaultArmorModel(livingEntity, equipmentSlot);
        copyModelProperties(parentModel, defaultModel);
        setPartVisibility(defaultModel, equipmentSlot);

        Model armorModel = ForgeHooksClient.getArmorModel(livingEntity, stack, equipmentSlot, defaultModel);
        boolean usesInnerModel = usesInnerModel(equipmentSlot);

        if (armorItem instanceof DyeableLeatherItem dyeableLeatherItem) {
            int color = dyeableLeatherItem.getColor(stack);
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;
            renderModel(
                poseStack,
                renderTypeBuffer,
                light,
                armorModel,
                getArmorResource(livingEntity, stack, equipmentSlot, null),
                red,
                green,
                blue
            );
            renderModel(
                poseStack,
                renderTypeBuffer,
                light,
                armorModel,
                getArmorResource(livingEntity, stack, equipmentSlot, "overlay"),
                1.0F,
                1.0F,
                1.0F
            );
        } else {
            renderModel(
                poseStack,
                renderTypeBuffer,
                light,
                armorModel,
                getArmorResource(livingEntity, stack, equipmentSlot, null),
                1.0F,
                1.0F,
                1.0F
            );
        }

        ArmorTrim.getTrim(livingEntity.level().registryAccess(), stack).ifPresent(trim ->
            renderTrim(armorItem.getMaterial(), poseStack, renderTypeBuffer, light, trim, armorModel, usesInnerModel)
        );

        if (stack.hasFoil()) {
            renderGlint(poseStack, renderTypeBuffer, light, armorModel);
        }
    }

    private static HumanoidArmorModel<LivingEntity> getDefaultArmorModel(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        boolean slim = livingEntity instanceof AbstractClientPlayer player && "slim".equals(player.getModelName());
        boolean inner = usesInnerModel(equipmentSlot);

        if (slim) {
            if (inner) {
                if (playerSlimInnerModel == null) {
                    playerSlimInnerModel = bakeArmorModel(ModelLayers.PLAYER_SLIM_INNER_ARMOR);
                }
                return playerSlimInnerModel;
            }
            if (playerSlimOuterModel == null) {
                playerSlimOuterModel = bakeArmorModel(ModelLayers.PLAYER_SLIM_OUTER_ARMOR);
            }
            return playerSlimOuterModel;
        }

        if (inner) {
            if (playerInnerModel == null) {
                playerInnerModel = bakeArmorModel(ModelLayers.PLAYER_INNER_ARMOR);
            }
            return playerInnerModel;
        }
        if (playerOuterModel == null) {
            playerOuterModel = bakeArmorModel(ModelLayers.PLAYER_OUTER_ARMOR);
        }
        return playerOuterModel;
    }

    private static HumanoidArmorModel<LivingEntity> bakeArmorModel(ModelLayerLocation layerLocation) {
        return new HumanoidArmorModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void copyModelProperties(HumanoidModel<?> parentModel, HumanoidModel<LivingEntity> armorModel) {
        ForgeHooksClient.copyModelProperties((HumanoidModel) parentModel, armorModel);
    }

    private static void setPartVisibility(HumanoidModel<?> armorModel, EquipmentSlot equipmentSlot) {
        armorModel.setAllVisible(false);
        switch (equipmentSlot) {
            case HEAD -> {
                armorModel.head.visible = true;
                armorModel.hat.visible = true;
            }
            case CHEST -> {
                armorModel.body.visible = true;
                armorModel.rightArm.visible = true;
                armorModel.leftArm.visible = true;
            }
            case LEGS -> {
                armorModel.body.visible = true;
                armorModel.rightLeg.visible = true;
                armorModel.leftLeg.visible = true;
            }
            case FEET -> {
                armorModel.rightLeg.visible = true;
                armorModel.leftLeg.visible = true;
            }
            default -> {
            }
        }
    }

    private static boolean usesInnerModel(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS;
    }

    private static void renderModel(
        PoseStack poseStack,
        MultiBufferSource renderTypeBuffer,
        int light,
        Model armorModel,
        ResourceLocation armorResource,
        float red,
        float green,
        float blue
    ) {
        VertexConsumer vertexConsumer = renderTypeBuffer.getBuffer(RenderType.armorCutoutNoCull(armorResource));
        armorModel.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }

    private static void renderTrim(
        ArmorMaterial armorMaterial,
        PoseStack poseStack,
        MultiBufferSource renderTypeBuffer,
        int light,
        ArmorTrim trim,
        Model armorModel,
        boolean usesInnerModel
    ) {
        TextureAtlas armorTrimAtlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
        TextureAtlasSprite sprite = armorTrimAtlas.getSprite(
            usesInnerModel ? trim.innerTexture(armorMaterial) : trim.outerTexture(armorMaterial)
        );
        VertexConsumer vertexConsumer = sprite.wrap(renderTypeBuffer.getBuffer(Sheets.armorTrimsSheet()));
        armorModel.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void renderGlint(PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, Model armorModel) {
        armorModel.renderToBuffer(
            poseStack,
            renderTypeBuffer.getBuffer(RenderType.armorEntityGlint()),
            light,
            OverlayTexture.NO_OVERLAY,
            1.0F,
            1.0F,
            1.0F,
            1.0F
        );
    }

    private static ResourceLocation getArmorResource(
        LivingEntity livingEntity,
        ItemStack stack,
        EquipmentSlot equipmentSlot,
        @Nullable String type
    ) {
        ArmorItem armorItem = (ArmorItem) stack.getItem();
        String texture = armorItem.getMaterial().getName();
        String domain = "minecraft";
        int separator = texture.indexOf(':');

        if (separator != -1) {
            domain = texture.substring(0, separator);
            texture = texture.substring(separator + 1);
        }

        String path = String.format(
            java.util.Locale.ROOT,
            "%s:textures/models/armor/%s_layer_%d%s.png",
            domain,
            texture,
            usesInnerModel(equipmentSlot) ? 2 : 1,
            type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type)
        );

        path = ForgeHooksClient.getArmorTexture(livingEntity, stack, path, equipmentSlot, type);
        return ARMOR_LOCATION_CACHE.computeIfAbsent(path, ResourceLocation::parse);
    }
}
