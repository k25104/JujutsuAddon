package com.arf8vhg7.jja.feature.player.revive.client;

import com.arf8vhg7.jja.feature.player.revive.JjaReviveSpecialStage;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class JjaReviveClientState {
    private static boolean waiting;
    private static int remainingTicks;
    private static int remainingRevives;
    private static JjaReviveSpecialStage specialStage = JjaReviveSpecialStage.NONE;
    private static boolean hiddenBackgroundOnly;
    private static boolean assistActive;
    private static int assistRemainingTicks;
    private static UUID assistTargetId;
    private static boolean rescueButtonHeld;
    private static UUID sentHoldTarget;

    private JjaReviveClientState() {
    }

    public static void applyWaitingState(boolean waiting, int remainingTicks, int remainingRevives, int specialStageId) {
        boolean wasWaiting = JjaReviveClientState.waiting;
        JjaReviveClientState.waiting = waiting;
        JjaReviveClientState.remainingTicks = Math.max(0, remainingTicks);
        JjaReviveClientState.remainingRevives = remainingRevives;
        JjaReviveClientState.specialStage = waiting ? JjaReviveSpecialStage.fromId(specialStageId) : JjaReviveSpecialStage.NONE;
        if (waiting && !wasWaiting) {
            hiddenBackgroundOnly = false;
        }
        if (!waiting || shouldKeepSpecialBranchVisible()) {
            hiddenBackgroundOnly = false;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!waiting) {
            closeReviveScreen(minecraft);
            return;
        }
        if (!hiddenBackgroundOnly) {
            ensureWaitingScreen(minecraft);
        }
    }

    public static void applyAssistState(boolean active, int remainingTicks, UUID targetId) {
        assistActive = active;
        assistRemainingTicks = Math.max(0, remainingTicks);
        assistTargetId = active ? targetId : null;
    }

    public static void tick(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.level == null) {
            waiting = false;
            remainingTicks = 0;
            remainingRevives = 0;
            specialStage = JjaReviveSpecialStage.NONE;
            hiddenBackgroundOnly = false;
            assistActive = false;
            assistRemainingTicks = 0;
            assistTargetId = null;
            rescueButtonHeld = false;
            sentHoldTarget = null;
            return;
        }
        if (waiting && remainingTicks > 0) {
            remainingTicks--;
        }
        if (waiting && shouldKeepSpecialBranchVisible()) {
            hiddenBackgroundOnly = false;
        }
        if (assistActive && assistRemainingTicks > 0) {
            assistRemainingTicks--;
        }
        if (waiting && hiddenBackgroundOnly && minecraft.screen == null) {
            while (minecraft.options.keyInventory.consumeClick()) {
                reopenScreen(minecraft);
            }
        }
        if (waiting && !hiddenBackgroundOnly) {
            ensureWaitingScreen(minecraft);
        } else if (!waiting) {
            closeReviveScreen(minecraft);
        }
    }

    public static boolean isWaiting() {
        return waiting;
    }

    public static int getRemainingTicks() {
        return remainingTicks;
    }

    public static int getRemainingRevives() {
        return remainingRevives;
    }

    public static JjaReviveSpecialStage getSpecialStage() {
        return specialStage;
    }

    public static boolean isSpecialBranchActive() {
        return specialStage.isActive();
    }

    public static boolean isHiddenBackgroundOnly() {
        return hiddenBackgroundOnly;
    }

    public static boolean isAssistActive() {
        return assistActive;
    }

    public static int getAssistRemainingTicks() {
        return assistRemainingTicks;
    }

    public static UUID getAssistTargetId() {
        return assistTargetId;
    }

    public static boolean isRescueButtonHeld() {
        return rescueButtonHeld;
    }

    public static void setRescueButtonHeld(boolean rescueButtonHeld) {
        JjaReviveClientState.rescueButtonHeld = rescueButtonHeld;
    }

    public static UUID getSentHoldTarget() {
        return sentHoldTarget;
    }

    public static void setSentHoldTarget(UUID sentHoldTarget) {
        JjaReviveClientState.sentHoldTarget = sentHoldTarget;
    }

    public static void hideToBackground(Minecraft minecraft) {
        if (isSpecialBranchActive() && !canHideSpecialBranch()) {
            return;
        }
        hiddenBackgroundOnly = true;
        if (minecraft.screen instanceof JjaReviveScreen) {
            minecraft.setScreen(null);
        }
    }

    public static void reopenScreen(Minecraft minecraft) {
        hiddenBackgroundOnly = false;
        ensureWaitingScreen(minecraft);
    }

    public static void markVisible() {
        hiddenBackgroundOnly = false;
    }

    public static int toDisplaySeconds(int ticks) {
        return Math.max(1, (ticks + 19) / 20);
    }

    public static boolean canHideSpecialBranch() {
        return specialStage == JjaReviveSpecialStage.ESSENCE_READY || specialStage == JjaReviveSpecialStage.ESSENCE_TRIGGERED;
    }

    private static boolean shouldKeepSpecialBranchVisible() {
        return isSpecialBranchActive() && !canHideSpecialBranch();
    }

    private static void ensureWaitingScreen(Minecraft minecraft) {
        if (!(minecraft.screen instanceof JjaReviveScreen)) {
            minecraft.setScreen(new JjaReviveScreen());
        }
    }

    private static void closeReviveScreen(Minecraft minecraft) {
        Screen screen = minecraft.screen;
        if (screen instanceof JjaReviveScreen) {
            minecraft.setScreen(null);
        }
    }
}
