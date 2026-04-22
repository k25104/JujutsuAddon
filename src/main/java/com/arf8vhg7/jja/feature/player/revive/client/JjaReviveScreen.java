package com.arf8vhg7.jja.feature.player.revive.client;

import com.arf8vhg7.jja.feature.player.revive.JjaReviveSpecialStage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class JjaReviveScreen extends Screen {
    private Button primaryButton;

    public JjaReviveScreen() {
        super(Component.translatable("screen.jja.revive_title"));
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.primaryButton = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> JjaReviveClientEvents.handlePrimaryButtonPress())
                .bounds(
                    JjaReviveUiRenderer.getPrimaryButtonX(this.width),
                    getPrimaryButtonY(),
                    JjaReviveUiRenderer.BUTTON_WIDTH,
                    JjaReviveUiRenderer.BUTTON_HEIGHT
                )
                .build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        JjaReviveUiRenderer.renderBackground(guiGraphics, this.width, this.height);
        if (!JjaReviveClientState.isSpecialBranchActive()) {
            JjaReviveUiRenderer.renderTitle(guiGraphics, this.font, this.width);
            JjaReviveUiRenderer.renderRemainingRevives(guiGraphics, this.font, this.width, this.height, JjaReviveClientState.getRemainingRevives());
        }
        updatePrimaryButton(mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Minecraft minecraft = this.minecraft;
        if (minecraft == null) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == 256) {
            JjaReviveClientState.hideToBackground(minecraft);
            return true;
        }
        if (minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getPrimaryButtonY() {
        return JjaReviveClientState.isSpecialBranchActive()
            ? JjaReviveUiRenderer.getSpecialButtonY(this.height)
            : JjaReviveUiRenderer.getPrimarySlotY(this.height);
    }

    private void updatePrimaryButton(int mouseX, int mouseY) {
        if (this.primaryButton == null) {
            return;
        }
        this.primaryButton.setX(JjaReviveUiRenderer.getPrimaryButtonX(this.width));
        this.primaryButton.setY(getPrimaryButtonY());
        JjaReviveSpecialStage specialStage = JjaReviveClientState.getSpecialStage();
        if (specialStage.isActive()) {
            this.primaryButton.active = specialStage == JjaReviveSpecialStage.ESSENCE_READY;
            this.primaryButton.setMessage(JjaReviveUiRenderer.getSpecialButtonText(specialStage));
            return;
        }
        this.primaryButton.active = true;
        Component message = this.primaryButton.isMouseOver(mouseX, mouseY)
            ? Component.translatable("screen.jja.revive_give_up")
            : JjaReviveUiRenderer.getRemainingText(JjaReviveClientState.getRemainingTicks());
        this.primaryButton.setMessage(message);
    }
}
