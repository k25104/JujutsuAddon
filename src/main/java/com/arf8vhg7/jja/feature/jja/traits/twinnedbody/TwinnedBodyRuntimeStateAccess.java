package com.arf8vhg7.jja.feature.jja.traits.twinnedbody;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public final class TwinnedBodyRuntimeStateAccess {
    private static final String KEY_TWINNED_BODY = "jjaTwinnedBody";
    private static final String KEY_COMBAT_ECHO_CONSUMED = "jjaTwinnedBodyCombatEchoConsumed";
    private static final String KEY_COMBAT_ECHO_CONSUMED_GAME_TIME = "jjaTwinnedBodyCombatEchoConsumedGameTime";

    private TwinnedBodyRuntimeStateAccess() {
    }

    public static boolean isTwinnedBodyMarked(@Nullable Entity entity) {
        return isTwinnedBodyMarked(data(entity));
    }

    static boolean isTwinnedBodyMarked(@Nullable CompoundTag data) {
        return data != null && data.getBoolean(KEY_TWINNED_BODY);
    }

    public static void markTwinnedBody(@Nullable Entity entity) {
        markTwinnedBody(data(entity));
    }

    static void markTwinnedBody(@Nullable CompoundTag data) {
        if (data != null) {
            clearCombatEchoConsumption(data);
            data.putBoolean(KEY_TWINNED_BODY, true);
        }
    }

    public static boolean clearTwinnedBody(@Nullable Entity entity) {
        return clearTwinnedBody(data(entity));
    }

    static boolean clearTwinnedBody(@Nullable CompoundTag data) {
        if (data == null) {
            return false;
        }

        boolean wasMarked = data.getBoolean(KEY_TWINNED_BODY);
        data.putBoolean(KEY_TWINNED_BODY, false);
        TwinnedBodyTechniqueAnimationStateAccess.clearTechniqueAnimationActive(data);
        clearCombatEchoConsumption(data);
        return wasMarked;
    }

    public static void copyTwinnedBody(@Nullable Entity source, @Nullable Entity target) {
        copyTwinnedBody(data(source), data(target));
    }

    static void copyTwinnedBody(@Nullable CompoundTag source, @Nullable CompoundTag target) {
        clearCombatEchoConsumption(target);
        if (isTwinnedBodyMarked(source)) {
            markTwinnedBody(target);
        }
    }

    public static boolean isCombatEchoConsumed(@Nullable Entity entity, long gameTime) {
        return isCombatEchoConsumed(data(entity), gameTime);
    }

    static boolean isCombatEchoConsumed(@Nullable CompoundTag data, long gameTime) {
        return data != null
            && data.getBoolean(KEY_COMBAT_ECHO_CONSUMED)
            && data.getLong(KEY_COMBAT_ECHO_CONSUMED_GAME_TIME) == gameTime;
    }

    public static void markCombatEchoConsumed(@Nullable Entity entity, long gameTime) {
        markCombatEchoConsumed(data(entity), gameTime);
    }

    static void markCombatEchoConsumed(@Nullable CompoundTag data, long gameTime) {
        if (data != null) {
            data.putBoolean(KEY_COMBAT_ECHO_CONSUMED, true);
            data.putLong(KEY_COMBAT_ECHO_CONSUMED_GAME_TIME, gameTime);
        }
    }

    public static boolean clearCombatEchoConsumption(@Nullable Entity entity) {
        return clearCombatEchoConsumption(data(entity));
    }

    static boolean clearCombatEchoConsumption(@Nullable CompoundTag data) {
        if (data == null) {
            return false;
        }

        boolean wasRecorded = data.getBoolean(KEY_COMBAT_ECHO_CONSUMED)
            || data.contains(KEY_COMBAT_ECHO_CONSUMED_GAME_TIME);
        data.remove(KEY_COMBAT_ECHO_CONSUMED);
        data.remove(KEY_COMBAT_ECHO_CONSUMED_GAME_TIME);
        return wasRecorded;
    }

    @Nullable
    private static CompoundTag data(@Nullable Entity entity) {
        return entity == null ? null : entity.getPersistentData();
    }
}
