package com.arf8vhg7.jja.feature.jja.technique.shared.menu.network;

import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSync;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerSkillState;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupService;
import com.arf8vhg7.jja.network.JjaPacketSenders;
import com.arf8vhg7.jja.network.JjaPacketHandlers;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public final class JjaTechniqueMenuRegistrationModeMessage {
    private final boolean enabled;

    public JjaTechniqueMenuRegistrationModeMessage(boolean enabled) {
        this.enabled = enabled;
    }

    public JjaTechniqueMenuRegistrationModeMessage(FriendlyByteBuf buffer) {
        this.enabled = buffer.readBoolean();
    }

    public static void encode(JjaTechniqueMenuRegistrationModeMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.enabled);
    }

    public static void handle(JjaTechniqueMenuRegistrationModeMessage message, Supplier<Context> contextSupplier) {
        JjaPacketHandlers.handleServer(contextSupplier, player -> {
            PlayerSkillState skillState = PlayerStateAccess.skill(player);
            if (skillState != null) {
                skillState.setPressSkillRegistrationToggle(message.enabled);
                JjaPlayerStateSync.sync(player);
                if (message.enabled) {
                    JjaPacketSenders.sendToPlayer(
                        player,
                        new JjaTechniqueSetupStateMessage(false, TechniqueSetupService.buildViewState(player))
                    );
                }
            }
        });
    }
}