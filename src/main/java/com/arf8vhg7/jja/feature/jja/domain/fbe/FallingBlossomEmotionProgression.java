package com.arf8vhg7.jja.feature.jja.domain.fbe;

import com.arf8vhg7.jja.feature.jja.domain.AntiDomainProgressionConfig;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class FallingBlossomEmotionProgression {
    public static final ResourceLocation MASTERY_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "mastery_falling_blossom_emotion"
    );
    private static final ResourceLocation UPSTREAM_SIMPLE_DOMAIN_MASTERY_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "mastery_simple_domain"
    );

    private FallingBlossomEmotionProgression() {
    }

    public static boolean hasUnlocked(ServerPlayer player) {
        return hasUnlocked(
            player != null && JjaAdvancementHelper.has(player, MASTERY_ID),
            player != null && JjaAdvancementHelper.has(player, UPSTREAM_SIMPLE_DOMAIN_MASTERY_ID),
            AntiDomainProgressionConfig.isFbeItemOnly()
        );
    }

    static boolean hasUnlocked(ServerPlayer player, boolean fbeItemOnly) {
        return hasUnlocked(
            player != null && JjaAdvancementHelper.has(player, MASTERY_ID),
            player != null && JjaAdvancementHelper.has(player, UPSTREAM_SIMPLE_DOMAIN_MASTERY_ID),
            fbeItemOnly
        );
    }

    static boolean hasUnlocked(boolean hasFbeItemMastery, boolean hasSimpleDomainMastery, boolean fbeItemOnly) {
        return hasFbeItemMastery || (!fbeItemOnly && hasSimpleDomainMastery);
    }
}
