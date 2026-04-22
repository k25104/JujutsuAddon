package com.arf8vhg7.jja.feature.combat.strike;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public final class JjaAttackStrikeParticleType extends ParticleType<JjaAttackStrikeParticleOptions> {
    public JjaAttackStrikeParticleType(boolean overrideLimiter) {
        super(overrideLimiter, JjaAttackStrikeParticleOptions.DESERIALIZER);
    }

    @Override
    public Codec<JjaAttackStrikeParticleOptions> codec() {
        return JjaAttackStrikeParticleOptions.CODEC;
    }
}
