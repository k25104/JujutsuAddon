package com.arf8vhg7.jja.feature.player.progression.grade;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidResetService;
import com.arf8vhg7.jja.util.JjaItemUseHelper;
import net.mcreator.jujutsucraft.init.JujutsucraftModGameRules;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.WhenRespawnProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class SorcererGradeItemHandler {
    private SorcererGradeItemHandler() {
    }

    public static boolean handleRecommendation2(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack) {
        if (entity == null || itemStack == null || itemStack.getItem() != JujutsucraftModItems.RECOMMENDATION_2.get()) {
            return false;
        }

        boolean consume = false;
        if (entity instanceof ServerPlayer serverPlayer) {
            SorcererGradeTier targetTier = SorcererGradeAdvancementHelper.resolveRecommendation2Target(serverPlayer);
            if (targetTier != null) {
                SorcererGradeAdvancementHelper.awardUpTo(serverPlayer, targetTier);
                consume = true;
            }
        }

        boolean consumedByBat = JjaItemUseHelper.confirmConsumableUse(world, entity, consume);
        JjaItemUseHelper.applyCooldown(entity, itemStack, 5);
        if (consumedByBat) {
            finalizeRecommendationUse(world, x, y, z, entity, itemStack);
        } else {
            JjaItemUseHelper.displayDontUse(entity);
        }
        return true;
    }

    public static boolean handleSpecialDemotion(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack) {
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        SorcererGradeTier currentTier = SorcererGradeAdvancementHelper.findHighestTier(serverPlayer);
        if (currentTier == null || !currentTier.isSpecialTier()) {
            return false;
        }

        JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariables(serverPlayer);
        if (playerVars != null && playerVars.PlayerCursePowerChange != -1.0) {
            playerVars.PlayerCursePowerChange += 1.0;
            playerVars.syncPlayerVariables(serverPlayer);
        }

        double difficulty = world.getLevelData().getGameRules().getInt(JujutsucraftModGameRules.JUJUTSUUPGRADEDIFFICULTY);
        SorcererGradeTier targetTier = SorcererGradeAdvancementHelper.resolveSpecialDemotionTarget(serverPlayer);
        boolean consume = targetTier != null;
        if (consume && playerVars != null) {
            playerVars.PlayerFame = Math.min(playerVars.PlayerFame, SorcererGradeAdvancementHelper.demotionClampFor(targetTier, difficulty));
            playerVars.syncPlayerVariables(serverPlayer);
            SorcererGradeAdvancementHelper.revoke(serverPlayer, currentTier.advancementId());
            if (currentTier == SorcererGradeTier.SPECIAL) {
                SorcererGradeAdvancementHelper.revoke(serverPlayer, SorcererGradeAdvancementHelper.FAME_SPECIAL_ID);
            }
            SorcererGradeAdvancementHelper.awardUpTo(serverPlayer, targetTier);
        }

        boolean consumedByBat = JjaItemUseHelper.confirmConsumableUse(world, entity, consume);
        if (consumedByBat) {
            finalizeDemotionUse(world, x, y, z, entity, itemStack);
        } else {
            JjaItemUseHelper.displayDontUse(entity);
        }
        return true;
    }

    public static boolean isSpecialDemotion(Entity entity) {
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        SorcererGradeTier currentTier = SorcererGradeAdvancementHelper.findHighestTier(serverPlayer);
        return currentTier != null && currentTier.isSpecialTier();
    }

    private static void finalizeRecommendationUse(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.swing(InteractionHand.MAIN_HAND, true);
        }
        itemStack.shrink(1);
        FirstAidResetService.preserveDamageModel(entity, () -> WhenRespawnProcedure.execute(world, x, y, z, entity));
    }

    private static void finalizeDemotionUse(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemStack) {
        FirstAidResetService.preserveDamageModel(entity, () -> {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.removeAllEffects();
                if (livingEntity.getAttribute(Attributes.ARMOR) != null) {
                    livingEntity.getAttribute(Attributes.ARMOR).setBaseValue(0.0);
                }
                if (livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS) != null) {
                    livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(0.0);
                }
                livingEntity.swing(InteractionHand.MAIN_HAND, true);
            }
            WhenRespawnProcedure.execute(world, x, y, z, entity);
        });
        itemStack.shrink(1);
    }
}
