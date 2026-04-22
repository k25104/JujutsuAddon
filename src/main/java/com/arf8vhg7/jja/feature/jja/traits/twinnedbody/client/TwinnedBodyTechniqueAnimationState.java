package com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class TwinnedBodyTechniqueAnimationState {
    private static final Map<UUID, PlaybackState> EXTRA_ARM_ANIMATIONS = new HashMap<>();

    private TwinnedBodyTechniqueAnimationState() {
    }

    public static boolean playExtraArmAnimation(AbstractClientPlayer player, String animationName, boolean override, boolean suppressMainArms) {
        return playAnimation(player, animationName, override, suppressMainArms);
    }

    public static boolean hasActiveAnimation(@Nullable AbstractClientPlayer player) {
        if (player == null) {
            return false;
        }

        PlaybackState state = EXTRA_ARM_ANIMATIONS.get(player.getUUID());
        return state != null && state.animation.isActive();
    }

    public static boolean shouldSuppressMainArms(@Nullable AbstractClientPlayer player) {
        if (player == null) {
            return false;
        }

        PlaybackState state = EXTRA_ARM_ANIMATIONS.get(player.getUUID());
        return state != null && state.animation.isActive() && state.suppressMainArms;
    }

    private static boolean playAnimation(AbstractClientPlayer player, String animationName, boolean override, boolean suppressMainArms) {
        KeyframeAnimation animation = PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath("jujutsucraft", animationName));
        if (animation == null) {
            return false;
        }

        UUID playerId = player.getUUID();
        PlaybackState currentState = EXTRA_ARM_ANIMATIONS.get(playerId);
        if (!override && currentState != null && currentState.animation.isActive()) {
            return true;
        }

        EXTRA_ARM_ANIMATIONS.put(playerId, new PlaybackState(new KeyframeAnimationPlayer(animation), suppressMainArms));
        return true;
    }

    public static void applyToCarrier(AbstractClientPlayer player, HumanoidModel<?> carrierModel, float partialTicks) {
        PlaybackState state = EXTRA_ARM_ANIMATIONS.get(player.getUUID());
        if (state == null || !state.animation.isActive()) {
            return;
        }

        AnimationApplier animationApplier = new AnimationApplier(state.animation);
        animationApplier.setTickDelta(partialTicks);
        KeyframeAnimation animationData = state.animation.getData();
        applyFirstAvailable(animationApplier, animationData, carrierModel.body, "body", "torso");
        applyFirstAvailable(animationApplier, animationData, carrierModel.rightArm, "right_arm", "rightArm");
        applyFirstAvailable(animationApplier, animationData, carrierModel.leftArm, "left_arm", "leftArm");
    }

    public static void tick() {
        Iterator<Map.Entry<UUID, PlaybackState>> iterator = EXTRA_ARM_ANIMATIONS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PlaybackState> entry = iterator.next();
            PlaybackState state = entry.getValue();
            state.animation.tick();
            if (!state.animation.isActive()) {
                iterator.remove();
            }
        }
    }

    public static void clear(@Nullable UUID playerId) {
        if (playerId != null) {
            EXTRA_ARM_ANIMATIONS.remove(playerId);
        }
    }

    public static void clearAll() {
        EXTRA_ARM_ANIMATIONS.clear();
    }

    private static final class PlaybackState {
        private final KeyframeAnimationPlayer animation;
        private final boolean suppressMainArms;

        private PlaybackState(KeyframeAnimationPlayer animation, boolean suppressMainArms) {
            this.animation = animation;
            this.suppressMainArms = suppressMainArms;
        }
    }

    private static void applyFirstAvailable(
        @Nonnull AnimationApplier animationApplier,
        @Nonnull KeyframeAnimation animation,
        @Nonnull ModelPart modelPart,
        @Nonnull String firstPartName,
        @Nonnull String secondPartName
    ) {
        if (animation.getPartOptional(firstPartName).isPresent()) {
            animationApplier.updatePart(firstPartName, modelPart);
            return;
        }

        if (animation.getPartOptional(secondPartName).isPresent()) {
            animationApplier.updatePart(secondPartName, modelPart);
        }
    }

}