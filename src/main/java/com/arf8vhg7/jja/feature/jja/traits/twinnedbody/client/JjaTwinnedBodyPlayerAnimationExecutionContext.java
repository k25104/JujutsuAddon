package com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client;

import com.arf8vhg7.jja.hook.minecraft.client.HumanoidModelHook;
import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class JjaTwinnedBodyPlayerAnimationExecutionContext {
    private static final ThreadLocal<SetupAnimContext> CURRENT_CONTEXT = new ThreadLocal<>();

    private JjaTwinnedBodyPlayerAnimationExecutionContext() {
    }

    public static void jja$enterSetupAnimContext(LivingEntity livingEntity, @Nonnull PlayerModel<?> playerModel) {
        CURRENT_CONTEXT.set(new SetupAnimContext(livingEntity, playerModel, CURRENT_CONTEXT.get()));
    }

    public static void jja$exitSetupAnimContext() {
        SetupAnimContext currentContext = CURRENT_CONTEXT.get();
        if (currentContext == null) {
            return;
        }

        SetupAnimContext previousContext = currentContext.previousContext();
        if (previousContext == null) {
            CURRENT_CONTEXT.remove();
            return;
        }

        CURRENT_CONTEXT.set(previousContext);
    }

    public static boolean jja$shouldSuppressPlayerAnimatorUpdatePart(String partName) {
        SetupAnimContext currentContext = CURRENT_CONTEXT.get();
        return currentContext != null && currentContext.jja$shouldSuppressPlayerAnimatorUpdatePart(partName);
    }

    private record SetupAnimContext(LivingEntity livingEntity, PlayerModel<?> playerModel, SetupAnimContext previousContext) {
        private boolean jja$shouldSuppressPlayerAnimatorUpdatePart(String partName) {
            if (partName == null) {
                return false;
            }

            if (!(livingEntity instanceof AbstractClientPlayer player)) {
                return false;
            }

            if (!TwinnedBodyClientState.isActive(player)) {
                return false;
            }

            if (HumanoidModelHook.isTwinnedBodyCarrier(playerModel)) {
                return false;
            }

            return TwinnedBodyTechniqueAnimationState.shouldSuppressMainArms(player) && jja$isPlayerArmPart(partName);
        }

        private static boolean jja$isPlayerArmPart(String partName) {
            String normalizedPartName = partName.toLowerCase(Locale.ROOT);
            return "leftarm".equals(normalizedPartName) || "rightarm".equals(normalizedPartName);
        }
    }
}