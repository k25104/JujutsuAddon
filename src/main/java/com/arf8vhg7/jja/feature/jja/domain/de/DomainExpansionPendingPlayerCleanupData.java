package com.arf8vhg7.jja.feature.jja.domain.de;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

final class DomainExpansionPendingPlayerCleanupData extends SavedData {
    private static final String DATA_NAME = "jja_domain_pending_player_cleanup";
    private static final String KEY_PENDING_PLAYERS = "pending_players";

    private final Set<UUID> pendingPlayers = new HashSet<>();

    private DomainExpansionPendingPlayerCleanupData() {
    }

    static DomainExpansionPendingPlayerCleanupData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
            DomainExpansionPendingPlayerCleanupData::load,
            DomainExpansionPendingPlayerCleanupData::new,
            DATA_NAME
        );
    }

    private static DomainExpansionPendingPlayerCleanupData load(CompoundTag tag) {
        DomainExpansionPendingPlayerCleanupData data = new DomainExpansionPendingPlayerCleanupData();
        ListTag pendingList = tag.getList(KEY_PENDING_PLAYERS, Tag.TAG_STRING);
        for (int i = 0; i < pendingList.size(); i++) {
            String value = pendingList.getString(i);
            try {
                data.pendingPlayers.add(UUID.fromString(value));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return data;
    }

    boolean add(UUID playerId) {
        boolean changed = this.pendingPlayers.add(playerId);
        if (changed) {
            this.setDirty();
        }
        return changed;
    }

    boolean remove(UUID playerId) {
        boolean changed = this.pendingPlayers.remove(playerId);
        if (changed) {
            this.setDirty();
        }
        return changed;
    }

    Set<UUID> snapshot() {
        return Set.copyOf(this.pendingPlayers);
    }

    @Override
    public @Nonnull CompoundTag save(@Nonnull CompoundTag tag) {
        ListTag pendingList = new ListTag();
        for (UUID playerId : this.pendingPlayers) {
            pendingList.add(StringTag.valueOf(Objects.requireNonNull(playerId.toString())));
        }
        tag.put(KEY_PENDING_PLAYERS, pendingList);
        return tag;
    }
}
