package com.arf8vhg7.jja.feature.jja.domain.sd;

final class AntiDomainAnimationStateMachine {
    private static final int ANIMATION_MIN_HOLD_TICKS = 20;

    private AntiDomainAnimationStateMachine() {
    }

    static void onPressStarted(AntiDomainAnimationState animation) {
        if (animation == null || animation.pendingStopReason != AntiDomainAnimationStopReason.RELEASE) {
            return;
        }
        animation.pendingStopReason = AntiDomainAnimationStopReason.NONE;
    }

    static void onActivationSuccess(AntiDomainAnimationState animation, long gameTime) {
        if (animation == null) {
            return;
        }
        animation.holdRunActive = false;
        animation.holdRunStartedTick = Long.MIN_VALUE;
        animation.pendingStopReason = AntiDomainAnimationStopReason.NONE;
        animation.minimumHoldUntilTick = resolveActivationMinimumHoldUntil(gameTime, animation.pressStartedTick);
    }

    static void onHoldAnimationReplay(AntiDomainAnimationState animation) {
        if (animation == null) {
            return;
        }
        animation.pendingStopReason = AntiDomainAnimationStopReason.NONE;
    }

    static boolean updateHoldRun(AntiDomainAnimationState animation, boolean mayAttemptExtension, long gameTime) {
        if (animation == null) {
            return false;
        }
        boolean startHoldRun = mayAttemptExtension && !animation.holdRunActive;
        animation.holdRunActive = mayAttemptExtension;
        if (startHoldRun) {
            animation.holdRunStartedTick = gameTime;
            animation.minimumHoldUntilTick = resolveHoldRunMinimumHoldUntil(gameTime);
        }
        return startHoldRun;
    }

    static void resetHoldRun(AntiDomainAnimationState animation) {
        if (animation == null) {
            return;
        }
        animation.holdRunActive = false;
    }

    static void requestReleaseStop(AntiDomainAnimationState animation) {
        if (animation == null) {
            return;
        }
        animation.holdRunActive = false;
        if (!hasTrackedAnimation(animation)) {
            return;
        }
        animation.pendingStopReason = AntiDomainAnimationStopReason.RELEASE;
    }

    static void requestTerminalStop(AntiDomainAnimationState animation) {
        if (animation == null) {
            return;
        }
        animation.holdRunActive = false;
        if (!hasTrackedAnimation(animation)) {
            return;
        }
        animation.pendingStopReason = AntiDomainAnimationStopReason.TERMINAL;
    }

    static boolean shouldSendPendingCancel(AntiDomainAnimationState animation, long gameTime) {
        if (animation == null) {
            return false;
        }
        return switch (animation.pendingStopReason) {
            case NONE -> false;
            case TERMINAL -> true;
            case RELEASE -> animation.minimumHoldUntilTick != Long.MIN_VALUE && gameTime >= animation.minimumHoldUntilTick;
        };
    }

    static void onCancelSent(AntiDomainAnimationState animation) {
        if (animation == null) {
            return;
        }
        animation.holdRunActive = false;
        animation.pendingStopReason = AntiDomainAnimationStopReason.NONE;
        animation.pressStartedTick = Long.MIN_VALUE;
        animation.holdRunStartedTick = Long.MIN_VALUE;
        animation.minimumHoldUntilTick = Long.MIN_VALUE;
    }

    static boolean hasTrackedAnimation(AntiDomainAnimationState animation) {
        return animation != null && animation.minimumHoldUntilTick != Long.MIN_VALUE;
    }

    static long resolveActivationMinimumHoldUntil(long gameTime) {
        return resolveActivationMinimumHoldUntil(gameTime, Long.MIN_VALUE);
    }

    static long resolveActivationMinimumHoldUntil(long gameTime, long pressStartedTick) {
        if (pressStartedTick != Long.MIN_VALUE) {
            return pressStartedTick + ANIMATION_MIN_HOLD_TICKS;
        }
        return gameTime + ANIMATION_MIN_HOLD_TICKS;
    }

    static long resolveHoldRunMinimumHoldUntil(long gameTime) {
        return gameTime + ANIMATION_MIN_HOLD_TICKS;
    }
}
