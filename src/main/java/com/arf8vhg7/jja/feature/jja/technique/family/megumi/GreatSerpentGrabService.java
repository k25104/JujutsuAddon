package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.LogicAttackProcedure;
import net.mcreator.jujutsucraft.procedures.ReturnEntitySizeProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class GreatSerpentGrabService {
    private static final String KEY_GRAB_TARGET_UUID = "jjaGreatSerpentGrabTargetUuid";
    private static final int SELF_SLOWNESS_DURATION = 10;
    private static final int SELF_SLOWNESS_AMPLIFIER = 4;
    private static final int TARGET_SLOWNESS_DURATION = 10;
    private static final int TARGET_SLOWNESS_AMPLIFIER = 9;
    private static final int TARGET_COMBAT_COOLDOWN_DURATION = 5;
    private static final int TARGET_COMBAT_COOLDOWN_AMPLIFIER = 1;
    private static final int TARGET_BACK_STEP_COOLDOWN_DURATION = 10;
    private static final int TARGET_BACK_STEP_COOLDOWN_AMPLIFIER = 9;
    private static final int TARGET_GUARD_COOLDOWN_DURATION = 10;
    private static final int MAX_GRAB_DISTANCE_SQR = 64;

    private GreatSerpentGrabService() {
    }

    public static void tickActiveGrab(Entity serpent) {
        if (serpent == null || serpent.level().isClientSide()) {
            return;
        }
        Entity target = resolveGrabTarget(serpent);
        if (!isValidGrabTarget(serpent, target)) {
            clearGrabTarget(serpent);
            return;
        }
        applyGrabState(serpent, target);
    }

    public static boolean tryGrabTarget(Entity serpent, Entity target) {
        if (!(target instanceof LivingEntity) || serpent == null) {
            return false;
        }
        if (serpent.level().isClientSide()) {
            return true;
        }

        Entity activeTarget = resolveGrabTarget(serpent);
        if (isValidGrabTarget(serpent, activeTarget)) {
            if (activeTarget == target) {
                applyGrabState(serpent, target);
                return true;
            }
            return false;
        }

        serpent.getPersistentData().putString(KEY_GRAB_TARGET_UUID, target.getStringUUID());
        applyGrabState(serpent, target);
        return true;
    }

    public static void tryGrabNearbyTarget(LevelAccessor world, double x, double y, double z, Entity serpent) {
        if (serpent == null || serpent.level().isClientSide() || !isGreatSerpentGrabWindow(serpent)) {
            return;
        }

        Entity activeTarget = resolveGrabTarget(serpent);
        if (isValidGrabTarget(serpent, activeTarget)) {
            applyGrabState(serpent, activeTarget);
            return;
        }

        double radius = 3.0D * ReturnEntitySizeProcedure.execute(serpent) / 2.0D;
        Vec3 center = new Vec3(x, y + serpent.getBbHeight(), z);
        for (Entity candidate : world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(radius), ignored -> true)) {
            if (candidate == serpent
                || !(candidate instanceof LivingEntity)
                || candidate.isPassenger()
                || !LogicAttackProcedure.execute(world, serpent, candidate)) {
                continue;
            }
            if (tryGrabTarget(serpent, candidate)) {
                return;
            }
        }
    }

    public static Entity resolveHeldVehicle(Entity candidate, Entity serpent, @Nullable Entity originalVehicle) {
        if (originalVehicle != null || !isHeldTarget(candidate, serpent)) {
            return originalVehicle;
        }
        return serpent;
    }

    public static void clearGrabWhenInactive(Entity serpent) {
        if (serpent == null || serpent.isAlive() && serpent.getPersistentData().getDouble("skill") == 1.0D) {
            return;
        }
        clearGrabTarget(serpent);
    }

    static boolean isHeldTarget(Entity candidate, Entity serpent) {
        if (candidate == null || serpent == null) {
            return false;
        }
        return candidate.getStringUUID().equals(serpent.getPersistentData().getString(KEY_GRAB_TARGET_UUID));
    }

    @Nullable
    private static Entity resolveGrabTarget(Entity serpent) {
        if (serpent == null) {
            return null;
        }
        String targetUuid = serpent.getPersistentData().getString(KEY_GRAB_TARGET_UUID);
        return targetUuid.isBlank() ? null : net.mcreator.jujutsucraft.procedures.GetEntityFromUUIDProcedure.execute(serpent.level(), targetUuid);
    }

    private static boolean isValidGrabTarget(Entity serpent, @Nullable Entity target) {
        return serpent != null
            && target instanceof LivingEntity
            && target.isAlive()
            && !target.isRemoved()
            && target.level() == serpent.level()
            && serpent.distanceToSqr(target) <= MAX_GRAB_DISTANCE_SQR;
    }

    private static boolean isGreatSerpentGrabWindow(Entity serpent) {
        return serpent.isAlive()
            && serpent.getPersistentData().getDouble("skill") == 1.0D
            && serpent.getPersistentData().getDouble("cnt2") != 0.0D
            && serpent.getPersistentData().getDouble("cnt1") >= 3.0D
            && serpent.getPersistentData().getDouble("cnt1") < 45.0D;
    }

    private static void applyGrabState(Entity serpent, Entity target) {
        if (!(target instanceof LivingEntity livingTarget)) {
            clearGrabTarget(serpent);
            return;
        }

        applyEffects(serpent, livingTarget);
        Vec3 destination = resolveSafeRideAnchor(serpent, livingTarget);
        float targetYaw = serpent.getYRot() + 180.0F;
        float targetPitch = serpent.getXRot() * -1.0F;
        livingTarget.moveTo(destination.x, destination.y, destination.z, targetYaw, targetPitch);
        if (livingTarget instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.teleport(destination.x, destination.y, destination.z, targetYaw, targetPitch);
        }
        livingTarget.setYRot(targetYaw);
        livingTarget.setXRot(targetPitch);
        livingTarget.setYBodyRot(targetYaw);
        livingTarget.setYHeadRot(targetYaw);
        livingTarget.setDeltaMovement(serpent.getDeltaMovement());
        livingTarget.hurtMarked = true;
    }

    private static void applyEffects(Entity serpent, LivingEntity target) {
        if (serpent instanceof LivingEntity livingSerpent) {
            livingSerpent.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                SELF_SLOWNESS_DURATION,
                SELF_SLOWNESS_AMPLIFIER,
                false,
                false
            ));
        }
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, TARGET_SLOWNESS_DURATION, TARGET_SLOWNESS_AMPLIFIER, false, false));
        target.addEffect(new MobEffectInstance(
            Objects.requireNonNull((MobEffect) JujutsucraftModMobEffects.COOLDOWN_TIME_COMBAT.get()),
            TARGET_COMBAT_COOLDOWN_DURATION,
            TARGET_COMBAT_COOLDOWN_AMPLIFIER,
            false,
            false
        ));
        target.addEffect(new MobEffectInstance(
            Objects.requireNonNull((MobEffect) JujutsucraftModMobEffects.COOLDOWN_TIME_BACK_STEP.get()),
            TARGET_BACK_STEP_COOLDOWN_DURATION,
            TARGET_BACK_STEP_COOLDOWN_AMPLIFIER,
            false,
            false
        ));
        target.addEffect(new MobEffectInstance(
            Objects.requireNonNull((MobEffect) JujutsucraftModMobEffects.COOLDOWN_TIME_GUARD.get()),
            TARGET_GUARD_COOLDOWN_DURATION,
            0,
            false,
            false
        ));
        preserveGuardEffect(target);
        target.removeEffect(Objects.requireNonNull((MobEffect) JujutsucraftModMobEffects.FLY_EFFECT.get()));
        target.removeEffect(Objects.requireNonNull((MobEffect) JujutsucraftModMobEffects.DOUBLE_JUMP_EFFECT.get()));
    }

    private static void preserveGuardEffect(LivingEntity target) {
        MobEffect guardEffect = Objects.requireNonNull((MobEffect) JujutsucraftModMobEffects.GUARD.get());
        MobEffectInstance guardInstance = target.getEffect(guardEffect);
        if (guardInstance == null || guardInstance.getAmplifier() <= 0) {
            return;
        }
        int duration = guardInstance.getDuration();
        target.removeEffect(guardEffect);
        target.addEffect(new MobEffectInstance(guardEffect, duration, 0, false, false));
    }

    private static Vec3 resolveSafeRideAnchor(Entity serpent, Entity target) {
        Vec3 anchor = resolveRideAnchor(serpent, target);
        Level level = serpent.level();
        if (canOccupy(level, target, anchor)) {
            return anchor;
        }

        for (double verticalOffset = 0.25D; verticalOffset <= 1.5D; verticalOffset += 0.25D) {
            Vec3 raisedAnchor = anchor.add(0.0D, verticalOffset, 0.0D);
            if (canOccupy(level, target, raisedAnchor)) {
                return raisedAnchor;
            }
        }

        return anchor;
    }

    private static Vec3 resolveRideAnchor(Entity serpent, Entity target) {
        return new Vec3(
            serpent.getX(),
            serpent.getY() + serpent.getPassengersRidingOffset() + target.getMyRidingOffset(),
            serpent.getZ()
        );
    }

    private static boolean canOccupy(Level level, Entity target, Vec3 destination) {
        Vec3 currentPosition = target.position();
        AABB movedBoundingBox = target.getBoundingBox().move(
            destination.x - currentPosition.x,
            destination.y - currentPosition.y,
            destination.z - currentPosition.z
        );
        return level.noCollision(target, movedBoundingBox);
    }

    private static void clearGrabTarget(Entity serpent) {
        if (serpent != null) {
            serpent.getPersistentData().putString(KEY_GRAB_TARGET_UUID, "");
        }
    }
}
