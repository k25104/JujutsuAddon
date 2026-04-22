package com.arf8vhg7.jja.hook.minecraft.client;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client.TwinnedBodyClientState;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client.TwinnedBodyModelParams;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client.TwinnedBodyTechniqueAnimationState;
import javax.annotation.Nonnull;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class PlayerModelHook {
    private static final float BASE_ARM_X = 5.0F;
    private static final float BASE_ARM_Y = 2.0F;
    private static final float BASE_ARM_Z = 0.0F;

    private PlayerModelHook() {
    }

    public static void syncTwinnedBodyArmPoses(LivingEntity livingEntity, @Nonnull PlayerModel<?> model) {
        if (!(livingEntity instanceof AbstractClientPlayer player) || !TwinnedBodyClientState.isActive(player)) {
            return;
        }

        if (HumanoidModelHook.isTwinnedBodyCarrier(model)) {
            return;
        }

        if (TwinnedBodyTechniqueAnimationState.shouldSuppressMainArms(player)) {
            resetVisibleMainArmPose(model.rightArm, -BASE_ARM_X);
            resetVisibleMainArmPose(model.leftArm, BASE_ARM_X);
            resetVisibleMainArmPose(model.rightSleeve, -BASE_ARM_X);
            resetVisibleMainArmPose(model.leftSleeve, BASE_ARM_X);
        }

        model.rightArm.xRot += TwinnedBodyModelParams.BASE_ARM_X_ROT_OFFSET;
        model.leftArm.xRot += TwinnedBodyModelParams.BASE_ARM_X_ROT_OFFSET;
        model.rightArm.zRot += TwinnedBodyModelParams.BASE_ARM_Z_ROT_OFFSET;
        model.leftArm.zRot -= TwinnedBodyModelParams.BASE_ARM_Z_ROT_OFFSET;
        model.rightArm.z += TwinnedBodyModelParams.BASE_ARM_Z_OFFSET;
        model.leftArm.z += TwinnedBodyModelParams.BASE_ARM_Z_OFFSET;
        model.rightSleeve.xRot += TwinnedBodyModelParams.BASE_ARM_X_ROT_OFFSET;
        model.leftSleeve.xRot += TwinnedBodyModelParams.BASE_ARM_X_ROT_OFFSET;
        model.rightSleeve.zRot += TwinnedBodyModelParams.BASE_ARM_Z_ROT_OFFSET;
        model.leftSleeve.zRot -= TwinnedBodyModelParams.BASE_ARM_Z_ROT_OFFSET;
        model.rightSleeve.z += TwinnedBodyModelParams.BASE_ARM_Z_OFFSET;
        model.leftSleeve.z += TwinnedBodyModelParams.BASE_ARM_Z_OFFSET;
    }

    private static void resetVisibleMainArmPose(ModelPart arm, float x) {
        arm.x = x;
        arm.y = BASE_ARM_Y;
        arm.z = BASE_ARM_Z;
        arm.xRot = 0.0F;
        arm.yRot = 0.0F;
        arm.zRot = 0.0F;
    }
}