package com.arf8vhg7.jja.feature.jja.traits.sixeyes.client;

import com.arf8vhg7.jja.feature.jja.traits.sixeyes.SixEyesOverlayLine;
import com.arf8vhg7.jja.feature.jja.traits.sixeyes.SixEyesOverlaySnapshot;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public final class SixEyesOverlayRenderer {
    private static final int PADDING = 8;
    private static final int LABEL_VALUE_GAP = 4;
    private static final int ROW_GAP = 2;
    private static final int PANEL_BACKGROUND = 0xB0141820;
    private static final int PANEL_BORDER = 0xFF27313E;
    private static final int TITLE_COLOR = 0xFFFFFFFF;
    private static final int TARGET_COLOR = 0xFFE2E8F0;
    private static final int LABEL_COLOR = 0xFF94A3B8;

    private SixEyesOverlayRenderer() {
    }

    public static void render(Minecraft minecraft, GuiGraphics guiGraphics, int screenWidth, int screenHeight, SixEyesOverlaySnapshot snapshot) {
        if (snapshot == null) {
            return;
        }

        Font font = minecraft.font;
        float alpha = snapshot.fadeAlpha();
        int titleColor = applyAlpha(TITLE_COLOR, alpha);
        int targetColor = applyAlpha(TARGET_COLOR, alpha);
        int labelColor = applyAlpha(LABEL_COLOR, alpha);
        int panelBackground = applyAlpha(PANEL_BACKGROUND, alpha);
        int panelBorder = applyAlpha(PANEL_BORDER, alpha);
        int accentColor = applyAlpha(snapshot.accentColor(), alpha);

        int maxWidth = Math.max(font.width(Objects.requireNonNull(snapshot.title().getString())), font.width(Objects.requireNonNull(snapshot.targetName().getString())));
        List<SixEyesOverlayLine> lines = snapshot.lines();
        for (SixEyesOverlayLine line : lines) {
            int lineWidth = font.width(Objects.requireNonNull(line.label().getString())) + LABEL_VALUE_GAP + font.width(Objects.requireNonNull(line.value().getString()));
            maxWidth = Math.max(maxWidth, lineWidth);
        }

        int panelWidth = Math.min(Math.max(maxWidth + PADDING * 2, 160), Math.max(screenWidth - 16, 160));
        int rowHeight = font.lineHeight + ROW_GAP;
        int panelHeight = PADDING * 3 + font.lineHeight * 2 + lines.size() * rowHeight;
        int left = 8;
        int top = 8;
        if (top + panelHeight > screenHeight - 8) {
            top = Math.max(8, screenHeight - 8 - panelHeight);
        }
        int right = left + panelWidth;
        int bottom = top + panelHeight;

        guiGraphics.fill(left, top, right, bottom, panelBackground);
        guiGraphics.fill(left, top, right, top + 1, accentColor);
        guiGraphics.fill(left, bottom - 1, right, bottom, panelBorder);
        guiGraphics.fill(left, top, left + 1, bottom, panelBorder);
        guiGraphics.fill(right - 1, top, right, bottom, panelBorder);

        int textLeft = left + PADDING;
        int cursorY = top + PADDING;
        guiGraphics.drawString(font, Objects.requireNonNull(snapshot.title().getString()), textLeft, cursorY, titleColor, false);
        cursorY += font.lineHeight + 2;
        guiGraphics.drawString(font, Objects.requireNonNull(snapshot.targetName().getString()), textLeft, cursorY, targetColor, false);
        cursorY += font.lineHeight + 4;

        for (SixEyesOverlayLine line : lines) {
            String label = Objects.requireNonNull(line.label().getString());
            String value = Objects.requireNonNull(line.value().getString());
            guiGraphics.drawString(font, label, textLeft, cursorY, labelColor, false);
            int valueX = textLeft + font.width(label) + LABEL_VALUE_GAP;
            guiGraphics.drawString(font, value, valueX, cursorY, line.valueColor(), false);
            cursorY += rowHeight;
        }
    }

    private static int applyAlpha(int color, float alpha) {
        int a = Math.round(((color >>> 24) & 0xFF) * alpha);
        if ((color >>> 24) == 0) {
            a = Math.round(255.0F * alpha);
        }
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}