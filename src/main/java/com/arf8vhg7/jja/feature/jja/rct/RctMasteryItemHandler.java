package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.util.JjaCommandHelper;
import com.arf8vhg7.jja.util.JjaItemUseHelper;
import java.util.List;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class RctMasteryItemHandler {
    private static final List<ResourceLocation> ITEM_AWARD_ORDER = List.of(
        RctAdvancementHelper.RCT_1_ID,
        RctAdvancementHelper.MASTERY_RCT_OUTPUT_ID,
        RctAdvancementHelper.MASTERY_RCT_BRAIN_DESTRUCTION_ID,
        RctAdvancementHelper.RCT_2_ID,
        RctAdvancementHelper.MASTERY_RCT_BRAIN_REGENERATION_ID,
        RctAdvancementHelper.MASTERY_RCT_AUTO_ID
    );

    private RctMasteryItemHandler() {
    }

    public static boolean handle(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack) {
        if (entity == null || itemStack == null || itemStack.getItem() != JujutsucraftModItems.ITEM_MASTER_REVERSE_CURSED_TECHNIQUE.get()) {
            return false;
        }
        boolean outputEnabled = JjaCommonConfig.RCT_OUTPUT_ENABLED.get();
        boolean brainDestructionEnabled = JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get();
        boolean brainRegenerationEnabled = JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get();
        boolean autoEnabled = JjaCommonConfig.AUTO_RCT_ENABLED.get();
        if (!(outputEnabled || brainDestructionEnabled || brainRegenerationEnabled || autoEnabled)) {
            return false;
        }

        boolean shouldConsume = awardNext(entity, outputEnabled, brainDestructionEnabled, brainRegenerationEnabled, autoEnabled);
        boolean consume = JjaItemUseHelper.confirmConsumableUse(world, entity, shouldConsume);
        JjaItemUseHelper.applyCooldown(entity, itemStack, 5);
        playClickSound(entity);
        if (consume) {
            itemStack.shrink(1);
        } else {
            JjaItemUseHelper.displayDontUse(entity);
        }
        return true;
    }

    private static boolean awardNext(
        Entity entity,
        boolean outputEnabled,
        boolean brainDestructionEnabled,
        boolean brainRegenerationEnabled,
        boolean autoEnabled
    ) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }
        boolean cursedSpirit = RctMath.isCursedSpirit(player);
        for (ResourceLocation advancementId : ITEM_AWARD_ORDER) {
            if (advancementId == RctAdvancementHelper.MASTERY_RCT_OUTPUT_ID) {
                if (!outputEnabled) {
                    continue;
                }
            } else if (advancementId == RctAdvancementHelper.MASTERY_RCT_BRAIN_DESTRUCTION_ID) {
                if (!brainDestructionEnabled) {
                    continue;
                }
            } else if (advancementId == RctAdvancementHelper.MASTERY_RCT_BRAIN_REGENERATION_ID) {
                if (!brainRegenerationEnabled) {
                    continue;
                }
            } else if (advancementId == RctAdvancementHelper.MASTERY_RCT_AUTO_ID) {
                if (!autoEnabled) {
                    continue;
                }
            }
            if (!RctTechniqueRestrictionRules.canUnlockAdvancement(cursedSpirit, advancementId)) {
                continue;
            }
            if (!RctAdvancementHelper.hasAdvancement(player, advancementId)) {
                return RctAdvancementHelper.award(player, advancementId);
            }
        }
        return false;
    }

    private static void playClickSound(Entity entity) {
        JjaCommandHelper.executeAsEntity(entity, "playsound ui.button.click master @s");
    }
}
