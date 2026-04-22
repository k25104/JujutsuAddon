package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class DomainExpansionConfiguredRadiusEvents {
    private DomainExpansionConfiguredRadiusEvents() {
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        DomainExpansionConfiguredRadiusSync.refresh(event.getServer());
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        DomainExpansionConfiguredRadiusSync.refresh(level.getServer());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        var server = ServerLifecycleHooks.getCurrentServer();
        DomainExpansionConfiguredRadiusSync.refresh(server);
    }

    @EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD)
    public static final class ModConfigHooks {
        private ModConfigHooks() {
        }

        @SubscribeEvent
        public static void onConfigLoading(ModConfigEvent.Loading event) {
            if (event.getConfig().getSpec() == JjaCommonConfig.SPEC) {
                DomainExpansionConfiguredRadiusSync.markDirty();
            }
        }

        @SubscribeEvent
        public static void onConfigReloading(ModConfigEvent.Reloading event) {
            if (event.getConfig().getSpec() == JjaCommonConfig.SPEC) {
                DomainExpansionConfiguredRadiusSync.markDirty();
            }
        }
    }
}
