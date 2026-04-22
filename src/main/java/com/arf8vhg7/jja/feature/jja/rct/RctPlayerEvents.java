package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class RctPlayerEvents {
    private RctPlayerEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        if (!(JjaCommonConfig.RCT_OUTPUT_ENABLED.get()
            || JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get()
            || JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get()
            || JjaCommonConfig.AUTO_RCT_ENABLED.get())) {
            RctStateService.clearRuntimeState(player);
            return;
        }
        RctAutoService.tick(player);
        RctBrainService.tick(player);
        RctAddonTickService.tick(player);
    }
}
