package com.arf8vhg7.jja.feature.jja.rct;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public final class RctRuntimeStateAccess {
    private static final String KEY_MANUAL_PRESS = "PRESS_M";
    private static final String KEY_AUTO_RCT_RUNNING = "jjaAutoRctRunning";
    private static final String KEY_BRAIN_DESTRUCTION_HOLDING = "jjaBrainDestructionHolding";
    private static final String KEY_BRAIN_DESTRUCTION_TICKS = "jjaBrainDestructionTicks";
    private static final String KEY_LAST_RCT_EFFECT_LEVEL = "jjaLastRctEffectLevel";

    private RctRuntimeStateAccess() {
    }

    public static void clearRuntimeState(@Nullable Entity entity) {
        clearRuntimeState(data(entity));
    }

    static void clearRuntimeState(@Nullable CompoundTag data) {
        if (data == null) {
            return;
        }
        setManualPressActive(data, false);
        setAutoRctRunning(data, false);
        setBrainDestructionHolding(data, false);
        setBrainDestructionTicks(data, 0);
        rememberRctEffectLevel(data, 0);
    }

    public static boolean isManualPressActive(@Nullable Entity entity) {
        return isManualPressActive(data(entity));
    }

    static boolean isManualPressActive(@Nullable CompoundTag data) {
        return data != null && data.getBoolean(KEY_MANUAL_PRESS);
    }

    public static void setManualPressActive(@Nullable Entity entity, boolean active) {
        setManualPressActive(data(entity), active);
    }

    static void setManualPressActive(@Nullable CompoundTag data, boolean active) {
        if (data != null) {
            data.putBoolean(KEY_MANUAL_PRESS, active);
        }
    }

    public static boolean isAutoRctRunning(@Nullable Entity entity) {
        return isAutoRctRunning(data(entity));
    }

    static boolean isAutoRctRunning(@Nullable CompoundTag data) {
        return data != null && data.getBoolean(KEY_AUTO_RCT_RUNNING);
    }

    public static void setAutoRctRunning(@Nullable Entity entity, boolean running) {
        setAutoRctRunning(data(entity), running);
    }

    static void setAutoRctRunning(@Nullable CompoundTag data, boolean running) {
        if (data != null) {
            data.putBoolean(KEY_AUTO_RCT_RUNNING, running);
        }
    }

    public static boolean isBrainDestructionHolding(@Nullable Entity entity) {
        return isBrainDestructionHolding(data(entity));
    }

    static boolean isBrainDestructionHolding(@Nullable CompoundTag data) {
        return data != null && data.getBoolean(KEY_BRAIN_DESTRUCTION_HOLDING);
    }

    public static void setBrainDestructionHolding(@Nullable Entity entity, boolean holding) {
        setBrainDestructionHolding(data(entity), holding);
    }

    static void setBrainDestructionHolding(@Nullable CompoundTag data, boolean holding) {
        if (data != null) {
            data.putBoolean(KEY_BRAIN_DESTRUCTION_HOLDING, holding);
        }
    }

    public static int getBrainDestructionTicks(@Nullable Entity entity) {
        return getBrainDestructionTicks(data(entity));
    }

    static int getBrainDestructionTicks(@Nullable CompoundTag data) {
        return data == null ? 0 : data.getInt(KEY_BRAIN_DESTRUCTION_TICKS);
    }

    public static void setBrainDestructionTicks(@Nullable Entity entity, int ticks) {
        setBrainDestructionTicks(data(entity), ticks);
    }

    static void setBrainDestructionTicks(@Nullable CompoundTag data, int ticks) {
        if (data != null) {
            data.putInt(KEY_BRAIN_DESTRUCTION_TICKS, Math.max(0, ticks));
        }
    }

    public static void rememberRctEffectLevel(@Nullable Entity entity, int effectLevel) {
        rememberRctEffectLevel(data(entity), effectLevel);
    }

    static void rememberRctEffectLevel(@Nullable CompoundTag data, int effectLevel) {
        if (data != null) {
            data.putInt(KEY_LAST_RCT_EFFECT_LEVEL, effectLevel);
        }
    }

    public static int getRememberedRctEffectLevel(@Nullable Entity entity) {
        return getRememberedRctEffectLevel(data(entity));
    }

    static int getRememberedRctEffectLevel(@Nullable CompoundTag data) {
        return data == null ? 0 : data.getInt(KEY_LAST_RCT_EFFECT_LEVEL);
    }

    @Nullable
    private static CompoundTag data(@Nullable Entity entity) {
        return entity == null ? null : entity.getPersistentData();
    }
}
