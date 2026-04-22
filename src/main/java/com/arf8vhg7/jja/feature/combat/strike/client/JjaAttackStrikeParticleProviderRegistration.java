package com.arf8vhg7.jja.feature.combat.strike.client;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.feature.combat.strike.JjaAttackStrikeParticleOptions;
import com.arf8vhg7.jja.feature.combat.strike.JjaAttackStrikeParticles;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = JujutsuAddon.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class JjaAttackStrikeParticleProviderRegistration {
    private JjaAttackStrikeParticleProviderRegistration() {
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpecial(
            (ParticleType<JjaAttackStrikeParticleOptions>) JjaAttackStrikeParticles.ATTACK_STRIKE_MATCH.get(),
            JjaAttackStrikeMatchParticle::new
        );
    }
}
