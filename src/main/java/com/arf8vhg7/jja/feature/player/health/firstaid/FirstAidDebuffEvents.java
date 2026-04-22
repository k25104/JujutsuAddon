package com.arf8vhg7.jja.feature.player.health.firstaid;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class FirstAidDebuffEvents {
    private FirstAidDebuffEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FirstAidHealthSyncService.stripDebuffsAndSync(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FirstAidHealthSyncService.stripDebuffsAndSync(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        FirstAidHealthSyncService.applyPendingHealthBoostRestoreIfReady(player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (!event.shouldUpdateStaticData()) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            FirstAidHealthSyncService.stripDebuffsAndSync(player);
        }
    }
}
