package com.arf8vhg7.jja.feature.jja.technique.shared.activation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class TechniqueStartGate {
    private TechniqueStartGate() {
    }

    public static int resolveSimpleDomainDurationForPlayer(Entity entity, int originalDuration) {
        return resolveSimpleDomainDuration(originalDuration, entity instanceof Player);
    }

    public static boolean applyHeldItemRule(Entity entity, boolean original) {
        if (!original || !(entity instanceof Player player)) {
            return original;
        }
        return applyHeldItemRule(original, TechniqueHeldItemRules.canUseSelectedSkill(player), player);
    }

    public static boolean shouldShowMagicCooldown(
        boolean cooldownTime,
        boolean cursedTechnique,
        boolean fallingBlossomEmotion,
        boolean domainAmplification,
        boolean unstable
    ) {
        return cooldownTime || cursedTechnique || fallingBlossomEmotion || domainAmplification || unstable;
    }

    static int resolveSimpleDomainDuration(int originalDuration, boolean ignoreSimpleDomainBlock) {
        return ignoreSimpleDomainBlock ? 0 : originalDuration;
    }

    static boolean applyHeldItemRule(boolean original, boolean canUseSkill) {
        return original && canUseSkill;
    }

    private static boolean applyHeldItemRule(boolean original, boolean canUseSkill, Player player) {
        if (applyHeldItemRule(original, canUseSkill)) {
            return true;
        }
        return JjaTechniqueUseDenialHelper.deny(player, true, false);
    }
}
