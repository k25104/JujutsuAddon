package com.arf8vhg7.jja.feature.jja.domain.de.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.client.keymap.JjaEdgeTriggeredKeyMapping;
import com.arf8vhg7.jja.client.keymap.JjaKeyMappingSupport;
import com.arf8vhg7.jja.feature.jja.domain.de.network.JjaDomainRadiusAdjustMessage;
import com.arf8vhg7.jja.feature.jja.domain.de.network.JjaDomainRadiusAdjustMessage.Direction;
import com.arf8vhg7.jja.network.JjaNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class DomainExpansionRadiusKeyMappings {
    public static final KeyMapping KEY_EXPAND_DOMAIN = new DomainRadiusKeyMapping(
        "key.jja.expand_domain",
        GLFW.GLFW_KEY_EQUAL,
        Direction.EXPAND
    );
    public static final KeyMapping KEY_SHRINK_DOMAIN = new DomainRadiusKeyMapping(
        "key.jja.shrink_domain",
        GLFW.GLFW_KEY_MINUS,
        Direction.SHRINK
    );

    private DomainExpansionRadiusKeyMappings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        JjaKeyMappingSupport.registerAll(event, KEY_EXPAND_DOMAIN, KEY_SHRINK_DOMAIN);
    }

    private static final class DomainRadiusKeyMapping extends JjaEdgeTriggeredKeyMapping {
        private final Direction direction;

        private DomainRadiusKeyMapping(String translationKey, int defaultKey, Direction direction) {
            super(translationKey, defaultKey);
            this.direction = direction;
        }

        @Override
        protected void onEdgeChange(boolean isDown) {
            if (isDown && JjaKeyMappingSupport.canProcessInput()) {
                JjaNetwork.CHANNEL.sendToServer(new JjaDomainRadiusAdjustMessage(this.direction));
            }
        }
    }
}
