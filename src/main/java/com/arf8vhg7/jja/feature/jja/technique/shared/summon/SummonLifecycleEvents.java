package com.arf8vhg7.jja.feature.jja.technique.shared.summon;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class SummonLifecycleEvents {
    private SummonLifecycleEvents() {
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        SummonLifecycleService.tick(livingEntity);
    }
}
