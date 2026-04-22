package com.arf8vhg7.jja.feature.combat.strike;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record JjaAttackStrikeParticleOptions(
    JjaAttackStrikeAnimation animation,
    float renderScale,
    float yawDegrees,
    float pitchDegrees
) implements ParticleOptions {
    public static final Codec<JjaAttackStrikeParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        JjaAttackStrikeAnimation.CODEC.fieldOf("animation").forGetter(JjaAttackStrikeParticleOptions::animation),
        Codec.FLOAT.fieldOf("render_scale").forGetter(JjaAttackStrikeParticleOptions::renderScale),
        Codec.FLOAT.fieldOf("yaw_deg").forGetter(JjaAttackStrikeParticleOptions::yawDegrees),
        Codec.FLOAT.fieldOf("pitch_deg").forGetter(JjaAttackStrikeParticleOptions::pitchDegrees)
    ).apply(instance, JjaAttackStrikeParticleOptions::new));

    public static final ParticleOptions.Deserializer<JjaAttackStrikeParticleOptions> DESERIALIZER =
        new ParticleOptions.Deserializer<>() {
            @Override
            public JjaAttackStrikeParticleOptions fromCommand(
                ParticleType<JjaAttackStrikeParticleOptions> particleType,
                StringReader reader
            ) throws CommandSyntaxException {
                JjaAttackStrikeAnimation animation = parseAnimation(reader.readString(), reader);
                reader.expect(' ');
                float renderScale = reader.readFloat();
                reader.expect(' ');
                float yawDegrees = reader.readFloat();
                reader.expect(' ');
                float pitchDegrees = reader.readFloat();
                return new JjaAttackStrikeParticleOptions(animation, renderScale, yawDegrees, pitchDegrees);
            }

            @Override
            public JjaAttackStrikeParticleOptions fromNetwork(
                ParticleType<JjaAttackStrikeParticleOptions> particleType,
                FriendlyByteBuf buffer
            ) {
                return new JjaAttackStrikeParticleOptions(
                    buffer.readEnum(JjaAttackStrikeAnimation.class),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat()
                );
            }
        };

    @Override
    public ParticleType<?> getType() {
        return JjaAttackStrikeParticles.ATTACK_STRIKE_MATCH.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.animation);
        buffer.writeFloat(this.renderScale);
        buffer.writeFloat(this.yawDegrees);
        buffer.writeFloat(this.pitchDegrees);
    }

    @Override
    public String writeToString() {
        ResourceLocation key = BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType());
        String particleName = key == null ? "jja:jja_attack_strike_match" : key.toString();
        return particleName
            + " "
            + this.animation.getSerializedName()
            + " "
            + this.renderScale
            + " "
            + this.yawDegrees
            + " "
            + this.pitchDegrees;
    }

    private static JjaAttackStrikeAnimation parseAnimation(String token, StringReader reader) throws CommandSyntaxException {
        JjaAttackStrikeAnimation animation = JjaAttackStrikeAnimation.CODEC.byName(token);
        if (animation == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
        }
        return animation;
    }
}
