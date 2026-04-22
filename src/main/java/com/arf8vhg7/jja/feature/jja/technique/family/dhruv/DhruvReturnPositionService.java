package com.arf8vhg7.jja.feature.jja.technique.family.dhruv;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import java.util.function.DoublePredicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class DhruvReturnPositionService {
    static final double MAX_OWNER_DISTANCE = 128.0D;
    static final double RETURN_MARGIN = 0.01D;
    static final double MIN_VECTOR_LENGTH_SQR = 1.0E-6D;

    private DhruvReturnPositionService() {
    }

    public static void enforceOwnerDistance(ServerLevel level, Entity shikigami) {
        if (level == null || shikigami == null || !DhruvTrailBlockService.isTrailPlacementEntity(shikigami)) {
            return;
        }

        Entity owner = JjaJujutsucraftDataAccess.jjaResolveDirectOwner(level, shikigami);
        if (!(owner instanceof LivingEntity) || owner.level() != shikigami.level()) {
            return;
        }

        Vec3 ownerPos = owner.position();
        Vec3 shikigamiPos = shikigami.position();
        if (!shouldTeleportBack(ownerPos, shikigamiPos)) {
            return;
        }

        Vec3 clampedTarget = clampInsideBoundary(ownerPos, shikigamiPos);
        Vec3 safeTarget = resolveSafeTeleportDestination(level, shikigami, clampedTarget);
        if (safeTarget == null) {
            return;
        }

        shikigami.setDeltaMovement(Vec3.ZERO);
        shikigami.fallDistance = 0.0F;
        shikigami.moveTo(safeTarget.x, safeTarget.y, safeTarget.z, shikigami.getYRot(), shikigami.getXRot());
        if (shikigami instanceof LivingEntity livingEntity) {
            livingEntity.setYBodyRot(shikigami.getYRot());
            livingEntity.setYHeadRot(shikigami.getYRot());
        }
    }

    static boolean shouldTeleportBack(Vec3 ownerPos, Vec3 shikigamiPos) {
        return ownerPos != null
            && shikigamiPos != null
            && ownerPos.distanceToSqr(shikigamiPos) > MAX_OWNER_DISTANCE * MAX_OWNER_DISTANCE;
    }

    static Vec3 clampInsideBoundary(Vec3 ownerPos, Vec3 shikigamiPos) {
        Vec3 offset = shikigamiPos.subtract(ownerPos);
        double lengthSqr = offset.lengthSqr();
        if (lengthSqr <= MAX_OWNER_DISTANCE * MAX_OWNER_DISTANCE || lengthSqr < MIN_VECTOR_LENGTH_SQR) {
            return shikigamiPos;
        }
        return ownerPos.add(offset.normalize().scale(MAX_OWNER_DISTANCE - RETURN_MARGIN));
    }

    @Nullable
    static Vec3 resolveSafeTeleportDestination(ServerLevel level, Entity shikigami, Vec3 clampedTarget) {
        double startY = Math.max(clampedTarget.y, level.getMinBuildHeight());
        Double safeY = selectTeleportY(
            startY,
            level.getMaxBuildHeight(),
            candidateY -> canOccupy(level, shikigami, new Vec3(clampedTarget.x, candidateY, clampedTarget.z)),
            candidateY -> hasStableSupport(level, new Vec3(clampedTarget.x, candidateY, clampedTarget.z))
        );
        return safeY == null ? null : new Vec3(clampedTarget.x, safeY, clampedTarget.z);
    }

    @Nullable
    static Double selectTeleportY(double startY, int maxBuildHeight, DoublePredicate canOccupy, DoublePredicate hasStableSupport) {
        Double collisionOnlyFallback = null;
        for (double candidateY = startY; candidateY <= maxBuildHeight; candidateY += 1.0D) {
            if (!canOccupy.test(candidateY)) {
                continue;
            }
            if (collisionOnlyFallback == null) {
                collisionOnlyFallback = candidateY;
            }
            if (hasStableSupport.test(candidateY)) {
                return candidateY;
            }
        }
        return collisionOnlyFallback;
    }

    static boolean canOccupy(ServerLevel level, Entity shikigami, Vec3 targetPos) {
        AABB shiftedBoundingBox = shikigami.getBoundingBox().move(targetPos.subtract(shikigami.position()));
        return level.noCollision(shikigami, shiftedBoundingBox);
    }

    static boolean hasStableSupport(ServerLevel level, Vec3 targetPos) {
        BlockPos supportPos = BlockPos.containing(targetPos.x, targetPos.y - 0.01D, targetPos.z);
        if (supportPos.getY() < level.getMinBuildHeight()) {
            return false;
        }

        BlockState supportState = level.getBlockState(supportPos);
        return !supportState.isAir() && supportState.isFaceSturdy(level, supportPos, Direction.UP);
    }
}
