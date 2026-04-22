package com.arf8vhg7.jja.feature.player.health.firstaid;

import com.arf8vhg7.jja.compat.firstaid.FirstAidDamageModelCompat;
import com.arf8vhg7.jja.compat.firstaid.FirstAidHealthCompat;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.OgiZeninPassiveSkillProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public final class FirstAidHealthSyncService {
    private static final String KEY_JJA_FIRST_AID_PENDING_HEALTH_BOOST_MISSING = "jjaFirstAidPendingHealthBoostMissing";
    private static final String KEY_JJA_FIRST_AID_PENDING_HEALTH_BOOST_QUEUED_GAME_TIME = "jjaFirstAidPendingHealthBoostQueuedGameTime";
    private static final float CURRENT_HEALTH_EPSILON = 0.05F;

    private FirstAidHealthSyncService() {
    }

    public enum DamageModelSyncMode {
        NONE,
        DEFERRED_ABSOLUTE,
        IMMEDIATE_ABSOLUTE
    }

    static float resolveCurrentHealthRatio(float currentHealth, float maxHealth) {
        if (!Float.isFinite(currentHealth) || !Float.isFinite(maxHealth) || !(maxHealth > 0.0F)) {
            return 0.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, currentHealth / maxHealth));
    }

    public static void finalizeMutation(Player player, boolean syncVanillaHealth, DamageModelSyncMode syncMode) {
        player = FirstAidPlayerResolver.resolve(player);
        if (player == null) {
            return;
        }
        if (syncVanillaHealth) {
            FirstAidHealthCompat.syncVanillaHealth(player);
        }
        switch (syncMode) {
            case NONE -> {
            }
            case DEFERRED_ABSOLUTE -> FirstAidDamageModelCompat.scheduleResync(player);
            case IMMEDIATE_ABSOLUTE -> {
                if (!FirstAidDamageModelCompat.trySyncDamageModelNow(player)) {
                    FirstAidDamageModelCompat.scheduleResync(player);
                }
            }
        }
    }

    public static void queuePendingHealthBoostRestore(Player player, @Nullable CompoundTag snapshot) {
        player = FirstAidPlayerResolver.resolve(player);
        if (player == null || snapshot == null || snapshot.isEmpty()) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        data.put(KEY_JJA_FIRST_AID_PENDING_HEALTH_BOOST_MISSING, snapshot.copy());
        data.putLong(KEY_JJA_FIRST_AID_PENDING_HEALTH_BOOST_QUEUED_GAME_TIME, player.level().getGameTime());
    }

    public static void applyPendingHealthBoostRestoreIfReady(Player player) {
        player = FirstAidPlayerResolver.resolve(player);
        if (player == null) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        if (!data.contains(KEY_JJA_FIRST_AID_PENDING_HEALTH_BOOST_MISSING, Tag.TAG_COMPOUND)) {
            return;
        }
        long queuedGameTime = data.getLong(KEY_JJA_FIRST_AID_PENDING_HEALTH_BOOST_QUEUED_GAME_TIME);
        if (player.level().getGameTime() <= queuedGameTime) {
            return;
        }
        if (!player.hasEffect(MobEffects.HEALTH_BOOST)) {
            clearPendingHealthBoostRestore(player);
            return;
        }
        CompoundTag snapshot = data.getCompound(KEY_JJA_FIRST_AID_PENDING_HEALTH_BOOST_MISSING);
        clearPendingHealthBoostRestore(player);
        if (snapshot.isEmpty()) {
            return;
        }
        FirstAidDamageModelCompat.restoreMissingHealth(player, snapshot);
        finalizeMutation(player, true, DamageModelSyncMode.DEFERRED_ABSOLUTE);
        OgiZeninPassiveSkillProcedure.execute(player);
    }

    public static void clearPendingHealthBoostRestore(Player player) {
        player = FirstAidPlayerResolver.resolve(player);
        if (player == null) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        data.remove(KEY_JJA_FIRST_AID_PENDING_HEALTH_BOOST_MISSING);
        data.remove(KEY_JJA_FIRST_AID_PENDING_HEALTH_BOOST_QUEUED_GAME_TIME);
    }

    public static void stripDebuffsAndSync(Player player) {
        player = FirstAidPlayerResolver.resolve(player);
        if (player == null) {
            return;
        }
        clearPendingHealthBoostRestore(player);
        FirstAidHealthCompat.DamageModelInspection inspection = FirstAidHealthCompat.inspectDamageModel(player, true);
        float effectiveHealth = inspection == null ? player.getHealth() : inspection.vanillaHealth();
        if (Float.isFinite(effectiveHealth) && Math.abs(player.getHealth() - effectiveHealth) > CURRENT_HEALTH_EPSILON) {
            FirstAidHealthCompat.syncVanillaHealth(player);
        }
        OgiZeninPassiveSkillProcedure.execute(player);
    }
}
