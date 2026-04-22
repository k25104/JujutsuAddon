package com.arf8vhg7.jja.feature.equipment.curios;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public final class CuriosDisplayTextService {
    private static final String CURIOS_MODIFIER_PREFIX = "curios.modifiers.";
    private static final String CURIOS_SLOT_MODIFIER_PREFIX = "curios.modifiers.slots.";
    private static final String CURIOS_IDENTIFIER_PREFIX = "curios.identifier.";
    private static final String JJA_HEAD = "jja_head";
    private static final String JJA_CHEST = "jja_chest";
    private static final String JJA_LEGS = "jja_legs";

    private CuriosDisplayTextService() {
    }

    public static MutableComponent modifierHeader(String identifier) {
        return Component.translatable(modifierHeaderKey(identifier));
    }

    public static MutableComponent identifierText(String identifier) {
        return Component.translatableWithFallback(CURIOS_IDENTIFIER_PREFIX + identifier, identifierFallback(identifier));
    }

    public static String resolveModifierHeaderKey(String translationKey) {
        String identifier = modifierIdentifier(translationKey);
        if (identifier == null) {
            return translationKey;
        }
        return modifierHeaderKey(identifier);
    }

    public static Component normalizeComponent(Component component) {
        MutableComponent rebuilt = rebuildComponent(component);
        rebuilt.setStyle(component.getStyle());
        for (Component sibling : component.getSiblings()) {
            rebuilt.append(normalizeComponent(sibling));
        }
        return rebuilt;
    }

    private static MutableComponent rebuildComponent(Component component) {
        if (!(component.getContents() instanceof TranslatableContents contents)) {
            return MutableComponent.create(component.getContents());
        }

        String key = contents.getKey();
        String modifierIdentifier = modifierIdentifier(key);
        if (modifierIdentifier != null) {
            return modifierHeader(modifierIdentifier);
        }

        String slotIdentifier = slotIdentifier(key);
        if (slotIdentifier != null) {
            return identifierText(slotIdentifier);
        }

        Object[] originalArgs = contents.getArgs();
        if (originalArgs == null || originalArgs.length == 0) {
            return Component.translatable(key);
        }

        Object[] normalizedArgs = new Object[originalArgs.length];
        for (int index = 0; index < originalArgs.length; index++) {
            Object arg = originalArgs[index];
            normalizedArgs[index] = arg instanceof Component nested ? normalizeComponent(nested) : arg;
        }
        return Component.translatable(key, normalizedArgs);
    }

    @Nullable
    private static String modifierIdentifier(String key) {
        if (!key.startsWith(CURIOS_MODIFIER_PREFIX) || key.startsWith(CURIOS_SLOT_MODIFIER_PREFIX)) {
            return null;
        }
        return key.substring(CURIOS_MODIFIER_PREFIX.length());
    }

    @Nullable
    private static String slotIdentifier(String key) {
        return key.startsWith(CURIOS_IDENTIFIER_PREFIX) ? key.substring(CURIOS_IDENTIFIER_PREFIX.length()) : null;
    }

    private static String titleCaseIdentifier(String identifier) {
        if (identifier.isEmpty()) {
            return identifier;
        }
        return Character.toUpperCase(identifier.charAt(0)) + identifier.substring(1).toLowerCase(Locale.ROOT);
    }

    private static String modifierHeaderKey(String identifier) {
        return switch (identifier) {
            case JJA_HEAD, "head" -> "item.modifiers.head";
            case JJA_CHEST, "body", "chest" -> "item.modifiers.chest";
            case JJA_LEGS, "legs" -> "item.modifiers.legs";
            default -> CURIOS_MODIFIER_PREFIX + identifier;
        };
    }

    private static String identifierFallback(String identifier) {
        return switch (identifier) {
            case JJA_HEAD, "head" -> "Head";
            case JJA_CHEST, "body", "chest" -> "Body";
            case JJA_LEGS, "legs" -> "Legs";
            default -> titleCaseIdentifier(identifier);
        };
    }
}
