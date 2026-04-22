package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

final class MegumiShadowStorageData extends SavedData {
    private static final String DATA_NAME = "jja_megumi_shadow_storage";
    private static final String KEY_OWNERS = "owners";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_ITEMS = "items";
    private static final String KEY_SLOT = "Slot";

    private final Map<UUID, NonNullList<ItemStack>> storageByOwner = new HashMap<>();

    private MegumiShadowStorageData() {
    }

    static MegumiShadowStorageData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
            MegumiShadowStorageData::load,
            MegumiShadowStorageData::new,
            DATA_NAME
        );
    }

    private static MegumiShadowStorageData load(CompoundTag tag) {
        MegumiShadowStorageData data = new MegumiShadowStorageData();
        ListTag owners = tag.getList(KEY_OWNERS, Tag.TAG_COMPOUND);
        for (int ownerIndex = 0; ownerIndex < owners.size(); ownerIndex++) {
            CompoundTag ownerTag = owners.getCompound(ownerIndex);
            UUID ownerId = readOwnerId(ownerTag);
            if (ownerId == null) {
                continue;
            }
            NonNullList<ItemStack> slots = NonNullList.withSize(MegumiShadowRules.MAX_STORAGE_SLOTS, ItemStack.EMPTY);
            ListTag items = ownerTag.getList(KEY_ITEMS, Tag.TAG_COMPOUND);
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                CompoundTag itemTag = items.getCompound(itemIndex);
                int slot = itemTag.getByte(KEY_SLOT) & 255;
                if (slot >= 0 && slot < slots.size()) {
                    slots.set(slot, ItemStack.of(itemTag));
                }
            }
            data.storageByOwner.put(ownerId, slots);
        }
        return data;
    }

    private static UUID readOwnerId(CompoundTag ownerTag) {
        if (ownerTag.hasUUID(KEY_OWNER)) {
            return ownerTag.getUUID(KEY_OWNER);
        }
        String ownerId = ownerTag.getString(KEY_OWNER);
        try {
            return ownerId.isBlank() ? null : UUID.fromString(ownerId);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    ItemStack insert(UUID ownerId, ItemStack stack, int capacity) {
        int effectiveCapacity = Math.min(Math.max(capacity, 0), MegumiShadowRules.MAX_STORAGE_SLOTS);
        if (stack.isEmpty() || effectiveCapacity == 0) {
            return stack.copy();
        }

        NonNullList<ItemStack> slots = this.slots(ownerId);
        ItemStack remaining = stack.copy();
        boolean changed = mergeIntoExistingSlots(slots, remaining, effectiveCapacity);
        changed |= moveIntoEmptySlots(slots, remaining, effectiveCapacity);
        if (changed) {
            this.setDirty();
        }
        return remaining.isEmpty() ? ItemStack.EMPTY : remaining;
    }

    ItemStack getSlot(UUID ownerId, int slot) {
        if (!isValidSlot(slot)) {
            return ItemStack.EMPTY;
        }
        return this.slots(ownerId).get(slot);
    }

    void setSlot(UUID ownerId, int slot, ItemStack stack) {
        if (!isValidSlot(slot)) {
            return;
        }
        this.slots(ownerId).set(slot, stack);
        this.setDirty();
    }

    ItemStack removeItem(UUID ownerId, int slot, int amount) {
        if (!isValidSlot(slot) || amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = ContainerHelper.removeItem(this.slots(ownerId), slot, amount);
        if (!removed.isEmpty()) {
            this.setDirty();
        }
        return removed;
    }

    ItemStack removeItemNoUpdate(UUID ownerId, int slot) {
        if (!isValidSlot(slot)) {
            return ItemStack.EMPTY;
        }
        NonNullList<ItemStack> slots = this.slots(ownerId);
        ItemStack removed = slots.get(slot);
        slots.set(slot, ItemStack.EMPTY);
        if (!removed.isEmpty()) {
            this.setDirty();
        }
        return removed;
    }

    int highestOccupiedSlot(UUID ownerId) {
        NonNullList<ItemStack> slots = this.storageByOwner.get(ownerId);
        if (slots == null) {
            return -1;
        }
        for (int slot = slots.size() - 1; slot >= 0; slot--) {
            if (!slots.get(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    @Override
    public @Nonnull CompoundTag save(@Nonnull CompoundTag tag) {
        ListTag owners = new ListTag();
        for (Map.Entry<UUID, NonNullList<ItemStack>> entry : this.storageByOwner.entrySet()) {
            CompoundTag ownerTag = new CompoundTag();
            ownerTag.putUUID(KEY_OWNER, entry.getKey());
            ownerTag.put(KEY_ITEMS, saveItems(entry.getValue()));
            owners.add(ownerTag);
        }
        tag.put(KEY_OWNERS, owners);
        return tag;
    }

    private static ListTag saveItems(NonNullList<ItemStack> slots) {
        ListTag items = new ListTag();
        for (int slot = 0; slot < slots.size(); slot++) {
            ItemStack stack = slots.get(slot);
            if (stack.isEmpty()) {
                continue;
            }
            CompoundTag itemTag = stack.save(new CompoundTag());
            itemTag.putByte(KEY_SLOT, (byte) slot);
            items.add(itemTag);
        }
        return items;
    }

    private static boolean mergeIntoExistingSlots(NonNullList<ItemStack> slots, ItemStack remaining, int capacity) {
        boolean changed = false;
        for (int slot = 0; slot < capacity && !remaining.isEmpty(); slot++) {
            ItemStack existing = slots.get(slot);
            if (existing.isEmpty() || !ItemStack.isSameItemSameTags(existing, remaining)) {
                continue;
            }
            int moved = Math.min(remaining.getCount(), Math.min(existing.getMaxStackSize(), 64) - existing.getCount());
            if (moved <= 0) {
                continue;
            }
            existing.grow(moved);
            remaining.shrink(moved);
            changed = true;
        }
        return changed;
    }

    private static boolean moveIntoEmptySlots(NonNullList<ItemStack> slots, ItemStack remaining, int capacity) {
        boolean changed = false;
        for (int slot = 0; slot < capacity && !remaining.isEmpty(); slot++) {
            if (!slots.get(slot).isEmpty()) {
                continue;
            }
            int moved = Math.min(remaining.getCount(), Math.min(remaining.getMaxStackSize(), 64));
            slots.set(slot, remaining.split(moved));
            changed = true;
        }
        return changed;
    }

    private NonNullList<ItemStack> slots(UUID ownerId) {
        return this.storageByOwner.computeIfAbsent(
            ownerId,
            ignored -> NonNullList.withSize(MegumiShadowRules.MAX_STORAGE_SLOTS, ItemStack.EMPTY)
        );
    }

    private static boolean isValidSlot(int slot) {
        return slot >= 0 && slot < MegumiShadowRules.MAX_STORAGE_SLOTS;
    }
}
