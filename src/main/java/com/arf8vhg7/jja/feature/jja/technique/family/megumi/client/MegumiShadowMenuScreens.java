package com.arf8vhg7.jja.feature.jja.technique.family.megumi.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class MegumiShadowMenuScreens {
    private MegumiShadowMenuScreens() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(MegumiShadowMenus.SHADOW_STORAGE.get(), MegumiShadowStorageScreen::new));
    }
}
