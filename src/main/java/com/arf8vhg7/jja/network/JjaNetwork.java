package com.arf8vhg7.jja.network;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.jja.domain.de.network.JjaDomainRadiusAdjustMessage;
import com.arf8vhg7.jja.feature.jja.domain.de.curtain.network.JjaCurtainVisualStateMessage;
import com.arf8vhg7.jja.feature.player.progression.fame.network.JjaFameGainClientMessage;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.network.JjaTwinnedBodyStateMessage;
import com.arf8vhg7.jja.feature.player.state.network.JjaPlayerStateSyncMessage;
import com.arf8vhg7.jja.feature.jja.rct.network.JjaBrainDestructionHoldMessage;
import com.arf8vhg7.jja.feature.jja.rct.network.JjaBrainDestructionHoldStateMessage;
import com.arf8vhg7.jja.feature.jja.rct.network.JjaRctToggleMessage;
import com.arf8vhg7.jja.feature.player.revive.network.JjaReviveAssistHudMessage;
import com.arf8vhg7.jja.feature.player.revive.network.JjaReviveCoreClickMessage;
import com.arf8vhg7.jja.feature.player.revive.network.JjaReviveGiveUpMessage;
import com.arf8vhg7.jja.feature.player.revive.network.JjaReviveHoldMessage;
import com.arf8vhg7.jja.feature.player.revive.network.JjaReviveStateMessage;
import com.arf8vhg7.jja.feature.jja.technique.shared.summon.network.JjaShikigamiEnhancementToggleMessage;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.network.JjaTechniqueMenuRegisterMessage;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.network.JjaTechniqueMenuRegistrationModeMessage;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.network.JjaTechniqueSetupCycleMessage;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.network.JjaTechniqueSetupOpenMessage;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.network.JjaTechniqueSetupStateMessage;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD)
public final class JjaNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(JujutsuAddon.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    private static final List<JjaPacketSpec<?>> PACKETS = List.of(
        new JjaPacketSpec<>(
            JjaRctToggleMessage.class,
            JjaRctToggleMessage::encode,
            JjaRctToggleMessage::new,
            JjaRctToggleMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaShikigamiEnhancementToggleMessage.class,
            JjaShikigamiEnhancementToggleMessage::encode,
            JjaShikigamiEnhancementToggleMessage::new,
            JjaShikigamiEnhancementToggleMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaTechniqueSetupOpenMessage.class,
            JjaTechniqueSetupOpenMessage::encode,
            JjaTechniqueSetupOpenMessage::new,
            JjaTechniqueSetupOpenMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaTechniqueMenuRegistrationModeMessage.class,
            JjaTechniqueMenuRegistrationModeMessage::encode,
            JjaTechniqueMenuRegistrationModeMessage::new,
            JjaTechniqueMenuRegistrationModeMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaTechniqueSetupCycleMessage.class,
            JjaTechniqueSetupCycleMessage::encode,
            JjaTechniqueSetupCycleMessage::new,
            JjaTechniqueSetupCycleMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaTechniqueMenuRegisterMessage.class,
            JjaTechniqueMenuRegisterMessage::encode,
            JjaTechniqueMenuRegisterMessage::new,
            JjaTechniqueMenuRegisterMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaTechniqueSetupStateMessage.class,
            JjaTechniqueSetupStateMessage::encode,
            JjaTechniqueSetupStateMessage::new,
            JjaTechniqueSetupStateMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaBrainDestructionHoldMessage.class,
            JjaBrainDestructionHoldMessage::encode,
            JjaBrainDestructionHoldMessage::new,
            JjaBrainDestructionHoldMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaBrainDestructionHoldStateMessage.class,
            JjaBrainDestructionHoldStateMessage::encode,
            JjaBrainDestructionHoldStateMessage::new,
            JjaBrainDestructionHoldStateMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaPlayerStateSyncMessage.class,
            JjaPlayerStateSyncMessage::encode,
            JjaPlayerStateSyncMessage::new,
            JjaPlayerStateSyncMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaCurtainVisualStateMessage.class,
            JjaCurtainVisualStateMessage::encode,
            JjaCurtainVisualStateMessage::new,
            JjaCurtainVisualStateMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaDomainRadiusAdjustMessage.class,
            JjaDomainRadiusAdjustMessage::encode,
            JjaDomainRadiusAdjustMessage::new,
            JjaDomainRadiusAdjustMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaReviveAssistHudMessage.class,
            JjaReviveAssistHudMessage::encode,
            JjaReviveAssistHudMessage::new,
            JjaReviveAssistHudMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaReviveStateMessage.class,
            JjaReviveStateMessage::encode,
            JjaReviveStateMessage::new,
            JjaReviveStateMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaReviveHoldMessage.class,
            JjaReviveHoldMessage::encode,
            JjaReviveHoldMessage::new,
            JjaReviveHoldMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaReviveGiveUpMessage.class,
            JjaReviveGiveUpMessage::encode,
            JjaReviveGiveUpMessage::new,
            JjaReviveGiveUpMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaReviveCoreClickMessage.class,
            JjaReviveCoreClickMessage::encode,
            JjaReviveCoreClickMessage::new,
            JjaReviveCoreClickMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaFameGainClientMessage.class,
            JjaFameGainClientMessage::encode,
            JjaFameGainClientMessage::new,
            JjaFameGainClientMessage::handle
        ),
        new JjaPacketSpec<>(
            JjaTwinnedBodyStateMessage.class,
            JjaTwinnedBodyStateMessage::encode,
            JjaTwinnedBodyStateMessage::new,
            JjaTwinnedBodyStateMessage::handle
        )
    );
    private static int nextMessageId = 0;

    private JjaNetwork() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        for (JjaPacketSpec<?> packet : PACKETS) {
            register(packet);
        }
    }

    private static <T> void register(JjaPacketSpec<T> packet) {
        CHANNEL.registerMessage(nextMessageId++, packet.messageType(), packet.encoder(), packet.decoder(), packet.handler());
    }
}
