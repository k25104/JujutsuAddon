package com.arf8vhg7.jja.feature.jja.technique.shared.menu.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.client.keymap.JjaKeyMappingSupport;
import com.arf8vhg7.jja.client.keymap.JjaPacketKeyMapping;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.network.JjaTechniqueSetupOpenMessage;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class TechniqueMenuKeyMappings {
    public static final KeyMapping KEY_TECHNIQUE_MENU = new JjaPacketKeyMapping(
        "key.jja.technique_menu",
        JjaTechniqueSetupOpenMessage::new
    );

    private TechniqueMenuKeyMappings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        JjaKeyMappingSupport.registerAll(event, KEY_TECHNIQUE_MENU);
    }
}