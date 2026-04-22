package com.arf8vhg7.jja.feature.jja.technique.shared.summon;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class SummonEnhancementEvents {
    private SummonEnhancementEvents() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        SummonEnhancementService.tryApplyPending(event.getEntity());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        SummonEnhancementService.clearRuntimeState(event.getOriginal());
        SummonEnhancementService.clearRuntimeState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        SummonEnhancementService.clearRuntimeState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        SummonEnhancementService.clearRuntimeState(event.getEntity());
    }
}
