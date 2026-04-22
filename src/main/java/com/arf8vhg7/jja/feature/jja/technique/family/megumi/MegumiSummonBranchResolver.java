package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class MegumiSummonBranchResolver {
    private static final int MEGUMI_CT_ID = 6;

    private MegumiSummonBranchResolver() {
    }

    public static boolean isUntamedSelection(ServerPlayer player, int activeCtId, int selectedTechnique, @Nullable String currentTechniqueName) {
        if (player == null || activeCtId != MEGUMI_CT_ID || selectedTechnique <= 0) {
            return false;
        }
        int skillId = activeCtId * 100 + selectedTechnique;
        return switch (skillId) {
            case 608 -> !isTamed(player, 4, "skill_nue");
            case 609 -> !isTamed(player, 5, "skill_great_serpent");
            case 610 -> !isTamed(player, 6, "skill_toad");
            case 611 -> !isTamed(player, 7, "skill_max_elephant");
            case 612 -> !isTamed(player, 8, "skill_rabbit_escape");
            case 613 -> !isTamed(player, 9, "skill_round_deer");
            case 614 -> !isTamed(player, 10, "skill_piercing_ox");
            case 615 -> !isTamed(player, 11, "skill_tiger_funeral");
            case 618 -> !isTamed(player, 14, "skill_mahoraga");
            default -> false;
        };
    }

    private static boolean isTamed(ServerPlayer player, int techniqueIndex, String advancementPath) {
        return player.getPersistentData().getDouble("TenShadowsTechnique" + techniqueIndex) == 1.0
            || hasAdvancement(player, advancementPath);
    }

    private static boolean hasAdvancement(ServerPlayer player, String advancementPath) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(
            Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath("jujutsucraft", Objects.requireNonNull(advancementPath, "advancementPath")))
        );
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }
}
