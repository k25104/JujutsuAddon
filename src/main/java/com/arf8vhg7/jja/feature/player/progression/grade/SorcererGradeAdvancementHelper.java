package com.arf8vhg7.jja.feature.player.progression.grade;

import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class SorcererGradeAdvancementHelper {
    public static final ResourceLocation FAME_SPECIAL_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "fame_special");
    public static final ResourceLocation UPDATE_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "update");
    private static final List<SorcererGradeTier> ASCENDING_TIERS = List.of(
        SorcererGradeTier.GRADE_4,
        SorcererGradeTier.GRADE_3,
        SorcererGradeTier.GRADE_2_SEMI,
        SorcererGradeTier.GRADE_2,
        SorcererGradeTier.GRADE_1_SEMI,
        SorcererGradeTier.GRADE_1,
        SorcererGradeTier.SPECIAL,
        SorcererGradeTier.SPECIAL_1,
        SorcererGradeTier.SPECIAL_2,
        SorcererGradeTier.SPECIAL_3,
        SorcererGradeTier.SPECIAL_4,
        SorcererGradeTier.SPECIAL_5
    );
    private static final List<SorcererGradeTier> DESCENDING_TIERS = List.of(
        SorcererGradeTier.SPECIAL_5,
        SorcererGradeTier.SPECIAL_4,
        SorcererGradeTier.SPECIAL_3,
        SorcererGradeTier.SPECIAL_2,
        SorcererGradeTier.SPECIAL_1,
        SorcererGradeTier.SPECIAL,
        SorcererGradeTier.GRADE_1,
        SorcererGradeTier.GRADE_1_SEMI,
        SorcererGradeTier.GRADE_2,
        SorcererGradeTier.GRADE_2_SEMI,
        SorcererGradeTier.GRADE_3,
        SorcererGradeTier.GRADE_4
    );
    private static final List<String> GRANT_UNTIL_PREFIXES = List.of(
        "advancement grant @s until jujutsucraft:sorcerer_grade_",
        "advancement grant @s until jja:sorcerer_grade_special_"
    );
    private static final Set<ResourceLocation> GRADE_ADVANCEMENT_IDS = Set.of(
        SorcererGradeTier.GRADE_4.advancementId(),
        SorcererGradeTier.GRADE_3.advancementId(),
        SorcererGradeTier.GRADE_2_SEMI.advancementId(),
        SorcererGradeTier.GRADE_2.advancementId(),
        SorcererGradeTier.GRADE_1_SEMI.advancementId(),
        SorcererGradeTier.GRADE_1.advancementId(),
        SorcererGradeTier.SPECIAL.advancementId(),
        SorcererGradeTier.SPECIAL_1.advancementId(),
        SorcererGradeTier.SPECIAL_2.advancementId(),
        SorcererGradeTier.SPECIAL_3.advancementId(),
        SorcererGradeTier.SPECIAL_4.advancementId(),
        SorcererGradeTier.SPECIAL_5.advancementId()
    );

    private SorcererGradeAdvancementHelper() {
    }

    public static boolean isManagedGrantUntilCommand(String command) {
        return command != null && GRANT_UNTIL_PREFIXES.stream().anyMatch(command::startsWith);
    }

    public static boolean isGradeAdvancement(ResourceLocation advancementId) {
        return advancementId != null && GRADE_ADVANCEMENT_IDS.contains(advancementId);
    }

    public static double resolveManagedPlayerLevel(ServerPlayer player) {
        return resolveManagedPlayerLevelForTier(findHighestTier(player));
    }

    public static double resolveManagedPlayerLevelForTier(SorcererGradeTier tier) {
        return tier == null ? 1.0 : tier.playerLevel();
    }

    public static double resolvePlayerLevel(ServerPlayer player) {
        return resolveManagedPlayerLevel(player);
    }

    public static SorcererGradeTier findHighestTier(ServerPlayer player) {
        if (player == null) {
            return null;
        }
        for (SorcererGradeTier tier : DESCENDING_TIERS) {
            if (JjaAdvancementHelper.has(player, tier.advancementId())) {
                return tier;
            }
        }
        return null;
    }

    public static boolean hasAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        return JjaAdvancementHelper.has(player, advancementId);
    }

    public static boolean awardUpTo(ServerPlayer player, SorcererGradeTier targetTier) {
        if (player == null || targetTier == null) {
            return false;
        }
        boolean changed = false;
        for (SorcererGradeTier tier : ASCENDING_TIERS) {
            if (tier.rank() > targetTier.rank()) {
                break;
            }
            changed |= award(player, tier.advancementId());
        }
        return changed;
    }

    public static boolean award(ServerPlayer player, ResourceLocation advancementId) {
        return JjaAdvancementHelper.award(player, advancementId);
    }

    public static boolean revoke(ServerPlayer player, ResourceLocation advancementId) {
        return JjaAdvancementHelper.revoke(player, advancementId);
    }

    public static SorcererGradeTier resolveSpecialTierByFame(double fame, double difficulty) {
        for (SorcererGradeTier tier : DESCENDING_TIERS) {
            if (tier.isSpecialTier() && fame >= tier.fameThreshold(difficulty)) {
                return tier;
            }
        }
        return null;
    }

    public static boolean syncSpecialTierFromFame(ServerPlayer player, double fame, double difficulty) {
        if (player == null || !hasAdvancement(player, FAME_SPECIAL_ID)) {
            return false;
        }
        SorcererGradeTier targetTier = resolveSpecialTierByFame(fame, difficulty);
        if (targetTier == null) {
            return false;
        }
        SorcererGradeTier currentTier = findHighestTier(player);
        if (currentTier != null && currentTier.rank() >= targetTier.rank()) {
            return false;
        }
        return awardUpTo(player, targetTier);
    }

    public static SorcererGradeTier resolveRecommendation2Target(ServerPlayer player) {
        SorcererGradeTier highestTier = findHighestTier(player);
        if (highestTier == SorcererGradeTier.GRADE_1) {
            return SorcererGradeTier.SPECIAL;
        }
        if (highestTier == SorcererGradeTier.SPECIAL) {
            return SorcererGradeTier.SPECIAL_1;
        }
        if (highestTier == SorcererGradeTier.SPECIAL_1) {
            return SorcererGradeTier.SPECIAL_2;
        }
        if (highestTier == SorcererGradeTier.SPECIAL_2) {
            return SorcererGradeTier.SPECIAL_3;
        }
        if (highestTier == SorcererGradeTier.SPECIAL_3) {
            return SorcererGradeTier.SPECIAL_4;
        }
        if (highestTier == SorcererGradeTier.SPECIAL_4) {
            return SorcererGradeTier.SPECIAL_5;
        }
        return null;
    }

    public static SorcererGradeTier resolveSpecialDemotionTarget(ServerPlayer player) {
        SorcererGradeTier highestTier = findHighestTier(player);
        if (highestTier == SorcererGradeTier.SPECIAL_5) {
            return SorcererGradeTier.SPECIAL_4;
        }
        if (highestTier == SorcererGradeTier.SPECIAL_4) {
            return SorcererGradeTier.SPECIAL_3;
        }
        if (highestTier == SorcererGradeTier.SPECIAL_3) {
            return SorcererGradeTier.SPECIAL_2;
        }
        if (highestTier == SorcererGradeTier.SPECIAL_2) {
            return SorcererGradeTier.SPECIAL_1;
        }
        if (highestTier == SorcererGradeTier.SPECIAL_1) {
            return SorcererGradeTier.SPECIAL;
        }
        if (highestTier == SorcererGradeTier.SPECIAL) {
            return SorcererGradeTier.GRADE_1;
        }
        return null;
    }

    public static double demotionClampFor(SorcererGradeTier targetTier, double difficulty) {
        if (targetTier == null) {
            return 0.0;
        }
        if (targetTier == SorcererGradeTier.GRADE_1) {
            return 2750.0 * difficulty;
        }
        if (targetTier.isSpecialTier()) {
            return targetTier.fameThreshold(difficulty);
        }
        return 0.0;
    }

}
