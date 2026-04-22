package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class MegumiShadowImmersionEvents {
    private MegumiShadowImmersionEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        MegumiShadowImmersionService.tick(player);
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        MegumiShadowImmersionService.clearRuntimeState(event.getOriginal());
        MegumiShadowImmersionService.clearRuntimeState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        MegumiShadowImmersionService.clearRuntimeState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        MegumiShadowImmersionService.clearRuntimeState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        MegumiShadowImmersionService.clearRuntimeState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        MegumiShadowImmersionService.clearRuntimeState(event.getEntity());
    }
}
