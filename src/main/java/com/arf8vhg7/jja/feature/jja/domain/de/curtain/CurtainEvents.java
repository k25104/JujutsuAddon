package com.arf8vhg7.jja.feature.jja.domain.de.curtain;

import com.arf8vhg7.jja.JujutsuAddon;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class CurtainEvents {
    private CurtainEvents() {
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        CurtainRuntimeService.tick(ServerLifecycleHooks.getCurrentServer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CurtainSyncService.syncToPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CurtainRuntimeService.clearOwner(player.server, player.getUUID());
            CurtainRuntimeService.clearViewerState(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CurtainRuntimeService.clearOwner(player.server, player.getUUID());
            CurtainSyncService.syncToPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        UUID ownerId = player.getUUID();
        CurtainRuntimeService.clearOwner(player.server, ownerId);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        CurtainRuntimeService.clearAll(event.getServer());
    }
}
