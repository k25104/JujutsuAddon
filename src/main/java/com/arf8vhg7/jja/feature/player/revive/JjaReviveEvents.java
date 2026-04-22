package com.arf8vhg7.jja.feature.player.revive;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaReviveEvents {
    private JjaReviveEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (ReviveFlowService.isWaiting(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        if (event.getNewTarget() != null && ReviveFlowService.isWaiting(event.getNewTarget())) {
            event.setNewTarget(null);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.player instanceof ServerPlayer player) {
            ReviveFlowService.tick(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (ReviveFlowService.isWaiting(player)) {
            JjaReviveFirstAidCompat.reapplyWaitingState(player);
            ReviveSyncService.syncWaitingState(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (ReviveFlowService.isWaiting(player)) {
            JjaReviveFirstAidCompat.reapplyWaitingState(player);
            ReviveSyncService.syncWaitingState(player);
        }
    }
}
