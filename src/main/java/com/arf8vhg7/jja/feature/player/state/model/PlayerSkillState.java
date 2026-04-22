package com.arf8vhg7.jja.feature.player.state.model;

import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSchema;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class PlayerSkillState {
    private static final int SLOT_COUNT = 10;

    private int pressedSlot;
    private boolean pressSkillRegistrationToggle;
    private final List<CompoundTag> registeredCurseTechniqueMaps = createSlots();
    private final List<CompoundTag> registeredCurseTechniqueNameMaps = createSlots();
    private CompoundTag hiddenCurseTechniqueNameMap = new CompoundTag();

    public int getPressedSlot() {
        return this.pressedSlot;
    }

    public void setPressedSlot(int pressedSlot) {
        this.pressedSlot = pressedSlot;
    }

    public boolean isPressSkillRegistrationToggle() {
        return this.pressSkillRegistrationToggle;
    }

    public void setPressSkillRegistrationToggle(boolean pressSkillRegistrationToggle) {
        this.pressSkillRegistrationToggle = pressSkillRegistrationToggle;
    }

    public boolean hasRegisteredCurseTechnique(int slot, int ctId) {
        return getRegisteredCurseTechniqueMap(slot).contains(String.valueOf(ctId), Tag.TAG_INT);
    }

    public int getRegisteredCurseTechnique(int slot, int ctId) {
        return getRegisteredCurseTechniqueMap(slot).getInt(String.valueOf(ctId));
    }

    public String getRegisteredCurseTechniqueName(int slot, int ctId) {
        return getRegisteredCurseTechniqueNameMap(slot).getString(String.valueOf(ctId));
    }

    public boolean hasRegisteredCurseTechniqueName(int ctId, String name) {
        if (ctId == 0 || name == null || name.isEmpty()) {
            return false;
        }
        for (int slot = 1; slot <= SLOT_COUNT; slot++) {
            if (!hasRegisteredCurseTechnique(slot, ctId)) {
                continue;
            }
            if (name.equals(getRegisteredCurseTechniqueName(slot, ctId))) {
                return true;
            }
        }
        return false;
    }

    public void setRegisteredCurseTechnique(int slot, int ctId, int value) {
        if (isValidSlot(slot)) {
            getRegisteredCurseTechniqueMap(slot).putInt(String.valueOf(ctId), value);
        }
    }

    public void clearRegisteredCurseTechnique(int slot, int ctId) {
        if (isValidSlot(slot)) {
            getRegisteredCurseTechniqueMap(slot).remove(String.valueOf(ctId));
        }
    }

    public void setRegisteredCurseTechniqueName(int slot, int ctId, String name) {
        if (isValidSlot(slot)) {
            getRegisteredCurseTechniqueNameMap(slot).putString(String.valueOf(ctId), name);
        }
    }

    public void clearRegisteredCurseTechniqueName(int slot, int ctId) {
        if (isValidSlot(slot)) {
            getRegisteredCurseTechniqueNameMap(slot).remove(String.valueOf(ctId));
        }
    }

    public boolean isHiddenCurseTechniqueName(int ctId, String name) {
        if (ctId == 0 || name == null || name.isEmpty()) {
            return false;
        }
        return getHiddenCurseTechniqueNameMap(ctId).contains(name, Tag.TAG_BYTE);
    }

    public void setHiddenCurseTechniqueName(int ctId, String name, boolean hidden) {
        if (ctId == 0 || name == null || name.isEmpty()) {
            return;
        }
        String ctKey = String.valueOf(ctId);
        CompoundTag hiddenNames = getHiddenCurseTechniqueNameMap(ctId);
        if (hidden) {
            hiddenNames.putBoolean(name, true);
            this.hiddenCurseTechniqueNameMap.put(ctKey, hiddenNames);
            return;
        }
        hiddenNames.remove(name);
        if (hiddenNames.getAllKeys().isEmpty()) {
            this.hiddenCurseTechniqueNameMap.remove(ctKey);
            return;
        }
        this.hiddenCurseTechniqueNameMap.put(ctKey, hiddenNames);
    }

    public boolean toggleHiddenCurseTechniqueName(int ctId, String name) {
        boolean hidden = !isHiddenCurseTechniqueName(ctId, name);
        setHiddenCurseTechniqueName(ctId, name, hidden);
        return hidden;
    }

    public void copyRegisteredMapsFrom(PlayerSkillState other) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            this.registeredCurseTechniqueMaps.set(i, other.registeredCurseTechniqueMaps.get(i).copy());
            this.registeredCurseTechniqueNameMaps.set(i, other.registeredCurseTechniqueNameMaps.get(i).copy());
        }
    }

    public void copyHiddenCurseTechniqueNamesFrom(PlayerSkillState other) {
        this.hiddenCurseTechniqueNameMap = other.hiddenCurseTechniqueNameMap.copy();
    }

    public void writeTo(CompoundTag nbt) {
        nbt.putInt(JjaPlayerStateSchema.NBT_KEY_PRESSED_SLOT, this.pressedSlot);
        nbt.putBoolean(JjaPlayerStateSchema.NBT_KEY_PRESS_SKILL_REGISTRATION_TOGGLE, this.pressSkillRegistrationToggle);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int slot = i + 1;
            nbt.put(
                JjaPlayerStateSchema.NBT_KEY_RESISTERED_CURSE_TECHNIQUE_MAP_PREFIX + slot,
                this.registeredCurseTechniqueMaps.get(i).copy()
            );
            nbt.put(
                JjaPlayerStateSchema.NBT_KEY_RESISTERED_CURSE_TECHNIQUE_NAME_MAP_PREFIX + slot,
                this.registeredCurseTechniqueNameMaps.get(i).copy()
            );
        }
        nbt.put(JjaPlayerStateSchema.NBT_KEY_JJA_HIDDEN_CURSE_TECHNIQUE_NAME_MAP, this.hiddenCurseTechniqueNameMap.copy());
    }

    public void readFrom(CompoundTag nbt) {
        this.pressedSlot = nbt.getInt(JjaPlayerStateSchema.NBT_KEY_PRESSED_SLOT);
        this.pressSkillRegistrationToggle = nbt.getBoolean(JjaPlayerStateSchema.NBT_KEY_PRESS_SKILL_REGISTRATION_TOGGLE);
        for (int i = 0; i < SLOT_COUNT; i++) {
            int slot = i + 1;
            this.registeredCurseTechniqueMaps.set(i, nbt.getCompound(JjaPlayerStateSchema.NBT_KEY_RESISTERED_CURSE_TECHNIQUE_MAP_PREFIX + slot));
            this.registeredCurseTechniqueNameMaps.set(
                i,
                nbt.getCompound(JjaPlayerStateSchema.NBT_KEY_RESISTERED_CURSE_TECHNIQUE_NAME_MAP_PREFIX + slot)
            );
        }
        this.hiddenCurseTechniqueNameMap = nbt.getCompound(JjaPlayerStateSchema.NBT_KEY_JJA_HIDDEN_CURSE_TECHNIQUE_NAME_MAP);
    }

    public void reset() {
        this.pressedSlot = 0;
        this.pressSkillRegistrationToggle = false;
        resetSlots(this.registeredCurseTechniqueMaps);
        resetSlots(this.registeredCurseTechniqueNameMaps);
        this.hiddenCurseTechniqueNameMap = new CompoundTag();
    }

    private CompoundTag getRegisteredCurseTechniqueMap(int slot) {
        return isValidSlot(slot) ? this.registeredCurseTechniqueMaps.get(slot - 1) : new CompoundTag();
    }

    private CompoundTag getRegisteredCurseTechniqueNameMap(int slot) {
        return isValidSlot(slot) ? this.registeredCurseTechniqueNameMaps.get(slot - 1) : new CompoundTag();
    }

    private CompoundTag getHiddenCurseTechniqueNameMap(int ctId) {
        return this.hiddenCurseTechniqueNameMap.getCompound(String.valueOf(ctId));
    }

    private static List<CompoundTag> createSlots() {
        ArrayList<CompoundTag> slots = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            slots.add(new CompoundTag());
        }
        return slots;
    }

    private static void resetSlots(List<CompoundTag> slots) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            slots.set(i, new CompoundTag());
        }
    }

    private static boolean isValidSlot(int slot) {
        return slot >= 1 && slot <= SLOT_COUNT;
    }
}
