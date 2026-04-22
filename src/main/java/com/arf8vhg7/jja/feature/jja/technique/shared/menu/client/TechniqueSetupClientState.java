package com.arf8vhg7.jja.feature.jja.technique.shared.menu.client;

import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupViewState;
import net.minecraft.client.Minecraft;

public final class TechniqueSetupClientState {
    private static TechniqueSetupViewState latestState = new TechniqueSetupViewState(0, 0, 0, 0, 0, 0, 0, java.util.List.of());

    private TechniqueSetupClientState() {
    }

    public static void apply(boolean openScreen, TechniqueSetupViewState viewState) {
        latestState = viewState;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof TechniqueSetupScreen screen) {
            screen.applyViewState(viewState);
            return;
        }
        if (openScreen) {
            minecraft.setScreen(new TechniqueSetupScreen(viewState));
        }
    }

    public static TechniqueSetupViewState getLatestState() {
        return latestState;
    }
}
