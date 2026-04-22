package com.arf8vhg7.jja.hook.jujutsucraft.client;

import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.JjaTwinnedBodyTechniqueAnimationRules;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client.TwinnedBodyClientState;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client.TwinnedBodyTechniqueAnimationState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class SetupAnimationsProcedureHook {
    private SetupAnimationsProcedureHook() {
    }

    public static boolean applyTwinnedBodyAnimation(Player player, String animationName, boolean override) {
        if (!(player instanceof AbstractClientPlayer clientPlayer) || animationName == null || !TwinnedBodyClientState.isActive(clientPlayer)) {
            return false;
        }

        String normalizedAnimationName = animationName.toLowerCase(java.util.Locale.ROOT);
        if (JjaTwinnedBodyTechniqueAnimationRules.jjaIsAnimationReset(normalizedAnimationName)) {
            TwinnedBodyTechniqueAnimationState.clear(clientPlayer.getUUID());
            return false;
        }

        if (JjaTwinnedBodyTechniqueAnimationRules.jjaShouldMirrorOnExtraArms(normalizedAnimationName)) {
            TwinnedBodyTechniqueAnimationState.playExtraArmAnimation(clientPlayer, normalizedAnimationName, true, false);
            return false;
        }

        if (JjaTwinnedBodyTechniqueAnimationRules.jjaIsExtraArmOnlyAnimation(normalizedAnimationName)) {
            TwinnedBodyTechniqueAnimationState.playExtraArmAnimation(clientPlayer, normalizedAnimationName, override, true);
            return false;
        }

        return false;
    }
}