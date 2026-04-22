package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainPresentation;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;

final class AntiDomainRuntimeStore {
    private static final Map<UUID, AntiDomainRuntimeState> CLIENT_RUNTIME_STATES = new ConcurrentHashMap<>();
    private static final Map<UUID, AntiDomainRuntimeState> SERVER_RUNTIME_STATES = new ConcurrentHashMap<>();

    private AntiDomainRuntimeStore() {
    }

    static AntiDomainRuntimeState get(Entity entity) {
        return entity == null ? null : get(entity.getUUID(), entity.level().isClientSide());
    }

    static AntiDomainRuntimeState getOrCreate(Entity entity) {
        return getOrCreate(entity.getUUID(), entity.level().isClientSide());
    }

    static void remove(Entity entity) {
        if (entity == null) {
            return;
        }
        remove(entity.getUUID(), entity.level().isClientSide());
    }

    static void removeIfIdle(Entity entity, AntiDomainRuntimeState state) {
        if (entity == null || state == null || !state.isIdle()) {
            return;
        }
        remove(entity.getUUID(), entity.level().isClientSide());
    }

    static AntiDomainPresentation getRuntimeActivePresentation(Entity entity) {
        AntiDomainRuntimeState state = get(entity);
        return state != null ? state.session.activePresentation : AntiDomainPresentation.NONE;
    }

    static AntiDomainTechniqueOption getRuntimeSelectedOptionAtPress(Entity entity) {
        AntiDomainRuntimeState state = get(entity);
        return state != null ? state.press.selectedOptionAtPress : null;
    }

    static AntiDomainRuntimeState get(UUID entityId, boolean clientSide) {
        if (entityId == null) {
            return null;
        }
        return runtimeStates(clientSide).get(entityId);
    }

    static AntiDomainRuntimeState getOrCreate(UUID entityId, boolean clientSide) {
        return runtimeStates(clientSide).computeIfAbsent(entityId, unused -> new AntiDomainRuntimeState());
    }

    static void remove(UUID entityId, boolean clientSide) {
        if (entityId == null) {
            return;
        }
        runtimeStates(clientSide).remove(entityId);
    }

    static void removeIfIdle(UUID entityId, boolean clientSide, AntiDomainRuntimeState state) {
        if (entityId == null || state == null || !state.isIdle()) {
            return;
        }
        runtimeStates(clientSide).remove(entityId);
    }

    private static Map<UUID, AntiDomainRuntimeState> runtimeStates(boolean clientSide) {
        return clientSide ? CLIENT_RUNTIME_STATES : SERVER_RUNTIME_STATES;
    }
}
