package com.arf8vhg7.jja.feature.jja.technique.family.mahoraga;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.NueMountedControlService;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class UntamedMahoragaRitualEvents {
    private UntamedMahoragaRitualEvents() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        UntamedMahoragaRitualService.initializeParticipants(event.getEntity());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        UntamedMahoragaRitualService.handleParticipantDeath(event.getEntity());
        NueMountedControlService.clearRuntimeState(event.getEntity());
    }
}
