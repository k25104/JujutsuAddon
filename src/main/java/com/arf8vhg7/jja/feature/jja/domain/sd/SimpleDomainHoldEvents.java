package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class SimpleDomainHoldEvents {
    private SimpleDomainHoldEvents() {
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        SimpleDomainHoldService.clearRuntimeState(event.getOriginal());
        SimpleDomainHoldService.clearRuntimeState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        SimpleDomainHoldService.clearRuntimeState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        SimpleDomainHoldService.clearRuntimeState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        SimpleDomainHoldService.clearRuntimeState(event.getEntity());
    }
}
