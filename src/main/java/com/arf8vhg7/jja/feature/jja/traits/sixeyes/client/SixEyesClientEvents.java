package com.arf8vhg7.jja.feature.jja.traits.sixeyes.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.jja.traits.sixeyes.SixEyesOverlayService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID, value = Dist.CLIENT)
public final class SixEyesClientEvents {
    private SixEyesClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            SixEyesOverlayService.clearCustomOverlay();
            return;
        }
        SixEyesOverlayService.tickCustomOverlay(player);
    }

    @SubscribeEvent
    public static void onLoggingOut(LoggingOut event) {
        SixEyesOverlayService.clearCustomOverlay();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || !player.hasEffect(java.util.Objects.requireNonNull(JujutsucraftModMobEffects.SIX_EYES.get()))) {
            return;
        }
        if (!SixEyesOverlayService.hasCustomOverlay()) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        SixEyesOverlayRenderer.render(minecraft, guiGraphics, event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight(), SixEyesOverlayService.getCurrentSnapshot());
    }
}
