package com.arf8vhg7.jja.feature.jja.domain.de.curtain.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.CurtainShellVisionMode;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID, value = Dist.CLIENT)
public final class CurtainClientEvents {
    private CurtainClientEvents() {
    }

    @SubscribeEvent
    public static void onLoggingOut(LoggingOut event) {
        CurtainClientState.clearAll();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            CurtainClientState.clearAll();
            return;
        }

        CurtainShellVisionMode nextMode = CurtainClientState.resolveShellVisionMode(minecraft.player);
        if (CurtainClientState.updateShellVisionMode(nextMode)) {
            minecraft.levelRenderer.allChanged();
        }
    }
}
