package com.arf8vhg7.jja.hook.curios.client;

import com.arf8vhg7.jja.feature.equipment.curios.CuriosDisplayTextService;
import java.util.List;
import net.minecraft.network.chat.Component;

public final class ClientEventHandlerHook {
    private ClientEventHandlerHook() {
    }

    public static void normalizeTooltip(List<Component> tooltip) {
        for (int index = 0; index < tooltip.size(); index++) {
            tooltip.set(index, CuriosDisplayTextService.normalizeComponent(tooltip.get(index)));
        }
    }
}