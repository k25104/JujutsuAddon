package com.arf8vhg7.jja.feature.jja.traits.twinnedbody.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public final class TwinnedBodyClientState {
    private static final Set<UUID> ACTIVE_ENTITY_IDS = new HashSet<>();

    private TwinnedBodyClientState() {
    }

    public static void apply(UUID entityId, boolean active) {
        if (entityId == null) {
            return;
        }

        if (active) {
            ACTIVE_ENTITY_IDS.add(entityId);
        } else {
            ACTIVE_ENTITY_IDS.remove(entityId);
            TwinnedBodyTechniqueAnimationState.clear(entityId);
        }
    }

    public static boolean isActive(@Nullable Entity entity) {
        return entity != null && ACTIVE_ENTITY_IDS.contains(entity.getUUID());
    }

    public static void clearAll() {
        ACTIVE_ENTITY_IDS.clear();
        TwinnedBodyTechniqueAnimationState.clearAll();
    }
}
