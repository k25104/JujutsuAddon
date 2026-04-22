package com.arf8vhg7.jja.feature.jja.technique.family.ranta;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.GetEntityFromUUIDProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class RantaEvilEyeBoundTargetService {
    private static final String TARGET_KEY = "TARGET";

    private RantaEvilEyeBoundTargetService() {
    }

    public static void runBoundTargetDamage(@Nullable Entity eyeEntity, Runnable damageAction) {
        String allowedTargetUuid = resolveAllowedTargetUuid(eyeEntity);
        if (allowedTargetUuid.isEmpty()) {
            return;
        }

        RantaEvilEyeBoundTargetContext.withAllowedTarget(allowedTargetUuid, Objects.requireNonNull(damageAction));
    }

    public static boolean shouldRestrictCandidate(@Nullable Entity candidate) {
        return RantaEvilEyeBoundTargetContext.shouldRestrictCandidate(candidate == null ? null : candidate.getStringUUID());
    }

    static String resolveAllowedTargetUuid(@Nullable Entity eyeEntity) {
        if (eyeEntity == null) {
            return "";
        }

        return resolveAllowedTargetUuid(
            eyeEntity.getPersistentData().getString(TARGET_KEY),
            targetUuid -> resolveBoundTargetState(eyeEntity, targetUuid)
        );
    }

    static String resolveAllowedTargetUuid(@Nullable String targetUuid, Function<String, BoundTargetState> targetResolver) {
        String normalizedTargetUuid = normalizeTargetUuid(targetUuid);
        if (normalizedTargetUuid.isEmpty() || targetResolver == null) {
            return "";
        }

        BoundTargetState targetState = targetResolver.apply(normalizedTargetUuid);
        return targetState != null && targetState.living() && targetState.alive() ? normalizedTargetUuid : "";
    }

    private static @Nullable BoundTargetState resolveBoundTargetState(Entity eyeEntity, String targetUuid) {
        Entity resolvedTarget = GetEntityFromUUIDProcedure.execute(eyeEntity.level(), targetUuid);
        if (resolvedTarget == null) {
            return null;
        }

        return new BoundTargetState(resolvedTarget instanceof LivingEntity, resolvedTarget.isAlive());
    }

    private static String normalizeTargetUuid(@Nullable String targetUuid) {
        return targetUuid == null ? "" : targetUuid.trim();
    }

    static record BoundTargetState(boolean living, boolean alive) {
    }
}
