package com.arf8vhg7.jja.feature.jja.domain.sd;

public enum AntiDomainPressDecision {
    PASS_THROUGH(false),
    CANCEL_NOOP(true),
    RELEASE_ONLY(false),
    CANCEL_HOLD(true),
    PRE_CLEAR_FBE(false);

    private final boolean cancelUpstream;

    AntiDomainPressDecision(boolean cancelUpstream) {
        this.cancelUpstream = cancelUpstream;
    }

    public boolean cancelUpstream() {
        return this.cancelUpstream;
    }
}
