package com.arf8vhg7.jja.hook.jujutsucraft;

import java.util.function.DoublePredicate;
import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class GrabProcedureHook {
    static final double GRAB_DISTANCE_BACKOFF_STEP = 0.125D;

    private GrabProcedureHook() {
    }

    public static Vec3 resolveSafeGrabDestination(LevelAccessor world, Entity caster, Entity target) {
        double grabDistance = resolveGrabDistance(caster, target);
        return resolveSafeGrabDestination(world, caster, target, grabDistance);
    }

    static Vec3 resolveSafeGrabDestination(LevelAccessor world, Entity caster, Entity target, double grabDistance) {
        if (!(world instanceof Level level)) {
            return computeGrabDestination(
                caster.getX(),
                caster.getY(),
                caster.getZ(),
                caster.getYRot(),
                caster.getXRot(),
                caster.getBbHeight(),
                target.getBbHeight(),
                grabDistance
            );
        }

        double safeDistance = selectSafeGrabDistance(grabDistance, candidateDistance -> {
            Vec3 candidateDestination = computeGrabDestination(
                caster.getX(),
                caster.getY(),
                caster.getZ(),
                caster.getYRot(),
                caster.getXRot(),
                caster.getBbHeight(),
                target.getBbHeight(),
                candidateDistance
            );
            return canOccupy(level, target, candidateDestination);
        });

        return computeGrabDestination(
            caster.getX(),
            caster.getY(),
            caster.getZ(),
            caster.getYRot(),
            caster.getXRot(),
            caster.getBbHeight(),
            target.getBbHeight(),
            safeDistance
        );
    }

    static double resolveGrabDistance(Entity caster, Entity target) {
        return 2.0D + 0.5D * (caster.getBbWidth() + target.getBbWidth());
    }

    static double selectSafeGrabDistance(double grabDistance, DoublePredicate canOccupy) {
        if (grabDistance <= 0.0D) {
            return 0.0D;
        }

        for (double candidateDistance = grabDistance; candidateDistance > 0.0D; candidateDistance -= GRAB_DISTANCE_BACKOFF_STEP) {
            if (canOccupy.test(candidateDistance)) {
                return candidateDistance;
            }
        }
        return canOccupy.test(0.0D) ? 0.0D : grabDistance;
    }

    static Vec3 computeGrabDestination(
        double casterX,
        double casterY,
        double casterZ,
        float casterYRot,
        float casterXRot,
        double casterBbHeight,
        double targetBbHeight,
        double grabDistance
    ) {
        double yaw = Math.toRadians(casterYRot + 90.0F);
        double pitch = Math.toRadians(casterXRot);
        double limitedPitch = Math.min(pitch, Math.toRadians(45.0D));
        return new Vec3(
            casterX + Math.cos(yaw) * Math.cos(limitedPitch) * grabDistance,
            Math.max(casterY + casterBbHeight * 0.9D + Math.sin(pitch) * -1.0D * grabDistance - targetBbHeight * 0.9D, casterY),
            casterZ + Math.sin(yaw) * Math.cos(limitedPitch) * grabDistance
        );
    }

    static boolean canOccupy(Level level, Entity entity, Vec3 destination) {
        Vec3 currentPosition = Objects.requireNonNull(entity.position());
        Vec3 safeDestination = Objects.requireNonNull(destination);
        AABB shiftedBoundingBox = Objects.requireNonNull(entity.getBoundingBox()).move(
            safeDestination.x - currentPosition.x,
            safeDestination.y - currentPosition.y,
            safeDestination.z - currentPosition.z
        );
        return level.noCollision(entity, Objects.requireNonNull(shiftedBoundingBox));
    }
}