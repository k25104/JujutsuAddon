package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.JujutsuAddon;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = JujutsuAddon.MODID)
public final class DomainDefenseAttributeEvents {
    private DomainDefenseAttributeEvents() {
    }

    @SubscribeEvent
    public static void onMobEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null || !isRelevant(effectInstance.getEffect())) {
            return;
        }

        DomainDefenseAttributeService.sync(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMobEffectRemove(MobEffectEvent.Remove event) {
        if (!isRelevant(event.getEffect())) {
            return;
        }

        DomainDefenseAttributeService.syncAfterRemoval(event.getEntity(), event.getEffect());
    }

    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null || !isRelevant(effectInstance.getEffect())) {
            return;
        }

        DomainDefenseAttributeService.syncAfterRemoval(event.getEntity(), effectInstance.getEffect());
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity.level().isClientSide()) {
            return;
        }

        DomainDefenseAttributeService.sync(livingEntity);
    }

    private static boolean isRelevant(@Nullable MobEffect effect) {
        return effect == JujutsucraftModMobEffects.DOMAIN_EXPANSION.get() || effect == JujutsucraftModMobEffects.SIMPLE_DOMAIN.get();
    }
}
