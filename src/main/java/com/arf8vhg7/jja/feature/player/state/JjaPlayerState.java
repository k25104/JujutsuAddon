package com.arf8vhg7.jja.feature.player.state;

import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import com.arf8vhg7.jja.feature.player.state.model.PlayerReviveState;
import com.arf8vhg7.jja.feature.player.state.model.PlayerSkillState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class JjaPlayerState {
    private final PlayerSkillState skillState = new PlayerSkillState();
    private final PlayerReviveState reviveState = new PlayerReviveState();
    private final PlayerRctState rctState = new PlayerRctState();
    private final PlayerAddonStatsState addonStatsState = new PlayerAddonStatsState();

    public PlayerSkillState skillState() {
        return this.skillState;
    }

    public PlayerReviveState reviveState() {
        return this.reviveState;
    }

    public PlayerRctState rctState() {
        return this.rctState;
    }

    public PlayerAddonStatsState addonStatsState() {
        return this.addonStatsState;
    }

    public void copyForCloneFrom(JjaPlayerState other, boolean wasDeath) {
        this.skillState.copyRegisteredMapsFrom(other.skillState);
        this.skillState.copyHiddenCurseTechniqueNamesFrom(other.skillState);
        this.rctState.copyFrom(other.rctState);
        this.addonStatsState.copyCommandStateFrom(other.addonStatsState);
        this.addonStatsState.copyShikigamiStateFrom(other.addonStatsState);
        this.addonStatsState.copySdStateFrom(other.addonStatsState);
        this.addonStatsState.copyProgressionStateFrom(other.addonStatsState);
        this.skillState.setPressedSlot(0);
        this.skillState.setPressSkillRegistrationToggle(other.skillState.isPressSkillRegistrationToggle());

        if (wasDeath) {
            this.addonStatsState.setCursePowerPreservation(0.0D);
            this.reviveState.reset();
            return;
        }

        this.addonStatsState.setCursePowerPreservation(other.addonStatsState.getCursePowerPreservation());
        this.reviveState.copyFrom(other.reviveState);
    }

    public CompoundTag writeNBT() {
        CompoundTag nbt = new CompoundTag();
        this.addonStatsState.writeTo(nbt);
        this.skillState.writeTo(nbt);
        this.reviveState.writeTo(nbt);
        this.rctState.writeTo(nbt);
        return nbt;
    }

    public void readNBT(Tag tag) {
        if (tag instanceof CompoundTag nbt) {
            this.addonStatsState.readFrom(nbt);
            this.skillState.readFrom(nbt);
            this.reviveState.readFrom(nbt);
            this.rctState.readFrom(nbt);
            return;
        }
        reset();
    }

    public void reset() {
        this.addonStatsState.reset();
        this.skillState.reset();
        this.reviveState.reset();
        this.rctState.reset();
    }
}
