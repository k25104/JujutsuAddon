package com.arf8vhg7.jja.hook.minecraft;

import com.arf8vhg7.jja.feature.jja.resource.ce.CEParticleRemapService;
import net.minecraft.core.particles.ParticleOptions;

public final class ServerLevelHook {
    private ServerLevelHook() {
    }

    public static ParticleOptions remapCeParticle(ParticleOptions particle) {
        return CEParticleRemapService.remapCursePowerParticle(particle);
    }
}
