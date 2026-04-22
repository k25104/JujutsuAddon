package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.progression.physicalgifted.MasterPhysicalGiftedGrantService;
import com.arf8vhg7.jja.feature.player.progression.grade.CompletedAdvancementFlowService;
import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeTier;
import com.arf8vhg7.jja.feature.player.onboarding.start.StartJujutsuCraftRewardService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;

public final class CompletedAdvancementProcedureHook {
    private CompletedAdvancementProcedureHook() {
    }

    public static void postProcess(Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
        CompletedAdvancementFlowService.postProcess(event, entity);
    }

    public static double resolveManagedPlayerLevelForComparison(Entity entity, double originalLevel) {
        return CompletedAdvancementFlowService.resolveManagedPlayerLevelForComparison(entity, originalLevel);
    }

    static boolean shouldGrantPhysicalGifted(
        SorcererGradeTier highestTier,
        double playerCurseTechnique,
        double playerCurseTechnique2,
        double playerCursePower,
        boolean alreadyHasPhysicalGifted
    ) {
        return MasterPhysicalGiftedGrantService.shouldGrant(
            highestTier,
            playerCurseTechnique,
            playerCurseTechnique2,
            playerCursePower,
            alreadyHasPhysicalGifted
        );
    }

    static boolean shouldGrantStartJujutsuCraftRewards(ResourceLocation earnedAdvancementId, boolean isServerPlayer) {
        return StartJujutsuCraftRewardService.shouldGrantForAdvancement(earnedAdvancementId, isServerPlayer);
    }
}
