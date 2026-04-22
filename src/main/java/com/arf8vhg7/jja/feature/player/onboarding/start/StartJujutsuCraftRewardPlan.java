package com.arf8vhg7.jja.feature.player.onboarding.start;

import java.util.ArrayList;
import java.util.List;

public final class StartJujutsuCraftRewardPlan {
    public record RewardCount(String rewardKey, int count) {
    }

    private StartJujutsuCraftRewardPlan() {
    }

    public static List<RewardCount> rewardCounts(int cursedTechniqueChangerCount, int professionChangerCount) {
        List<RewardCount> rewards = new ArrayList<>();
        addIfPositive(rewards, "cursed_technique_changer", cursedTechniqueChangerCount);
        addIfPositive(rewards, "profession_changer", professionChangerCount);
        return rewards;
    }

    private static void addIfPositive(List<RewardCount> rewards, String rewardKey, int count) {
        if (count > 0) {
            rewards.add(new RewardCount(rewardKey, count));
        }
    }
}
