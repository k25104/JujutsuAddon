package com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client;

import com.arf8vhg7.jja.JujutsuAddon;
import java.util.Objects;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class TwinnedBodyRendererRegistration {
    private TwinnedBodyRendererRegistration() {
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (String skinName : event.getSkins()) {
            PlayerRenderer playerRenderer = event.getSkin(Objects.requireNonNull(skinName));
            if (playerRenderer != null) {
                playerRenderer.addLayer(new TwinnedBodyRenderLayer(playerRenderer));
            }
        }
    }
}
