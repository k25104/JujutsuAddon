package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class DomainExpansionImmersivePortalsEvents {
    private DomainExpansionImmersivePortalsEvents() {
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        DomainExpansionImmersivePortalsService.onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        DomainExpansionImmersivePortalsService.tick(ServerLifecycleHooks.getCurrentServer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        DomainExpansionImmersivePortalsService.onPlayerLoggedOut(event.getEntity());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        DomainExpansionImmersivePortalsService.onServerStopping(event.getServer());
    }
}
