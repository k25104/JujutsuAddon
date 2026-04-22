package com.arf8vhg7.jja.feature.jja.domain.sd;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;

public final class SimpleDomainExpireGuard {
    private static final ThreadLocal<UUID> GUARDED_ENTITY_UUID = new ThreadLocal<>();

    private SimpleDomainExpireGuard() {
    }

    public static <T> T withGuard(Entity entity, Supplier<T> action) {
        return withGuardedUuid(entity != null ? entity.getUUID() : null, action);
    }

    public static boolean isGuarded(Entity entity) {
        return entity != null && isGuardedUuid(entity.getUUID());
    }

    static <T> T withGuardedUuid(UUID guardedEntityUuid, Supplier<T> action) {
        UUID previous = GUARDED_ENTITY_UUID.get();
        GUARDED_ENTITY_UUID.set(guardedEntityUuid);
        try {
            return action.get();
        } finally {
            if (previous == null) {
                GUARDED_ENTITY_UUID.remove();
            } else {
                GUARDED_ENTITY_UUID.set(previous);
            }
        }
    }

    static boolean isGuardedUuid(UUID entityUuid) {
        return guardedEntityUuidMatches(GUARDED_ENTITY_UUID.get(), entityUuid);
    }

    static boolean guardedEntityUuidMatches(UUID guardedEntityUuid, UUID entityUuid) {
        return guardedEntityUuid != null && guardedEntityUuid.equals(entityUuid);
    }
}
