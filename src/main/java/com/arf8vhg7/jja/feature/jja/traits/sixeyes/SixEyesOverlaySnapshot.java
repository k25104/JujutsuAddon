package com.arf8vhg7.jja.feature.jja.traits.sixeyes;

import java.util.List;
import net.minecraft.network.chat.Component;

public record SixEyesOverlaySnapshot(
    Component title,
    Component targetName,
    List<SixEyesOverlayLine> lines,
    int fadeTicksRemaining,
    int fadeTicksMaximum,
    int accentColor
) {
    public SixEyesOverlaySnapshot {
        lines = List.copyOf(lines);
    }

    public SixEyesOverlaySnapshot withFadeTicksRemaining(int updatedFadeTicksRemaining) {
        return new SixEyesOverlaySnapshot(title, targetName, lines, updatedFadeTicksRemaining, fadeTicksMaximum, accentColor);
    }

    public float fadeAlpha() {
        if (fadeTicksMaximum <= 0) {
            return 1.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, fadeTicksRemaining / (float) fadeTicksMaximum));
    }
}