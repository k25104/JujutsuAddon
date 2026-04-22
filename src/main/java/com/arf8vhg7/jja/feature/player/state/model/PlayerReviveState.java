package com.arf8vhg7.jja.feature.player.state.model;

import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSchema;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class PlayerReviveState {
    private int reviveRemainingTicks;
    private int remainingRevives = 3;
    private boolean reviveHoldActive;
    private int reviveHoldTicks;
    private UUID reviveHoldTarget;
    private int reviveSpecialStage;
    private int reviveSpecialTicks;

    public int getReviveRemainingTicks() {
        return this.reviveRemainingTicks;
    }

    public void setReviveRemainingTicks(int reviveRemainingTicks) {
        this.reviveRemainingTicks = reviveRemainingTicks;
    }

    public int getRemainingRevives() {
        return this.remainingRevives;
    }

    public void setRemainingRevives(int remainingRevives) {
        this.remainingRevives = remainingRevives;
    }

    public boolean isReviveHoldActive() {
        return this.reviveHoldActive;
    }

    public void setReviveHoldActive(boolean reviveHoldActive) {
        this.reviveHoldActive = reviveHoldActive;
    }

    public int getReviveHoldTicks() {
        return this.reviveHoldTicks;
    }

    public void setReviveHoldTicks(int reviveHoldTicks) {
        this.reviveHoldTicks = reviveHoldTicks;
    }

    public UUID getReviveHoldTarget() {
        return this.reviveHoldTarget;
    }

    public void setReviveHoldTarget(UUID reviveHoldTarget) {
        this.reviveHoldTarget = reviveHoldTarget;
    }

    public int getReviveSpecialStage() {
        return this.reviveSpecialStage;
    }

    public void setReviveSpecialStage(int reviveSpecialStage) {
        this.reviveSpecialStage = reviveSpecialStage;
    }

    public int getReviveSpecialTicks() {
        return this.reviveSpecialTicks;
    }

    public void setReviveSpecialTicks(int reviveSpecialTicks) {
        this.reviveSpecialTicks = reviveSpecialTicks;
    }

    public void copyFrom(PlayerReviveState other) {
        this.reviveRemainingTicks = other.reviveRemainingTicks;
        this.remainingRevives = other.remainingRevives;
        this.reviveHoldActive = other.reviveHoldActive;
        this.reviveHoldTicks = other.reviveHoldTicks;
        this.reviveHoldTarget = other.reviveHoldTarget;
        this.reviveSpecialStage = other.reviveSpecialStage;
        this.reviveSpecialTicks = other.reviveSpecialTicks;
    }

    public void writeTo(CompoundTag nbt) {
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_REMAINING_TICKS, this.reviveRemainingTicks);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_REMAINING_REVIVES, this.remainingRevives);
        nbt.putBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_HOLD_ACTIVE, this.reviveHoldActive);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_HOLD_TICKS, this.reviveHoldTicks);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_SPECIAL_STAGE, this.reviveSpecialStage);
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_SPECIAL_TICKS, this.reviveSpecialTicks);
        if (this.reviveHoldTarget != null) {
            nbt.putUUID(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_HOLD_TARGET, this.reviveHoldTarget);
        }
    }

    public void readFrom(CompoundTag nbt) {
        this.reviveRemainingTicks = nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_REMAINING_TICKS);
        this.remainingRevives = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_REMAINING_REVIVES, Tag.TAG_INT)
            ? nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_REMAINING_REVIVES)
            : 3;
        this.reviveHoldActive = nbt.getBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_HOLD_ACTIVE);
        this.reviveHoldTicks = nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_HOLD_TICKS);
        this.reviveHoldTarget = nbt.hasUUID(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_HOLD_TARGET)
            ? nbt.getUUID(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_HOLD_TARGET)
            : null;
        this.reviveSpecialStage = nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_SPECIAL_STAGE);
        this.reviveSpecialTicks = nbt.getInt(JjaPlayerStateSchema.NBT_KEY_JJA_REVIVE_SPECIAL_TICKS);
    }

    public void reset() {
        this.reviveRemainingTicks = 0;
        this.remainingRevives = 3;
        this.reviveHoldActive = false;
        this.reviveHoldTicks = 0;
        this.reviveHoldTarget = null;
        this.reviveSpecialStage = 0;
        this.reviveSpecialTicks = 0;
    }
}
