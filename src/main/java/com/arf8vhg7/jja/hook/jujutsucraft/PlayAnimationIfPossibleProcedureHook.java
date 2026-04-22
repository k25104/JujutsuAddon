package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.JjaTwinnedBodyTechniqueAnimationRules;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyRuntimeStateAccess;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyTechniqueAnimationStateAccess;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public final class PlayAnimationIfPossibleProcedureHook {
    private PlayAnimationIfPossibleProcedureHook() {
    }

    public static void updateTwinnedBodyTechniqueAnimationState(LevelAccessor world, Entity entity, String animationName) {
        if (world == null || animationName == null || !(entity instanceof ServerPlayer player)) {
            return;
        }
        if (!(world instanceof Level level) || level.isClientSide()) {
            return;
        }

        if (JjaTwinnedBodyTechniqueAnimationRules.jjaIsAnimationReset(animationName)) {
            TwinnedBodyTechniqueAnimationStateAccess.clearTechniqueAnimationActive(player);
            return;
        }

        if (!TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(player) || !JjaTwinnedBodyTechniqueAnimationRules.jjaIsExtraArmOnlyAnimation(animationName)) {
            return;
        }

        KeyframeAnimation animation = PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath("jujutsucraft", animationName));
        if (animation == null) {
            return;
        }

        long activeUntilGameTime = level.getGameTime() + Math.max(1, animation.getLength());
        TwinnedBodyTechniqueAnimationStateAccess.setTechniqueAnimationActive(player, activeUntilGameTime);
    }
}