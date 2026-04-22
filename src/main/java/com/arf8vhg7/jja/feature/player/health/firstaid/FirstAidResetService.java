package com.arf8vhg7.jja.feature.player.health.firstaid;

import com.arf8vhg7.jja.compat.firstaid.FirstAidDamageModelCompat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.mcreator.jujutsucraft.procedures.PlayerPhysicalAbilityProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class FirstAidResetService {
    private static final Map<UUID, Deque<CompoundTag>> RESET_SNAPSHOTS = new ConcurrentHashMap<>();

    private FirstAidResetService() {
    }

    public static void beginResetTransaction(Entity entity) {
        Player player = FirstAidPlayerResolver.resolve(entity);
        if (player == null) {
            return;
        }
        CompoundTag snapshot = FirstAidDamageModelCompat.snapshotDamageModelWithRatios(player);
        if (snapshot == null) {
            return;
        }
        RESET_SNAPSHOTS.computeIfAbsent(player.getUUID(), ignored -> new ArrayDeque<>()).push(snapshot);
    }

    public static void finishResetTransaction(Entity entity) {
        Player player = FirstAidPlayerResolver.resolve(entity);
        if (player == null) {
            return;
        }
        Deque<CompoundTag> snapshots = RESET_SNAPSHOTS.get(player.getUUID());
        if (snapshots == null || snapshots.isEmpty()) {
            return;
        }
        CompoundTag snapshot = snapshots.pop();
        if (snapshots.isEmpty()) {
            RESET_SNAPSHOTS.remove(player.getUUID());
        }
        try {
            PlayerPhysicalAbilityProcedure.execute(player);
        } finally {
            FirstAidDamageModelCompat.restoreDamageModelPreservingRatios(player, snapshot);
            FirstAidHealthSyncService.finalizeMutation(player, false, FirstAidHealthSyncService.DamageModelSyncMode.NONE);
        }
    }

    public static <T> T preserveDamageModel(Entity entity, Supplier<T> action) {
        beginResetTransaction(entity);
        try {
            return action.get();
        } finally {
            finishResetTransaction(entity);
        }
    }

    public static void preserveDamageModel(Entity entity, Runnable action) {
        preserveDamageModel(entity, () -> {
            action.run();
            return null;
        });
    }

    public static boolean clearAllEffectsPreservingDamageModel(LivingEntity entity) {
        return preserveDamageModel(entity, entity::removeAllEffects);
    }
}
