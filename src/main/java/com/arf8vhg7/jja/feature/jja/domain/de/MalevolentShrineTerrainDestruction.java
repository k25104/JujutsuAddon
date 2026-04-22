package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import net.mcreator.jujutsucraft.procedures.BlockDestroyAllDirectionProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class MalevolentShrineTerrainDestruction {
    private static final int RANDOM_TRIES = 512;
    private static final double DUST_AMOUNT_MAX = 200.0;
    private static final double BLOCK_RANGE = 16.0;
    private static final double BLOCK_DAMAGE = 99.0;

    private MalevolentShrineTerrainDestruction() {
    }

    public static void tryExtraAttempts(LevelAccessor world, Entity entity, double range, double xCenter, double yCenter, double zCenter) {
        if (!JjaCommonConfig.MALEVOLENT_SHRINE_TERRAIN_DESTRUCTION_SCALING.get()) {
            return;
        }

        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        MobEffectInstance strength = livingEntity.getEffect(MobEffects.DAMAGE_BOOST);
        if (strength == null) {
            return;
        }

        int extraAttempts = strength.getAmplifier();
        if (extraAttempts <= 0) {
            return;
        }

        for (int attempt = 0; attempt < extraAttempts; attempt++) {
            tryRandomDestruction(world, entity, range, xCenter, yCenter, zCenter);
        }
    }

    private static void tryRandomDestruction(LevelAccessor world, Entity entity, double range, double xCenter, double yCenter, double zCenter) {
        double countA = 0.0;
        double dis = 0.0;
        double xPos = 0.0;
        double yPos = 0.0;
        double zPos = 0.0;
        double num1 = 0.0;

        for (int index = 0; index < RANDOM_TRIES; index++) {
            countA = Math.toRadians(Math.random() * 360.0);
            dis = range * 0.5 * (Math.random() * 2.0 - 1.0);
            xPos = xCenter + Math.sin(countA) * dis;
            yPos = yCenter + Math.random() * range * 0.2;
            zPos = zCenter + Math.cos(countA) * dis;
            if (!world.isEmptyBlock(BlockPos.containing(xPos, yPos, zPos))) {
                double dustAmount = entity.getPersistentData().getDouble("dust_amount");
                entity.getPersistentData().putDouble("dust_amount", Math.min(dustAmount + 1.0, DUST_AMOUNT_MAX));
                if (world instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.EXPLOSION, xPos, yPos, zPos, 2, 1.5, 1.5, 1.5, 0.0);
                    level.sendParticles(ParticleTypes.CLOUD, xPos, yPos, zPos, 2, 1.5, 1.5, 1.5, 0.5);
                }

                entity.getPersistentData().putBoolean("noParticle", true);
                entity.getPersistentData().putBoolean("ExtinctionBlock", true);
                entity.getPersistentData().putDouble("BlockRange", BLOCK_RANGE);
                entity.getPersistentData().putDouble("BlockDamage", BLOCK_DAMAGE);
                BlockDestroyAllDirectionProcedure.execute(world, xPos, Math.max(yPos, yCenter + 8.0), zPos, entity);
                if (num1 < 32.0 || num1 / RANDOM_TRIES < Math.random()) {
                    break;
                }
            }

            num1++;
        }
    }
}
