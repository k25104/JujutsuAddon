package com.arf8vhg7.jja.feature.combat.strike;

import com.arf8vhg7.jja.JujutsuAddon;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class JjaAttackStrikeParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(
        ForgeRegistries.PARTICLE_TYPES,
        JujutsuAddon.MODID
    );
    public static final RegistryObject<ParticleType<JjaAttackStrikeParticleOptions>> ATTACK_STRIKE_MATCH = PARTICLE_TYPES.register(
        "jja_attack_strike_match",
        () -> new JjaAttackStrikeParticleType(false)
    );

    private JjaAttackStrikeParticles() {
    }

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
