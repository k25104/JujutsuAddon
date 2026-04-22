package com.arf8vhg7.jja.feature.jja.rct.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.client.keymap.JjaEdgeTriggeredKeyMapping;
import com.arf8vhg7.jja.client.keymap.JjaKeyMappingSupport;
import com.arf8vhg7.jja.client.keymap.JjaPacketKeyMapping;
import com.arf8vhg7.jja.feature.jja.rct.network.JjaRctToggleMessage;
import com.arf8vhg7.jja.feature.jja.rct.network.JjaRctToggleMessage.ToggleType;
import net.minecraft.client.KeyMapping;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class RctKeyMappings {
    public static final KeyMapping KEY_RCT_OUTPUT_TOGGLE =
        new JjaPacketKeyMapping("key.jja.rct_output_toggle", () -> new JjaRctToggleMessage(ToggleType.OUTPUT));
    public static final KeyMapping KEY_BRAIN_DESTRUCTION = new BrainDestructionKeyMapping();
    public static final KeyMapping KEY_BRAIN_REGENERATION_TOGGLE = new JjaPacketKeyMapping(
        "key.jja.brain_regeneration_toggle",
        () -> new JjaRctToggleMessage(ToggleType.BRAIN_REGENERATION)
    );
    public static final KeyMapping KEY_AUTO_RCT_TOGGLE =
        new JjaPacketKeyMapping("key.jja.auto_rct_toggle", () -> new JjaRctToggleMessage(ToggleType.AUTO));

    private RctKeyMappings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        List<KeyMapping> keyMappings = new ArrayList<>();
        if (JjaCommonConfig.RCT_OUTPUT_ENABLED.get()) {
            keyMappings.add(KEY_RCT_OUTPUT_TOGGLE);
        }
        if (JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get()) {
            keyMappings.add(KEY_BRAIN_DESTRUCTION);
        }
        if (JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get()) {
            keyMappings.add(KEY_BRAIN_REGENERATION_TOGGLE);
        }
        if (JjaCommonConfig.AUTO_RCT_ENABLED.get()) {
            keyMappings.add(KEY_AUTO_RCT_TOGGLE);
        }
        JjaKeyMappingSupport.registerAll(event, keyMappings.toArray(KeyMapping[]::new));
    }

    private static final class BrainDestructionKeyMapping extends JjaEdgeTriggeredKeyMapping {
        private BrainDestructionKeyMapping() {
            super("key.jja.brain_destruction");
        }

        @Override
        protected void onEdgeChange(boolean isDown) {
            if (!JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get()) {
                if (!isDown) {
                    RctClientEvents.stopBrainDestructionHold();
                }
                return;
            }
            if (isDown && JjaKeyMappingSupport.canProcessInput()) {
                RctClientEvents.startBrainDestructionHold();
            } else if (!isDown) {
                RctClientEvents.stopBrainDestructionHold();
            }
        }
    }
}
