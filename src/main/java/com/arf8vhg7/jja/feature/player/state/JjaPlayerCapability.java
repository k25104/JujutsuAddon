package com.arf8vhg7.jja.feature.player.state;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD)
public final class JjaPlayerCapability {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("jja", "player_state");
    public static final Capability<JjaPlayerState> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private JjaPlayerCapability() {
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(JjaPlayerState.class);
    }

    public static JjaPlayerState get(Entity entity) {
        if (entity == null) {
            return null;
        }
        return entity.getCapability(CAPABILITY, null).resolve().orElse(null);
    }
}
