package com.arf8vhg7.jja.feature.jja.resource.ce;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class CePoolScalingEvents {
    private CePoolScalingEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        JjaCursePowerAccountingService.refreshPlayerCursePowerFormer(event.getEntity());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        CePoolScalingSync.refresh(ServerLifecycleHooks.getCurrentServer());
    }

    @EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD)
    public static final class ModConfigHooks {
        private ModConfigHooks() {
        }

        @SubscribeEvent
        public static void onConfigLoading(ModConfigEvent.Loading event) {
            if (event.getConfig().getSpec() == JjaCommonConfig.SPEC) {
                CePoolScalingSync.markDirty();
            }
        }

        @SubscribeEvent
        public static void onConfigReloading(ModConfigEvent.Reloading event) {
            if (event.getConfig().getSpec() == JjaCommonConfig.SPEC) {
                CePoolScalingSync.markDirty();
            }
        }
    }
}
