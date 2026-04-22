package com.arf8vhg7.jja.hook.curios.client;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosDisplayTextService;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;

public final class CuriosTooltipHook {
    private CuriosTooltipHook() {
    }

    public static String resolveModifierHeaderKey(String translationKey) {
        return CuriosDisplayTextService.resolveModifierHeaderKey(translationKey);
    }

    public static List<Component> normalizeTooltip(List<Component> tooltip) {
        List<Component> normalized = new ArrayList<>(tooltip.size());
        for (Component component : tooltip) {
            normalized.add(CuriosDisplayTextService.normalizeComponent(component));
        }
        return normalized;
    }
}