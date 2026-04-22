package com.arf8vhg7.jja.feature.jja.technique.shared.menu.network;

import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateLifecycleService;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupCategory;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupInputSlot;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupService;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupViewState;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import com.arf8vhg7.jja.network.JjaPacketSenders;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaTechniqueSetupCycleMessage {
    private final TechniqueSetupCategory category;
    private final TechniqueSetupInputSlot slot;

    public JjaTechniqueSetupCycleMessage(TechniqueSetupCategory category, TechniqueSetupInputSlot slot) {
        this.category = category;
        this.slot = slot;
    }

    public JjaTechniqueSetupCycleMessage(FriendlyByteBuf buffer) {
        this.category = TechniqueSetupCategory.fromId(buffer.readVarInt());
        this.slot = TechniqueSetupInputSlot.fromId(buffer.readVarInt());
    }

    public static void encode(JjaTechniqueSetupCycleMessage message, FriendlyByteBuf buffer) {
        buffer.writeVarInt(message.category.id());
        buffer.writeVarInt(message.slot.id());
    }

    public static void handle(JjaTechniqueSetupCycleMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(contextSupplier, player -> {
            JjaPlayerStateLifecycleService.runTechniqueSetupMutation(
                player,
                () -> TechniqueSetupService.cycle(player, message.category, message.slot)
            );
            TechniqueSetupViewState viewState = TechniqueSetupService.buildViewState(player);
            JjaPacketSenders.sendToPlayer(player, new JjaTechniqueSetupStateMessage(false, viewState));
        });
    }
}
