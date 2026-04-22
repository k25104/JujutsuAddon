package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyRuntimeStateAccess;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyTechniqueAnimationStateAccess;
import java.util.Set;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class SukunaComboProjectileRules {
    private static final Set<Integer> RESTRICTED_SKILL_IDS = Set.of(111, 112, 113);

    private SukunaComboProjectileRules() {
    }

    public static boolean allowProjectileSlash(Entity entity, boolean original) {
        return original && !shouldSuppressProjectileSlash(entity);
    }

    public static boolean shouldSuppressProjectileSlash(Entity entity) {
        if (!(entity instanceof Player player) || player.isCreative()) {
            return false;
        }

        boolean twinnedBodyExtraArmsUsed = TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(player)
            && TwinnedBodyTechniqueAnimationStateAccess.isTechniqueAnimationActive(player);
        PlayerHandState handState = PlayerHandState.resolve(player, twinnedBodyExtraArmsUsed);
        return shouldSuppressProjectileSlash(
            player.hasEffect(JujutsucraftModMobEffects.SUKUNA_EFFECT.get()),
            TechniqueSkillResolver.resolveCurrentSkillId(entity),
            TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(player),
            twinnedBodyExtraArmsUsed,
            handState,
            player.getMainArm()
        );
    }

    public static boolean allowProjectileSlash(Entity entity, ItemStack mainHandSnapshot, boolean original) {
        return original && !shouldSuppressProjectileSlash(entity, mainHandSnapshot);
    }

    public static boolean shouldSuppressProjectileSlash(Entity entity, ItemStack mainHandSnapshot) {
        if (!(entity instanceof Player player) || player.isCreative()) {
            return false;
        }

        boolean twinnedBodyExtraArmsUsed = TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(player)
            && TwinnedBodyTechniqueAnimationStateAccess.isTechniqueAnimationActive(player);
        HandItemState mainHandState = PlayerHandStateRules.classifyHandItem(player, mainHandSnapshot);
        return shouldSuppressProjectileSlash(
            player.hasEffect(JujutsucraftModMobEffects.SUKUNA_EFFECT.get()),
            TechniqueSkillResolver.resolveCurrentSkillId(entity),
            TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(player),
            twinnedBodyExtraArmsUsed,
            mainHandState
        );
    }

    static boolean shouldSuppressProjectileSlash(
        boolean hasSukunaEffect,
        int currentSkillId,
        boolean twinnedBodyMarked,
        boolean twinnedBodyExtraArmsUsed,
        PlayerHandState handState,
        HumanoidArm mainArm
    ) {
        if (!hasSukunaEffect || !RESTRICTED_SKILL_IDS.contains(currentSkillId)) {
            return false;
        }
        if (twinnedBodyMarked && !twinnedBodyExtraArmsUsed) {
            return false;
        }
        return handState.isMainHandMeaningful(mainArm);
    }

    static boolean shouldSuppressProjectileSlash(
        boolean hasSukunaEffect,
        int currentSkillId,
        boolean twinnedBodyMarked,
        boolean twinnedBodyExtraArmsUsed,
        HandItemState mainHandState
    ) {
        if (!hasSukunaEffect || !RESTRICTED_SKILL_IDS.contains(currentSkillId)) {
            return false;
        }
        if (twinnedBodyMarked && !twinnedBodyExtraArmsUsed) {
            return false;
        }
        return mainHandState != null && mainHandState.isMeaningfulHeldItem();
    }
}
