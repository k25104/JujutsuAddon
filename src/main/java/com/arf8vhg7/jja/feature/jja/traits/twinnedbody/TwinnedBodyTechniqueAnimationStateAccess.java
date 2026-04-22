package com.arf8vhg7.jja.feature.jja.traits.twinnedbody;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public final class TwinnedBodyTechniqueAnimationStateAccess {
    private static final String KEY_TWINNED_BODY_TECHNIQUE_ANIMATION_EXPIRES_AT = "jjaTwinnedBodyTechniqueAnimationExpiresAt";

    private TwinnedBodyTechniqueAnimationStateAccess() {
    }

    public static boolean isTechniqueAnimationActive(@Nullable Entity entity) {
        return isTechniqueAnimationActive(data(entity), currentGameTime(entity));
    }

    static boolean isTechniqueAnimationActive(@Nullable CompoundTag data) {
        return isTechniqueAnimationActive(data, 0L);
    }

    static boolean isTechniqueAnimationActive(@Nullable CompoundTag data, long currentGameTime) {
        return data != null && data.getLong(KEY_TWINNED_BODY_TECHNIQUE_ANIMATION_EXPIRES_AT) > currentGameTime;
    }

    public static void setTechniqueAnimationActive(@Nullable Entity entity, long activeUntilGameTime) {
        setTechniqueAnimationActive(data(entity), activeUntilGameTime);
    }

    static void setTechniqueAnimationActive(@Nullable CompoundTag data, long activeUntilGameTime) {
        if (data != null) {
            long currentActiveUntil = data.getLong(KEY_TWINNED_BODY_TECHNIQUE_ANIMATION_EXPIRES_AT);
            data.putLong(KEY_TWINNED_BODY_TECHNIQUE_ANIMATION_EXPIRES_AT, Math.max(currentActiveUntil, Math.max(0L, activeUntilGameTime)));
        }
    }

    public static boolean clearTechniqueAnimationActive(@Nullable Entity entity) {
        return clearTechniqueAnimationActive(data(entity));
    }

    static boolean clearTechniqueAnimationActive(@Nullable CompoundTag data) {
        if (data == null) {
            return false;
        }

        boolean wasActive = data.getLong(KEY_TWINNED_BODY_TECHNIQUE_ANIMATION_EXPIRES_AT) > 0L;
        data.putLong(KEY_TWINNED_BODY_TECHNIQUE_ANIMATION_EXPIRES_AT, 0L);
        return wasActive;
    }

    public static long getTechniqueAnimationActiveUntil(@Nullable Entity entity) {
        return getTechniqueAnimationActiveUntil(data(entity));
    }

    static long getTechniqueAnimationActiveUntil(@Nullable CompoundTag data) {
        return data == null ? 0L : data.getLong(KEY_TWINNED_BODY_TECHNIQUE_ANIMATION_EXPIRES_AT);
    }

    @Nullable
    private static CompoundTag data(@Nullable Entity entity) {
        return entity == null ? null : entity.getPersistentData();
    }

    private static long currentGameTime(@Nullable Entity entity) {
        return entity == null ? Long.MAX_VALUE : entity.level().getGameTime();
    }
}