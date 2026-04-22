package com.arf8vhg7.jja.feature.player.onboarding.start;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import java.util.ArrayList;
import java.util.List;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public final class StartJujutsuCraftRewardService {
    private static final ResourceLocation START_JUJUTSU_CRAFT = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "start_jujutsu_craft");

    public record RewardGrant(Item item, int count) {
    }

    private StartJujutsuCraftRewardService() {
    }

    public static void grantConfiguredRewards(Player player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }
        for (RewardGrant reward : configuredRewards()) {
            ItemStack stack = new ItemStack(reward.item());
            stack.setCount(reward.count());
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
    }

    public static boolean shouldGrantForAdvancement(ResourceLocation earnedAdvancementId, boolean isServerPlayer) {
        return isServerPlayer && START_JUJUTSU_CRAFT.equals(earnedAdvancementId);
    }

    static List<RewardGrant> configuredRewards() {
        return configuredRewards(
            JjaCommonConfig.CURSED_TECHNIQUE_CHANGER_COUNT.get(),
            JjaCommonConfig.PROFESSION_CHANGER_COUNT.get()
        );
    }

    static List<RewardGrant> configuredRewards(int cursedTechniqueChangerCount, int professionChangerCount) {
        List<RewardGrant> rewards = new ArrayList<>();
        for (StartJujutsuCraftRewardPlan.RewardCount rewardCount : StartJujutsuCraftRewardPlan.rewardCounts(
            cursedTechniqueChangerCount,
            professionChangerCount
        )) {
            addIfPositive(rewards, resolveItem(rewardCount.rewardKey()), rewardCount.count());
        }
        return rewards;
    }

    private static Item resolveItem(String rewardKey) {
        return switch (rewardKey) {
            case "cursed_technique_changer" -> JujutsucraftModItems.CURSED_TECHNIQUE_CHANGER.get();
            case "profession_changer" -> JujutsucraftModItems.PROFESSION_CHANGER.get();
            default -> throw new IllegalArgumentException("Unknown start reward key: " + rewardKey);
        };
    }

    private static void addIfPositive(List<RewardGrant> rewards, Item item, int count) {
        if (count > 0) {
            rewards.add(new RewardGrant(item, count));
        }
    }
}
