package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainPresentation;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueOption;
import javax.annotation.Nullable;

final class AntiDomainRuntimeState {
    final AntiDomainPressState press = new AntiDomainPressState();
    final AntiDomainActiveSessionState session = new AntiDomainActiveSessionState();
    final AntiDomainAnimationState animation = new AntiDomainAnimationState();

    boolean isIdle() {
        return !this.press.keyHeld
            && !this.session.holdRequested
            && this.session.activePresentation == AntiDomainPresentation.NONE
            && !AntiDomainAnimationStateMachine.hasTrackedAnimation(this.animation)
            && !this.animation.holdRunActive
            && this.animation.pendingStopReason == AntiDomainAnimationStopReason.NONE;
    }
}

final class AntiDomainPressState {
    boolean keyHeld;
    @Nullable
    AntiDomainTechniqueOption selectedOptionAtPress;
}

final class AntiDomainActiveSessionState {
    AntiDomainPresentation activePresentation = AntiDomainPresentation.NONE;
    boolean holdRequested;
    int holdAmplifier;
}

final class AntiDomainAnimationState {
    boolean holdRunActive;
    AntiDomainAnimationStopReason pendingStopReason = AntiDomainAnimationStopReason.NONE;
    long pressStartedTick = Long.MIN_VALUE;
    long holdRunStartedTick = Long.MIN_VALUE;
    long minimumHoldUntilTick = Long.MIN_VALUE;
}

enum AntiDomainAnimationStopReason {
    NONE,
    RELEASE,
    TERMINAL
}
