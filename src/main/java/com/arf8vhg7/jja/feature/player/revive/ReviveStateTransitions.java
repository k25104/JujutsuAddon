package com.arf8vhg7.jja.feature.player.revive;

import com.arf8vhg7.jja.feature.player.state.model.PlayerReviveState;

final class ReviveStateTransitions {
    private ReviveStateTransitions() {
    }

    static void enterWaiting(PlayerReviveState reviveState, int waitTicks) {
        if (reviveState == null) {
            return;
        }
        reviveState.setReviveRemainingTicks(waitTicks);
        clearHold(reviveState);
    }

    static void clearWaiting(PlayerReviveState reviveState, boolean consumeRevive) {
        if (reviveState == null) {
            return;
        }
        reviveState.setReviveRemainingTicks(0);
        if (consumeRevive) {
            reviveState.setRemainingRevives(Math.max(0, reviveState.getRemainingRevives() - 1));
        }
        clearHold(reviveState);
        clearSpecialStage(reviveState);
    }

    static void forceDeath(PlayerReviveState reviveState) {
        if (reviveState == null) {
            return;
        }
        reviveState.setReviveRemainingTicks(-1);
        clearHold(reviveState);
        clearSpecialStage(reviveState);
    }

    static SpecialStageUpdate advanceSpecialStage(PlayerReviveState reviveState, int graspedStartTicks, int readyStartTicks) {
        JjaReviveSpecialStage previousStage = reviveState == null
            ? JjaReviveSpecialStage.NONE
            : JjaReviveSpecialStage.fromId(reviveState.getReviveSpecialStage());
        if (reviveState == null || !previousStage.isActive() || previousStage == JjaReviveSpecialStage.ESSENCE_TRIGGERED) {
            return new SpecialStageUpdate(previousStage, previousStage, reviveState == null ? 0 : reviveState.getReviveSpecialTicks());
        }

        int specialTicks = reviveState.getReviveSpecialTicks() + 1;
        reviveState.setReviveSpecialTicks(specialTicks);
        if (specialTicks == graspedStartTicks) {
            reviveState.setReviveSpecialStage(JjaReviveSpecialStage.GRASPED.id());
        } else if (specialTicks == readyStartTicks) {
            reviveState.setReviveSpecialStage(JjaReviveSpecialStage.ESSENCE_READY.id());
        }
        return new SpecialStageUpdate(previousStage, JjaReviveSpecialStage.fromId(reviveState.getReviveSpecialStage()), specialTicks);
    }

    static void clearSpecialStage(PlayerReviveState reviveState) {
        if (reviveState == null) {
            return;
        }
        reviveState.setReviveSpecialStage(JjaReviveSpecialStage.NONE.id());
        reviveState.setReviveSpecialTicks(0);
    }

    static void clearHold(PlayerReviveState reviveState) {
        if (reviveState == null) {
            return;
        }
        reviveState.setReviveHoldActive(false);
        reviveState.setReviveHoldTicks(0);
        reviveState.setReviveHoldTarget(null);
    }

    record SpecialStageUpdate(JjaReviveSpecialStage previousStage, JjaReviveSpecialStage currentStage, int currentTicks) {
        boolean stageChanged() {
            return this.previousStage != this.currentStage;
        }
    }
}
