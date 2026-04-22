package com.arf8vhg7.jja.feature.player.progression.grade;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.domain.de.OpenBarrierMasteryReservationService;
import com.arf8vhg7.jja.feature.player.progression.physicalgifted.MasterPhysicalGiftedGrantService;
import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSync;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerReviveState;
import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import com.arf8vhg7.jja.feature.player.revive.ReviveSyncService;
import com.arf8vhg7.jja.feature.world.spawn.WeakestPlayerScaling;
import com.arf8vhg7.jja.feature.player.onboarding.start.StartJujutsuCraftRewardService;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.Event;

public final class CompletedAdvancementFlowService {
    private CompletedAdvancementFlowService() {
    }

    public static void postProcess(Event event, Entity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVariables == null) {
            return;
        }

        SorcererGradeTier highestTier = null;
        if (entity instanceof ServerPlayer serverPlayer) {
            highestTier = SorcererGradeAdvancementHelper.findHighestTier(serverPlayer);
        }

        maybeResetRemainingRevives(event, entity);
        if (MasterPhysicalGiftedGrantService.shouldGrant(
            highestTier,
            playerVariables.PlayerCurseTechnique,
            playerVariables.PlayerCurseTechnique2,
            playerVariables.PlayerCursePower,
            player.getInventory().contains(new ItemStack(JujutsucraftModItems.ITEM_MASTER_PHYSICAL_GIFTED.get()))
        )) {
            MasterPhysicalGiftedGrantService.grantIfEligible(player, playerVariables);
        }

        if (StartJujutsuCraftRewardService.shouldGrantForAdvancement(resolveEarnedAdvancementId(event), player instanceof ServerPlayer)) {
            StartJujutsuCraftRewardService.grantConfiguredRewards(player);
        }

        if (entity instanceof ServerPlayer serverPlayer) {
            WeakestPlayerScaling.refresh(serverPlayer.getServer());
        }
    }

    public static double resolveManagedPlayerLevelForComparison(Entity entity, double originalLevel) {
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return originalLevel;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(serverPlayer);
        if (playerVariables == null) {
            return originalLevel;
        }
        double resolvedLevel = SorcererGradeAdvancementHelper.resolveManagedPlayerLevel(serverPlayer);
        if (Double.compare(playerVariables.PlayerLevel, resolvedLevel) != 0) {
            playerVariables.PlayerLevel = resolvedLevel;
            playerVariables.syncPlayerVariables(serverPlayer);
        }
        return resolvedLevel;
    }

    static void maybeResetRemainingRevives(Event event, Entity entity) {
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return;
        }
        OpenBarrierMasteryReservationService.clearPendingIfAwarded(serverPlayer, resolveCompletedAdvancementId(event));
        ResourceLocation advancementId = resolveCompletedAdvancementId(event);
        if (!SorcererGradeAdvancementHelper.isGradeAdvancement(advancementId)) {
            return;
        }
        PlayerReviveState reviveState = PlayerStateAccess.revive(serverPlayer);
        if (reviveState == null) {
            return;
        }
        reviveState.setRemainingRevives(ReviveFlowService.INITIAL_REVIVES);
        JjaPlayerStateSync.sync(serverPlayer);
        if (ReviveFlowService.isWaiting(serverPlayer)) {
            ReviveSyncService.syncWaitingState(serverPlayer);
        }
    }

    static ResourceLocation resolveCompletedAdvancementId(Event event) {
        if (event instanceof AdvancementEvent.AdvancementEarnEvent advancementEarnEvent) {
            return advancementEarnEvent.getAdvancement().getId();
        }
        if (event instanceof AdvancementEvent.AdvancementProgressEvent advancementProgressEvent
            && advancementProgressEvent.getProgressType() == AdvancementEvent.AdvancementProgressEvent.ProgressType.GRANT
            && advancementProgressEvent.getAdvancementProgress().isDone()) {
            return advancementProgressEvent.getAdvancement().getId();
        }
        return null;
    }

    static ResourceLocation resolveEarnedAdvancementId(Event event) {
        if (event instanceof AdvancementEvent.AdvancementEarnEvent advancementEarnEvent) {
            return advancementEarnEvent.getAdvancement().getId();
        }
        return null;
    }
}
