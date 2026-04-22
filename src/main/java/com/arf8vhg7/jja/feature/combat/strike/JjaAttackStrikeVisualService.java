package com.arf8vhg7.jja.feature.combat.strike;

import java.util.function.DoubleSupplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public final class JjaAttackStrikeVisualService {
    public static final JjaAttackStrikeJitterSampler UPSTREAM_JITTER_SAMPLER = (min, max) -> Mth.nextDouble(RandomSource.create(), min, max);

    private JjaAttackStrikeVisualService() {
    }

    public static JjaAttackStrikeParticleDescriptor createWeakDescriptor(
        double x,
        double y,
        double z,
        double range,
        float baseYaw,
        float basePitch,
        double cnt4,
        boolean combo,
        DoubleSupplier skippedSpawnYawRoll,
        JjaAttackStrikeJitterSampler jitterSampler
    ) {
        skippedSpawnYawRoll.getAsDouble();
        float yaw = baseYaw;
        float pitch = basePitch;
        if (combo) {
            yaw += (float) jitterSampler.sample(-22.5D, 22.5D);
            pitch += (float) jitterSampler.sample(-11.25D, 11.25D);
        }
        return new JjaAttackStrikeParticleDescriptor(
            x,
            y,
            z,
            JjaAttackStrikeAnimation.fromCnt4(cnt4, combo),
            (float) (2.0D * range),
            yaw,
            pitch
        );
    }

    public static JjaAttackStrikeParticleDescriptor createStrongDescriptor(
        double x,
        double y,
        double z,
        double range,
        float baseYaw,
        float basePitch,
        DoubleSupplier skippedSpawnYawRoll
    ) {
        skippedSpawnYawRoll.getAsDouble();
        return new JjaAttackStrikeParticleDescriptor(
            x,
            y,
            z,
            JjaAttackStrikeAnimation.IDLE3,
            (float) (2.0D * range),
            baseYaw,
            basePitch
        );
    }

    public static void spawn(ServerLevel level, JjaAttackStrikeParticleDescriptor descriptor) {
        level.sendParticles(
            descriptor.asParticleOptions(),
            descriptor.x(),
            descriptor.y(),
            descriptor.z(),
            0,
            0.0D,
            0.0D,
            0.0D,
            0.0D
        );
    }

    public static void preserveOwnerRangedIdentity(Entity entity) {
        if (entity.getPersistentData().getDouble("NameRanged") == 0.0D) {
            entity.getPersistentData().putDouble("NameRanged", Math.random());
        }
        if (entity.getPersistentData().getDouble("friend_num") == 0.0D) {
            entity.getPersistentData().putDouble("friend_num", Math.random());
        }
    }
}
