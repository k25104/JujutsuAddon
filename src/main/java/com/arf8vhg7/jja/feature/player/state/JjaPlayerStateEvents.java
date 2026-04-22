package com.arf8vhg7.jja.feature.player.state;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import com.arf8vhg7.jja.feature.jja.rct.RctHealTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaPlayerStateEvents {
    private JjaPlayerStateEvents() {
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer)) {
            event.addCapability(JjaPlayerCapability.ID, new JjaPlayerStateProvider());
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        event.getOriginal().revive();
        JjaPlayerState original = JjaPlayerCapability.get(event.getOriginal());
        JjaPlayerState clone = JjaPlayerCapability.get(event.getEntity());
        if (original != null && clone != null) {
            clone.copyForCloneFrom(original, event.isWasDeath());
        }
        JjaPlayerStateLifecycleService.refreshRuntimeAndSync(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        RctHealTracker.awardProgression(event.getEntity());
        JjaPlayerStateLifecycleService.refreshRuntimeAndSync(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        JjaPlayerStateLifecycleService.refreshRuntimeAndSync(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        JjaPlayerStateSync.sync(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        DomainExpansionHookSupport.clearRadiusRuntime(event.getEntity());
    }
}
