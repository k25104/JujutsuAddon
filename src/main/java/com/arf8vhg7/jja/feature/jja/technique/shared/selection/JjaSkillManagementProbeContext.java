package com.arf8vhg7.jja.feature.jja.technique.shared.selection;

public final class JjaSkillManagementProbeContext {
    private static final ThreadLocal<CandidateContext> CURRENT_CANDIDATE = new ThreadLocal<>();
    private static final ThreadLocal<ProbeState> CURRENT_PROBE = new ThreadLocal<>();

    private JjaSkillManagementProbeContext() {
    }

    public static void setCurrentCandidate(int curseTechniqueId, int selectTechniqueId, String canonicalName) {
        CURRENT_CANDIDATE.set(new CandidateContext(curseTechniqueId, selectTechniqueId, canonicalName));
    }

    public static CandidateContext getCurrentCandidate() {
        return CURRENT_CANDIDATE.get();
    }

    public static void clearCurrentCandidate() {
        CURRENT_CANDIDATE.remove();
    }

    public static boolean isIgnoringHiddenProbe() {
        ProbeState state = CURRENT_PROBE.get();
        return state != null && state.ignoreHidden;
    }

    public static boolean isProbeActive() {
        return CURRENT_PROBE.get() != null;
    }

    public static void captureProbeCandidate(int curseTechniqueId, int selectTechniqueId, String canonicalName, boolean skipped) {
        ProbeState state = CURRENT_PROBE.get();
        if (state == null || state.captured) {
            return;
        }
        state.captured = true;
        state.curseTechniqueId = curseTechniqueId;
        state.selectTechniqueId = selectTechniqueId;
        state.canonicalName = canonicalName;
        state.skipped = skipped;
    }

    public static void captureProbeCost(double cost) {
        ProbeState state = CURRENT_PROBE.get();
        if (state == null || state.costCaptured) {
            return;
        }
        state.cost = cost;
        state.costCaptured = true;
    }

    public static ProbeSnapshot runProbe(int initialSelectTechniqueId, boolean ignoreHidden, Runnable action) {
        ProbeState previous = CURRENT_PROBE.get();
        ProbeState state = new ProbeState(initialSelectTechniqueId, ignoreHidden);
        CURRENT_PROBE.set(state);
        try {
            action.run();
            return state.snapshot();
        } finally {
            CURRENT_PROBE.set(previous);
            CURRENT_CANDIDATE.remove();
        }
    }

    public record CandidateContext(int curseTechniqueId, int selectTechniqueId, String canonicalName) {
    }

    public record ProbeSnapshot(
        int initialSelectTechniqueId,
        boolean captured,
        int curseTechniqueId,
        int selectTechniqueId,
        String canonicalName,
        double cost,
        boolean skipped
    ) {
        public boolean matchesInitialSelect() {
            if (!this.captured) {
                return false;
            }
            if (this.curseTechniqueId == 18
                && this.initialSelectTechniqueId >= 11
                && this.initialSelectTechniqueId <= 13
                && this.selectTechniqueId == 12) {
                return true;
            }
            return this.initialSelectTechniqueId == this.selectTechniqueId;
        }
    }

    private static final class ProbeState {
        private final int initialSelectTechniqueId;
        private final boolean ignoreHidden;
        private boolean captured;
        private int curseTechniqueId;
        private int selectTechniqueId;
        private String canonicalName = "";
        private double cost;
        private boolean costCaptured;
        private boolean skipped;

        private ProbeState(int initialSelectTechniqueId, boolean ignoreHidden) {
            this.initialSelectTechniqueId = initialSelectTechniqueId;
            this.ignoreHidden = ignoreHidden;
        }

        private ProbeSnapshot snapshot() {
            return new ProbeSnapshot(
                this.initialSelectTechniqueId,
                this.captured,
                this.curseTechniqueId,
                this.selectTechniqueId,
                this.canonicalName,
                this.cost,
                this.skipped
            );
        }
    }
}
