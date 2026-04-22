package com.arf8vhg7.jja.feature.jja.technique.shared.display;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

public final class JjaTechniqueNameKeyResolver {
    private JjaTechniqueNameKeyResolver() {
    }

    public static String jjaGetKeyOrString(Component component) {
        if (component == null) {
            return "";
        }
        if (component.getContents() instanceof TranslatableContents translatable) {
            return translatable.getKey();
        }
        return component.getString();
    }

    public static String jjaGetTechniqueKeyOrString(Component component) {
        if (component == null) {
            return "";
        }
        if (component.getContents() instanceof TranslatableContents translatable) {
            String key = translatable.getKey();
            if (key != null && key.startsWith("jujutsu.technique.")) {
                return key;
            }
        }
        return component.getString();
    }

    public static String jjaTranslateTechniqueName(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        return Component.translatable(key).getString();
    }
}
