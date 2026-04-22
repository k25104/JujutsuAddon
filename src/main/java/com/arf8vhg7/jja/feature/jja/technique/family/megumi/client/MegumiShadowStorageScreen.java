package com.arf8vhg7.jja.feature.jja.technique.family.megumi.client;

import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

final class MegumiShadowStorageScreen extends AbstractContainerScreen<MegumiShadowStorageMenu> {
    private static final ResourceLocation CONTAINER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "minecraft",
        "textures/gui/container/generic_54.png"
    );
    private static final int SLOT_TEXTURE_U = 7;
    private static final int SLOT_TEXTURE_V = 17;
    private static final int DISABLED_SLOT_OVERLAY = 0x88000000;

    MegumiShadowStorageScreen(MegumiShadowStorageMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 114 + menu.rows() * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int upperHeight = 17 + this.menu.rows() * 18;
        guiGraphics.blit(CONTAINER_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, upperHeight);
        guiGraphics.blit(CONTAINER_TEXTURE, this.leftPos, this.topPos + upperHeight, 0, 126, this.imageWidth, 96);
        renderDisabledSlots(guiGraphics);
    }

    private void renderDisabledSlots(GuiGraphics guiGraphics) {
        for (int slot = this.menu.activeSlots(); slot < this.menu.rows() * 9; slot++) {
            int column = slot % 9;
            int row = slot / 9;
            int left = this.leftPos + 7 + column * 18;
            int top = this.topPos + 17 + row * 18;
            guiGraphics.blit(CONTAINER_TEXTURE, left, top, SLOT_TEXTURE_U, SLOT_TEXTURE_V, 18, 18);
            guiGraphics.fill(left + 1, top + 1, left + 17, top + 17, DISABLED_SLOT_OVERLAY);
        }
    }
}
