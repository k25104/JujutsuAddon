package com.arf8vhg7.jja.feature.jja.resource.ce;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;

public final class CEParticleRemapService {
    private static final String PARTICLE_COMMAND_PREFIX = "particle ";

    private CEParticleRemapService() {
    }

    public static @Nullable String remapCursePowerParticleCommand(@Nullable String command) {
        if (command == null || !CEParticleContextService.hasContext()) {
            return command;
        }

        int firstSpace = command.indexOf(' ');
        if (firstSpace < 0 || !command.startsWith(PARTICLE_COMMAND_PREFIX)) {
            return command;
        }

        int secondSpace = command.indexOf(' ', firstSpace + 1);
        if (secondSpace < 0) {
            return command;
        }

        String particleName = command.substring(firstSpace + 1, secondSpace);
        if (CEColorName.fromCursePowerParticleName(particleName) == null) {
            return command;
        }

        CEColorName resolvedColor = resolveContextColor();
        if (resolvedColor == null) {
            return command;
        }
        return PARTICLE_COMMAND_PREFIX + resolvedColor.cursePowerParticleName() + command.substring(secondSpace);
    }

    public static ParticleOptions remapCursePowerParticle(ParticleOptions particle) {
        if (!CEParticleContextService.hasContext() || CEColorName.fromCursePowerParticle(particle) == null) {
            return particle;
        }

        CEColorName resolvedColor = resolveContextColor();
        return resolvedColor == null ? particle : resolvedColor.cursePowerParticle();
    }

    private static @Nullable CEColorName resolveContextColor() {
        return CEColorService.resolveCurrentColorName(CEParticleContextService.currentResolvedOwner());
    }
}
