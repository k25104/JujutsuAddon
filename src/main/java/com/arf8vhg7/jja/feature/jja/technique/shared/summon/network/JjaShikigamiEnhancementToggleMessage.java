package com.arf8vhg7.jja.feature.jja.technique.shared.summon.network;

import com.arf8vhg7.jja.network.JjaPacketCodecs;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import com.arf8vhg7.jja.feature.jja.technique.shared.summon.SummonEnhancementService;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaShikigamiEnhancementToggleMessage {
    public JjaShikigamiEnhancementToggleMessage() {
    }

    public JjaShikigamiEnhancementToggleMessage(FriendlyByteBuf buffer) {
        JjaPacketCodecs.decodeEmpty(buffer);
    }

    public static void encode(JjaShikigamiEnhancementToggleMessage message, FriendlyByteBuf buffer) {
        JjaPacketCodecs.encodeEmpty(buffer);
    }

    public static void handle(JjaShikigamiEnhancementToggleMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(contextSupplier, player -> {
            if (!SummonEnhancementService.canToggleForActiveCt(player)) {
                player.displayClientMessage(Component.translatable("jujutsu.message.dont_use"), false);
                return;
            }
            boolean enabled = SummonEnhancementService.toggle(player);
            player.displayClientMessage(
                Component.translatable("key.jja.shikigami_enhancement")
                    .append(Component.literal(": "))
                    .append(Component.translatable(enabled ? "options.on" : "options.off")),
                false
            );
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
        });
    }
}
