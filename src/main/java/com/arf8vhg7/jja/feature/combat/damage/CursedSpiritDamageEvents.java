package com.arf8vhg7.jja.feature.combat.damage;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class CursedSpiritDamageEvents {
    private CursedSpiritDamageEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (CursedSpiritDamageRules.shouldCancelAttack(event.getEntity(), event.getSource())) {
            event.setCanceled(true);
        }
    }
}
