package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class MegumiShadowStorageMenu extends AbstractContainerMenu {
    private static final int SLOTS_PER_ROW = 9;

    private final Container container;
    private final @Nullable MegumiShadowStorageContainer shadowContainer;
    private final int rows;
    private final int activeSlots;

    MegumiShadowStorageMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, readClientInit(buffer));
    }

    MegumiShadowStorageMenu(int id, Inventory inventory, MegumiShadowStorageContainer container, int rows, int activeSlots) {
        this(id, inventory, container, container, rows, activeSlots, true);
    }

    private MegumiShadowStorageMenu(int id, Inventory inventory, ClientInit init) {
        this(id, inventory, init.container(), null, init.rows(), init.activeSlots(), false);
    }

    private MegumiShadowStorageMenu(
        int id,
        Inventory inventory,
        Container container,
        @Nullable MegumiShadowStorageContainer shadowContainer,
        int rows,
        int activeSlots,
        boolean shouldStartOpen
    ) {
        super(MegumiShadowMenus.SHADOW_STORAGE.get(), id);
        this.container = container;
        this.shadowContainer = shadowContainer;
        this.rows = Mth.clamp(rows, 1, 6);
        this.activeSlots = Mth.clamp(activeSlots, 0, Math.min(container.getContainerSize(), this.rows * SLOTS_PER_ROW));
        if (shouldStartOpen && shadowContainer != null) {
            shadowContainer.startOpen(inventory.player);
        }

        int inventoryYOffset = (this.rows - 4) * 18;
        for (int slot = 0; slot < this.activeSlots; slot++) {
            int row = slot / SLOTS_PER_ROW;
            int column = slot % SLOTS_PER_ROW;
            this.addSlot(new Slot(container, slot, 8 + column * 18, 18 + row * 18));
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < SLOTS_PER_ROW; column++) {
                this.addSlot(new Slot(inventory, column + row * SLOTS_PER_ROW + SLOTS_PER_ROW, 8 + column * 18, 103 + row * 18 + inventoryYOffset));
            }
        }

        for (int column = 0; column < SLOTS_PER_ROW; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 161 + inventoryYOffset));
        }
    }

    public int rows() {
        return this.rows;
    }

    public int activeSlots() {
        return this.activeSlots;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack moved = ItemStack.EMPTY;
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            return moved;
        }
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) {
            return moved;
        }

        ItemStack stack = slot.getItem();
        moved = stack.copy();
        if (slotIndex < this.activeSlots) {
            if (!this.moveItemStackTo(stack, this.activeSlots, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, this.activeSlots, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return moved;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (this.shadowContainer != null) {
            this.shadowContainer.stopOpen(player);
        }
    }

    private static ClientInit readClientInit(FriendlyByteBuf buffer) {
        int rows = Mth.clamp(buffer.readVarInt(), 1, 6);
        int activeSlots = Mth.clamp(buffer.readVarInt(), 0, rows * SLOTS_PER_ROW);
        return new ClientInit(new SimpleContainer(activeSlots), rows, activeSlots);
    }

    private record ClientInit(Container container, int rows, int activeSlots) {
    }
}
