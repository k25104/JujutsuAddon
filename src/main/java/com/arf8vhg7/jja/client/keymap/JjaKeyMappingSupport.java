package com.arf8vhg7.jja.client.keymap;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

public final class JjaKeyMappingSupport {
    public static final String CATEGORY = "key.categories.jja";

    private JjaKeyMappingSupport() {
    }

    public static boolean canProcessInput() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null && minecraft.level != null && minecraft.screen == null;
    }

    public static void registerAll(RegisterKeyMappingsEvent event, KeyMapping... keyMappings) {
        for (KeyMapping keyMapping : keyMappings) {
            event.register(keyMapping);
        }
    }
}
