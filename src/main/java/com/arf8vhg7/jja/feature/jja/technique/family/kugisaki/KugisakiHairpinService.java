package com.arf8vhg7.jja.feature.jja.technique.family.kugisaki;

import com.arf8vhg7.jja.feature.jja.resource.ce.CEParticleContextService;
import net.mcreator.jujutsucraft.init.JujutsucraftModParticleTypes;
import net.mcreator.jujutsucraft.procedures.BlockDestroyAllDirectionProcedure;
import net.mcreator.jujutsucraft.procedures.LogicAttackProcedure;
import net.mcreator.jujutsucraft.procedures.RangeAttackProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.LevelAccessor;

public final class KugisakiHairpinService {
    private static final int ACTIVATION_DELAY_TICKS = 5;
    private static final int MAX_ACTIVE_TICKS = 15;
    private static final double TRAIL_STEP = 0.25D;
    private static final double TRAIL_LENGTH_STEPS = 16.0D;
    private static final double TRAIL_PARTICLE_SPACING = 4.0D;
    private static final double SEARCH_FALLBACK_RADIUS = 32.0D;
    private static final double WORLD_SEARCH_RADIUS = 30_000_000.0D;

    private KugisakiHairpinService() {
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        CEParticleContextService.enter(entity);
        try {
            executeInternal(world, x, y, z, entity);
        } finally {
            CEParticleContextService.exit();
        }
    }

    private static void executeInternal(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) {
            return;
        }

        entity.getPersistentData().putDouble("cnt1", entity.getPersistentData().getDouble("cnt1") + 1.0D);
        if (entity.getPersistentData().getDouble("cnt1") == 1.0D) {
            playActivationCue(world, x, y, z, entity);
        }

        if (entity.getPersistentData().getDouble("cnt1") > ACTIVATION_DELAY_TICKS) {
            DetonationCandidate candidate = findDetonationCandidate(world, x, y, z, entity);
            if (candidate != null) {
                detonate(world, x, y, z, entity, candidate);
            } else {
                entity.getPersistentData().putDouble("skill", 0.0D);
            }
        }

        if (entity.getPersistentData().getDouble("cnt1") > MAX_ACTIVE_TICKS) {
            entity.getPersistentData().putDouble("skill", 0.0D);
        }
    }

    private static void playActivationCue(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (isEmptyMainHand(entity)) {
            playSound(world, x, y, z, SoundEvents.FIREWORK_ROCKET_BLAST, 1.0F, 1.22F);
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.swing(InteractionHand.MAIN_HAND, true);
            }
            return;
        }

        if (isEmptyOffHand(entity)) {
            playSound(world, x, y, z, SoundEvents.FIREWORK_ROCKET_BLAST, 1.0F, 1.22F);
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.swing(InteractionHand.OFF_HAND, true);
            }
        }
    }

    private static boolean isEmptyMainHand(Entity entity) {
        return (entity instanceof LivingEntity livingEntity ? livingEntity.getMainHandItem() : ItemStack.EMPTY).isEmpty();
    }

    private static boolean isEmptyOffHand(Entity entity) {
        return (entity instanceof LivingEntity livingEntity ? livingEntity.getOffhandItem() : ItemStack.EMPTY).isEmpty();
    }

    private static DetonationCandidate findDetonationCandidate(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (world instanceof ServerLevel level) {
            DetonationCandidate embeddedTarget = null;
            for (Entity candidate : level.getAllEntities()) {
                if (candidate == entity || candidate.getPersistentData().getDouble("Nail") <= 0.0D) {
                    continue;
                }

                if (candidate instanceof Projectile projectile && projectile.getDeltaMovement().lengthSqr() > 0.0D) {
                    return DetonationCandidate.projectile(candidate);
                }

                if (embeddedTarget == null && LogicAttackProcedure.execute(world, entity, candidate)) {
                    embeddedTarget = DetonationCandidate.embedded(candidate);
                }
            }
            return embeddedTarget;
        }

        double radius = world instanceof Level ? WORLD_SEARCH_RADIUS : SEARCH_FALLBACK_RADIUS;
        for (Entity candidate : world.getEntitiesOfClass(
            Entity.class,
            new net.minecraft.world.phys.AABB(x, y, z, x, y, z).inflate(radius),
            ignored -> true
        )) {
            if (candidate != entity && candidate.getPersistentData().getDouble("Nail") > 0.0D) {
                if (candidate instanceof Projectile projectile && projectile.getDeltaMovement().lengthSqr() > 0.0D) {
                    return DetonationCandidate.projectile(candidate);
                }
                if (LogicAttackProcedure.execute(world, entity, candidate)) {
                    return DetonationCandidate.embedded(candidate);
                }
            }
        }
        return null;
    }

    private static void detonate(LevelAccessor world, double x, double y, double z, Entity entity, DetonationCandidate candidate) {
        double detonationX = candidate.target.getX();
        double detonationY = candidate.target.getY() + candidate.target.getBbHeight();
        double detonationZ = candidate.target.getZ();
        double nailPower = candidate.resolveNailPower();
        double range = nailPower;

        playSound(world, x, y, z, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, (float) range, 1.0F);
        if (world instanceof Level level && !level.isClientSide()) {
            level.explode(null, detonationX, detonationY, detonationZ, 0.0F, ExplosionInteraction.NONE);
        }

        spawnDetonationParticles(world, detonationX, detonationY, detonationZ, nailPower, range);
        if (candidate.projectile) {
            detonateProjectileTrail(world, entity, candidate.target, detonationX, detonationY, detonationZ, nailPower, range);
            return;
        }

        detonateEmbeddedTarget(world, entity, candidate.target, detonationX, detonationY, detonationZ, nailPower);
    }

    private static void spawnDetonationParticles(LevelAccessor world, double x, double y, double z, double nailPower, double range) {
        if (world instanceof ServerLevel level) {
            level.sendParticles(
                (SimpleParticleType) JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_BLUE.get(),
                x,
                y,
                z,
                (int) (10.0D * nailPower),
                0.25D * range,
                0.25D * range,
                0.25D * range,
                range
            );
            level.sendParticles(
                (SimpleParticleType) JujutsucraftModParticleTypes.PARTICLE_BLACK_FLASH_1.get(),
                x,
                y,
                z,
                (int) (5.0D * nailPower),
                0.25D * range,
                0.25D * range,
                0.25D * range,
                range
            );
        }
    }

    private static void detonateProjectileTrail(
        LevelAccessor world,
        Entity attacker,
        Entity projectile,
        double detonationX,
        double detonationY,
        double detonationZ,
        double nailPower,
        double range
    ) {
        double xPower = projectile.getDeltaMovement().x;
        double yPower = projectile.getDeltaMovement().y;
        double zPower = projectile.getDeltaMovement().z;
        if (!projectile.level().isClientSide()) {
            projectile.discard();
        }

        double currentX = detonationX - xPower * TRAIL_STEP * TRAIL_LENGTH_STEPS * range;
        double currentY = detonationY - yPower * TRAIL_STEP * TRAIL_LENGTH_STEPS * range;
        double currentZ = detonationZ - zPower * TRAIL_STEP * TRAIL_LENGTH_STEPS * range;
        double cooldown = 0.0D;
        int trailLoops = (int) (32.0D * range);

        for (int index = 0; index < trailLoops; index++) {
            if (++cooldown > 0.0D) {
                applyRangeAttackSettings(attacker, nailPower);
                RangeAttackProcedure.execute(world, currentX, currentY, currentZ, attacker);
                attacker.getPersistentData().putDouble("BlockRange", nailPower);
                attacker.getPersistentData().putDouble("BlockDamage", 2.0D * nailPower);
                BlockDestroyAllDirectionProcedure.execute(world, currentX, currentY, currentZ, attacker);
                cooldown = -TRAIL_PARTICLE_SPACING;
            }

            spawnTrailParticles(world, currentX, currentY, currentZ, nailPower, range);
            currentX += xPower * TRAIL_STEP;
            currentY += yPower * TRAIL_STEP;
            currentZ += zPower * TRAIL_STEP;
        }
    }

    private static void detonateEmbeddedTarget(
        LevelAccessor world,
        Entity attacker,
        Entity target,
        double detonationX,
        double detonationY,
        double detonationZ,
        double nailPower
    ) {
        target.getPersistentData().putDouble("Nail", 0.0D);
        applyRangeAttackSettings(attacker, nailPower);
        try (KugisakiHairpinTargetingContext.Scope ignored = KugisakiHairpinTargetingContext.enter(attacker, target)) {
            RangeAttackProcedure.execute(world, detonationX, detonationY, detonationZ, attacker);
        }
    }

    private static void applyRangeAttackSettings(Entity attacker, double nailPower) {
        attacker.getPersistentData().putDouble("Damage", 13.0D * nailPower);
        attacker.getPersistentData().putDouble("Range", 3.0D * nailPower);
        attacker.getPersistentData().putDouble("effect", 1.0D);
        attacker.getPersistentData().putDouble("effectConfirm", 2.0D);
        attacker.getPersistentData().putBoolean("ignore", true);
    }

    private static void spawnTrailParticles(LevelAccessor world, double x, double y, double z, double nailPower, double range) {
        if (world instanceof ServerLevel level) {
            level.sendParticles(
                (SimpleParticleType) JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_BLUE.get(),
                x,
                y,
                z,
                (int) (1.0D + nailPower),
                0.1D * range,
                0.1D * range,
                0.1D * range,
                0.0D
            );
        }
    }

    private static void playSound(LevelAccessor world, double x, double y, double z, net.minecraft.sounds.SoundEvent sound, float volume, float pitch) {
        if (world instanceof Level level) {
            if (!level.isClientSide()) {
                level.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.NEUTRAL, volume, pitch);
            } else {
                level.playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, volume, pitch, false);
            }
        }
    }

    private static final class DetonationCandidate {
        private final Entity target;
        private final boolean projectile;

        private DetonationCandidate(Entity target, boolean projectile) {
            this.target = target;
            this.projectile = projectile;
        }

        private static DetonationCandidate projectile(Entity target) {
            return new DetonationCandidate(target, true);
        }

        private static DetonationCandidate embedded(Entity target) {
            return new DetonationCandidate(target, false);
        }

        private double resolveNailPower() {
            double nailCount = this.projectile
                ? 1.0D
                : KugisakiHairpinRules.resolveNailDamageMultiplier(this.target.getPersistentData().getDouble("Nail"));
            return Math.max(nailCount, 1.0D);
        }
    }
}
