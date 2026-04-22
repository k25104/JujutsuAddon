package com.arf8vhg7.jja.feature.world.spawn;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class JjaStrongestPlayerScalingEvents {
    private JjaStrongestPlayerScalingEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        WeakestPlayerScaling.refresh(event.getEntity().getServer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Entity entity = event.getEntity();
        WeakestPlayerScaling.refresh(entity.getServer(), entity.getUUID());
    }
}
