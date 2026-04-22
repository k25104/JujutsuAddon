package com.arf8vhg7.jja.feature.jja.technique.shared.summon.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.client.keymap.JjaKeyMappingSupport;
import com.arf8vhg7.jja.client.keymap.JjaPacketKeyMapping;
import com.arf8vhg7.jja.feature.jja.technique.shared.summon.network.JjaShikigamiEnhancementToggleMessage;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class ShikigamiEnhancementKeyMappings {
    public static final KeyMapping KEY_SHIKIGAMI_ENHANCEMENT = new JjaPacketKeyMapping(
        "key.jja.shikigami_enhancement",
        JjaShikigamiEnhancementToggleMessage::new
    );

    private ShikigamiEnhancementKeyMappings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        JjaKeyMappingSupport.registerAll(event, KEY_SHIKIGAMI_ENHANCEMENT);
    }
}
