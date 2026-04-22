package com.arf8vhg7.jja.feature.player.revive.client;

import com.arf8vhg7.jja.feature.player.revive.JjaReviveSpecialStage;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class JjaReviveUiRenderer {
    public static final int BUTTON_WIDTH = 200;
    public static final int BUTTON_HEIGHT = 20;
    private static final int TITLE_Y = 30;
    private static final int PRIMARY_SLOT_Y_OFFSET = 72;
    private static final int SECONDARY_SLOT_Y_OFFSET = 96;
    private static final int TEXT_COLOR = 0xFFFFFF;

    private JjaReviveUiRenderer() {
    }

    public static int getPrimarySlotY(int height) {
        return height / 4 + PRIMARY_SLOT_Y_OFFSET;
    }

    public static int getSecondarySlotY(int height) {
        return height / 4 + SECONDARY_SLOT_Y_OFFSET;
    }

    public static int getPrimaryButtonX(int width) {
        return width / 2 - BUTTON_WIDTH / 2;
    }

    public static int getSpecialButtonY(int height) {
        return height / 2 - BUTTON_HEIGHT / 2;
    }

    public static void renderBackground(GuiGraphics guiGraphics, int width, int height) {
        guiGraphics.fillGradient(0, 0, width, height, 1615855616, -1602211792);
    }

    public static void renderTitle(GuiGraphics guiGraphics, Font font, int width) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(2.0F, 2.0F, 2.0F);
        guiGraphics.drawCenteredString(font, Component.translatable("screen.jja.revive_title"), width / 4, TITLE_Y, TEXT_COLOR);
        guiGraphics.pose().popPose();
    }

    public static void renderHiddenOverlay(GuiGraphics guiGraphics, Font font, int width, int height, int remainingTicks, Component returnKeyText) {
        renderBackground(guiGraphics, width, height);
        renderTitle(guiGraphics, font, width);
        guiGraphics.drawCenteredString(font, getRemainingText(remainingTicks), width / 2, getPrimarySlotY(height), TEXT_COLOR);
        guiGraphics.drawCenteredString(font, returnKeyText, width / 2, getSecondarySlotY(height), TEXT_COLOR);
    }

    public static void renderRemainingRevives(GuiGraphics guiGraphics, Font font, int width, int height, int remainingRevives) {
        guiGraphics.drawCenteredString(font, getRemainingRevivesText(remainingRevives), width / 2, getSecondarySlotY(height), TEXT_COLOR);
    }

    public static Component getRemainingText(int remainingTicks) {
        return Component.translatable("screen.jja.revive_remaining", JjaReviveClientState.toDisplaySeconds(remainingTicks));
    }

    public static Component getRemainingRevivesText(int remainingRevives) {
        return Component.translatable("screen.jja.revive_remaining_revives", remainingRevives);
    }

    public static Component getSpecialButtonText(JjaReviveSpecialStage specialStage) {
        return switch (specialStage) {
            case ELLIPSIS -> Component.translatable("screen.jja.revive_special_ellipsis");
            case GRASPED -> Component.translatable("screen.jja.revive_special_grasped");
            case ESSENCE_READY, ESSENCE_TRIGGERED -> Component.translatable("screen.jja.revive_special_essence");
            case NONE -> Component.empty();
        };
    }
}
