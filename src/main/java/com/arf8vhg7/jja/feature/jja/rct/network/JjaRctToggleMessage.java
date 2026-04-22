package com.arf8vhg7.jja.feature.jja.rct.network;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import com.arf8vhg7.jja.feature.jja.rct.RctStateService;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaRctToggleMessage {
    private final int toggleType;

    public JjaRctToggleMessage(ToggleType toggleType) {
        this.toggleType = toggleType.ordinal();
    }

    public JjaRctToggleMessage(FriendlyByteBuf buffer) {
        this.toggleType = buffer.readInt();
    }

    public static void encode(JjaRctToggleMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.toggleType);
    }

    public static void handle(JjaRctToggleMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(contextSupplier, player -> {
            ToggleType toggleType = ToggleType.fromOrdinal(message.toggleType);
            if (toggleType == null) {
                return;
            }
            boolean enabled = switch (toggleType) {
                case OUTPUT -> JjaCommonConfig.RCT_OUTPUT_ENABLED.get();
                case BRAIN_REGENERATION -> JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get();
                case AUTO -> JjaCommonConfig.AUTO_RCT_ENABLED.get();
            };
            if (!enabled) {
                return;
            }
            boolean changed = false;
            switch (toggleType) {
                case OUTPUT -> changed = RctStateService.toggleOutput(player);
                case BRAIN_REGENERATION -> changed = RctStateService.toggleBrainRegeneration(player);
                case AUTO -> changed = RctStateService.toggleAutoRct(player);
            }
            if (changed) {
                boolean currentEnabled = switch (toggleType) {
                    case OUTPUT -> RctStateService.isOutputEnabled(player);
                    case BRAIN_REGENERATION -> RctStateService.isBrainRegenerationEnabled(player);
                    case AUTO -> RctStateService.isAutoRctEnabled(player);
                };
                player.displayClientMessage(
                    Component.translatable(toggleType.translationKey()).append(Component.literal(": " + Boolean.toString(currentEnabled))),
                    false
                );
                player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
            }
        });
    }

    public enum ToggleType {
        OUTPUT,
        BRAIN_REGENERATION,
        AUTO;

        private String translationKey() {
            return switch (this) {
                case OUTPUT -> "advancements.mastery_rct_output.title";
                case BRAIN_REGENERATION -> "advancements.mastery_rct_brain_regeneration.title";
                case AUTO -> "advancements.mastery_rct_auto.title";
            };
        }

        private static ToggleType fromOrdinal(int ordinal) {
            ToggleType[] values = values();
            return ordinal >= 0 && ordinal < values.length ? values[ordinal] : null;
        }
    }
}
