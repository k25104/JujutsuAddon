package com.arf8vhg7.jja.feature.jja.resource.ce;

import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

enum CEColorName {
    // Upstream ReturnEnergyColorProcedure uses 1=blue, 2=orange, 3=red, 4=green, 5=purple.
    BLUE(1, "blue", "jujutsucraft:particle_curse_power_blue", "jujutsucraft:particle_thunder_blue"),
    ORANGE(2, "orange", "jujutsucraft:particle_curse_power_orange", "jujutsucraft:particle_curse_power_orange"),
    RED(3, "red", "jujutsucraft:particle_curse_power_red", "jujutsucraft:particle_curse_power_red"),
    GREEN(4, "green", "jujutsucraft:particle_curse_power_green", "minecraft:happy_villager"),
    PURPLE(5, "purple", "jujutsucraft:particle_curse_power_purple", "jujutsucraft:particle_curse_power_purple");

    private final int id;
    private final String literal;
    private final String cursePowerParticleName;
    private final String sixEyesParticleName;

    CEColorName(int id, String literal, String cursePowerParticleName, String sixEyesParticleName) {
        this.id = id;
        this.literal = literal;
        this.cursePowerParticleName = cursePowerParticleName;
        this.sixEyesParticleName = sixEyesParticleName;
    }

    int id() {
        return this.id;
    }

    String literal() {
        return this.literal;
    }

    String cursePowerParticleName() {
        return this.cursePowerParticleName;
    }

    String sixEyesParticleName() {
        return this.sixEyesParticleName;
    }

    SimpleParticleType cursePowerParticle() {
        return switch (this) {
            case RED -> (SimpleParticleType) JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_RED.get();
            case ORANGE -> (SimpleParticleType) JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_ORANGE.get();
            case BLUE -> (SimpleParticleType) JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_BLUE.get();
            case GREEN -> (SimpleParticleType) JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_GREEN.get();
            case PURPLE -> (SimpleParticleType) JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_PURPLE.get();
        };
    }

    static @Nullable CEColorName fromId(double colorId) {
        return switch ((int) Math.round(colorId)) {
            case 1 -> BLUE;
            case 2 -> ORANGE;
            case 3 -> RED;
            case 4 -> GREEN;
            case 5 -> PURPLE;
            default -> null;
        };
    }

    static @Nullable CEColorName fromLiteral(String literal) {
        return switch (literal) {
            case "red" -> RED;
            case "orange" -> ORANGE;
            case "blue" -> BLUE;
            case "green" -> GREEN;
            case "purple" -> PURPLE;
            default -> null;
        };
    }

    static @Nullable CEColorName fromCursePowerParticleName(String particleName) {
        return switch (particleName) {
            case "jujutsucraft:particle_curse_power_red" -> RED;
            case "jujutsucraft:particle_curse_power_orange" -> ORANGE;
            case "jujutsucraft:particle_curse_power_blue" -> BLUE;
            case "jujutsucraft:particle_curse_power_green" -> GREEN;
            case "jujutsucraft:particle_curse_power_purple" -> PURPLE;
            default -> null;
        };
    }

    static @Nullable CEColorName fromCursePowerParticle(ParticleOptions particle) {
        if (particle == JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_RED.get()) {
            return RED;
        }
        if (particle == JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_ORANGE.get()) {
            return ORANGE;
        }
        if (particle == JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_BLUE.get()) {
            return BLUE;
        }
        if (particle == JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_GREEN.get()) {
            return GREEN;
        }
        if (particle == JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_PURPLE.get()) {
            return PURPLE;
        }
        return null;
    }

    static int randomColorId() {
        CEColorName[] values = values();
        return values[ThreadLocalRandom.current().nextInt(values.length)].id;
    }
}
