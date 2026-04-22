package com.arf8vhg7.jja.feature.player.state.model;

import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSchema;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class PlayerRctState {
    private double rctHealed;
    private boolean rctOutputEnabled;
    private boolean brainRegenerationEnabled = true;
    private boolean autoRctEnabled;
    private CompoundTag attackTarget = new CompoundTag();

    public double getRctHealed() {
        return this.rctHealed;
    }

    public void setRctHealed(double rctHealed) {
        this.rctHealed = Math.max(0.0, rctHealed);
    }

    public void addRctHealed(double value) {
        if (value > 0.0) {
            this.rctHealed += value;
        }
    }

    public boolean isRctOutputEnabled() {
        return this.rctOutputEnabled;
    }

    public void setRctOutputEnabled(boolean rctOutputEnabled) {
        this.rctOutputEnabled = rctOutputEnabled;
    }

    public boolean isBrainRegenerationEnabled() {
        return this.brainRegenerationEnabled;
    }

    public void setBrainRegenerationEnabled(boolean brainRegenerationEnabled) {
        this.brainRegenerationEnabled = brainRegenerationEnabled;
    }

    public boolean isAutoRctEnabled() {
        return this.autoRctEnabled;
    }

    public void setAutoRctEnabled(boolean autoRctEnabled) {
        this.autoRctEnabled = autoRctEnabled;
    }

    public boolean hasAttackTarget(UUID targetId) {
        return targetId != null && this.attackTarget.contains(targetId.toString(), Tag.TAG_BYTE);
    }

    public boolean toggleAttackTarget(UUID targetId) {
        if (targetId == null) {
            return false;
        }
        String key = targetId.toString();
        if (this.attackTarget.contains(key, Tag.TAG_BYTE)) {
            this.attackTarget.remove(key);
            return false;
        }
        this.attackTarget.putBoolean(key, true);
        return true;
    }

    public List<String> getAttackTargetKeys() {
        return this.attackTarget.getAllKeys().stream().sorted().toList();
    }

    public void copyFrom(PlayerRctState other) {
        this.rctHealed = other.rctHealed;
        this.rctOutputEnabled = other.rctOutputEnabled;
        this.brainRegenerationEnabled = other.brainRegenerationEnabled;
        this.autoRctEnabled = other.autoRctEnabled;
        this.attackTarget = other.attackTarget.copy();
    }

    public void writeTo(CompoundTag nbt) {
        nbt.putDouble(JjaPlayerStateSchema.NBT_KEY_JJA_RCT_HEALED, this.rctHealed);
        nbt.putBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_RCT_OUTPUT_ENABLED, this.rctOutputEnabled);
        nbt.putBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_BRAIN_REGENERATION_ENABLED, this.brainRegenerationEnabled);
        nbt.putBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_AUTO_RCT_ENABLED, this.autoRctEnabled);
        nbt.put(JjaPlayerStateSchema.NBT_KEY_ATTACK_TARGET, this.attackTarget.copy());
    }

    public void readFrom(CompoundTag nbt) {
        this.rctHealed = nbt.getDouble(JjaPlayerStateSchema.NBT_KEY_JJA_RCT_HEALED);
        this.rctOutputEnabled = nbt.getBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_RCT_OUTPUT_ENABLED);
        this.brainRegenerationEnabled = nbt.contains(JjaPlayerStateSchema.NBT_KEY_JJA_BRAIN_REGENERATION_ENABLED, Tag.TAG_BYTE)
            ? nbt.getBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_BRAIN_REGENERATION_ENABLED)
            : true;
        this.autoRctEnabled = nbt.getBoolean(JjaPlayerStateSchema.NBT_KEY_JJA_AUTO_RCT_ENABLED);
        this.attackTarget = nbt.getCompound(JjaPlayerStateSchema.NBT_KEY_ATTACK_TARGET);
    }

    public void reset() {
        this.rctHealed = 0.0;
        this.rctOutputEnabled = false;
        this.brainRegenerationEnabled = true;
        this.autoRctEnabled = false;
        this.attackTarget = new CompoundTag();
    }
}
