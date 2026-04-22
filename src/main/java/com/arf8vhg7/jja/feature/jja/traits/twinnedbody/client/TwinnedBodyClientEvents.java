package com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID, value = Dist.CLIENT)
public final class TwinnedBodyClientEvents {
    private TwinnedBodyClientEvents() {
    }

    @SubscribeEvent
    public static void onLoggingOut(LoggingOut event) {
        TwinnedBodyClientState.clearAll();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
            return;
        }

        if (Minecraft.getInstance().level == null) {
            TwinnedBodyClientState.clearAll();
            return;
        }

        TwinnedBodyTechniqueAnimationState.tick();
    }
}
