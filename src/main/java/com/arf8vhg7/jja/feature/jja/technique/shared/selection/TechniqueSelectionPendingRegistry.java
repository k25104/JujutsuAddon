package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class TechniqueSelectionPendingRegistry {
    private final Set<UUID> pendingSelections = ConcurrentHashMap.newKeySet();

    public void begin(Entity entity) {
        if (entity instanceof Player player) {
            begin(player.getUUID());
        }
    }

    public boolean isPending(Entity entity) {
        return entity instanceof Player player && isPending(player.getUUID());
    }

    public void finish(Entity entity) {
        if (entity instanceof Player player) {
            finish(player.getUUID());
        }
    }

    void begin(UUID playerId) {
        if (playerId != null) {
            pendingSelections.add(playerId);
        }
    }

    boolean isPending(UUID playerId) {
        return playerId != null && pendingSelections.contains(playerId);
    }

    void finish(UUID playerId) {
        if (playerId != null) {
            pendingSelections.remove(playerId);
        }
    }
}
