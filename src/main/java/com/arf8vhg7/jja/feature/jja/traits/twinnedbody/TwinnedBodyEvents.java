package com.arf8vhg7.jja.feature.jja.traits.twinnedbody;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerEvent.StopTracking;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class TwinnedBodyEvents {
    private TwinnedBodyEvents() {
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        TwinnedBodyRuntimeStateAccess.copyTwinnedBody(event.getOriginal(), event.getEntity());
        TwinnedBodyTechniqueAnimationStateAccess.clearTechniqueAnimationActive(event.getEntity());
        TwinnedBodyRuntimeStateAccess.clearCombatEchoConsumption(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TwinnedBodyTechniqueAnimationStateAccess.clearTechniqueAnimationActive(player);
            TwinnedBodyRuntimeStateAccess.clearCombatEchoConsumption(player);
            TwinnedBodySyncService.syncTrackingState(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TwinnedBodyTechniqueAnimationStateAccess.clearTechniqueAnimationActive(player);
            TwinnedBodyRuntimeStateAccess.clearCombatEchoConsumption(player);
            TwinnedBodySyncService.syncTrackingState(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TwinnedBodyRuntimeStateAccess.clearCombatEchoConsumption(player);
            TwinnedBodySyncService.syncTrackingState(player);
        }
    }

    @SubscribeEvent
    public static void onStartTracking(StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TwinnedBodySyncService.sendTrackingState(player, event.getTarget());
        }
    }

    @SubscribeEvent
    public static void onStopTracking(StopTracking event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TwinnedBodySyncService.clearTrackingState(player, event.getTarget());
        }
    }
}
