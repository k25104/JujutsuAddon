package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

final class MegumiShadowStorageContainer implements Container {
    private final MegumiShadowStorageData data;
    private final UUID ownerId;
    private final int activeSlots;
    private final BlockPos sourcePos;

    MegumiShadowStorageContainer(MegumiShadowStorageData data, UUID ownerId, int activeSlots, BlockPos sourcePos) {
        this.data = data;
        this.ownerId = ownerId;
        this.activeSlots = Math.min(Math.max(activeSlots, 0), MegumiShadowRules.MAX_STORAGE_SLOTS);
        this.sourcePos = sourcePos.immutable();
    }

    @Override
    public int getContainerSize() {
        return this.activeSlots;
    }

    @Override
    public boolean isEmpty() {
        for (int slot = 0; slot < this.activeSlots; slot++) {
            if (!this.getItem(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (!this.isVisibleSlot(slot)) {
            return ItemStack.EMPTY;
        }
        return this.data.getSlot(this.ownerId, slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (!this.isVisibleSlot(slot)) {
            return ItemStack.EMPTY;
        }
        return this.data.removeItem(this.ownerId, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (!this.isVisibleSlot(slot)) {
            return ItemStack.EMPTY;
        }
        return this.data.removeItemNoUpdate(this.ownerId, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (!this.isVisibleSlot(slot)) {
            return;
        }
        if (!stack.isEmpty() && !this.canPlaceItem(slot, stack)) {
            return;
        }
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.data.setSlot(this.ownerId, slot, stack);
    }

    @Override
    public void setChanged() {
        this.data.setDirty();
    }

    @Override
    public boolean stillValid(Player player) {
        return player != null && this.ownerId.equals(player.getUUID());
    }

    @Override
    public void startOpen(Player player) {
        MegumiShadowService.onStorageOpened(player, this.sourcePos);
    }

    @Override
    public void stopOpen(Player player) {
        MegumiShadowService.onStorageClosed(player, this.sourcePos);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return this.isVisibleSlot(slot);
    }

    @Override
    public void clearContent() {
        for (int slot = 0; slot < this.activeSlots; slot++) {
            this.data.setSlot(this.ownerId, slot, ItemStack.EMPTY);
        }
    }

    private boolean isVisibleSlot(int slot) {
        return slot >= 0 && slot < this.activeSlots;
    }
}
