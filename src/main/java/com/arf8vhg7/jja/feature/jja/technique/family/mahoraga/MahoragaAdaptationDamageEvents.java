package com.arf8vhg7.jja.feature.jja.technique.family.mahoraga;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class MahoragaAdaptationDamageEvents {
    private MahoragaAdaptationDamageEvents() {
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        MahoragaAdaptation.recordPendingDamage(event.getEntity(), event.getSource(), event.getAmount());
    }
}
