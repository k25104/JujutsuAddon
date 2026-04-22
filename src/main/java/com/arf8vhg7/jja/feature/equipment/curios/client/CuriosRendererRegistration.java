package com.arf8vhg7.jja.feature.equipment.curios.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.compat.curios.JjaCuriosClientCompat;
import com.arf8vhg7.jja.compat.curios.JjaCuriosCompat;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosManagedItemRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class CuriosRendererRegistration {
    private CuriosRendererRegistration() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            if (!JjaCuriosCompat.isCuriosLoaded()) {
                return;
            }
            CuriosManagedItemRegistry.forEachManagedItem(item ->
                JjaCuriosClientCompat.registerRenderer(item, CuriosArmorRenderService::renderManagedArmor)
            );
        });
    }
}
