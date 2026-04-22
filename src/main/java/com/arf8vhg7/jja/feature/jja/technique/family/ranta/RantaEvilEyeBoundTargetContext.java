package com.arf8vhg7.jja.feature.jja.technique.family.ranta;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class RantaEvilEyeBoundTargetContext {
    private static final ThreadLocal<Deque<String>> CONTEXTS = ThreadLocal.withInitial(ArrayDeque::new);

    private RantaEvilEyeBoundTargetContext() {
    }

    public static Scope enter(String allowedTargetUuid) {
        CONTEXTS.get().push(normalizeTargetUuid(allowedTargetUuid));
        return new Scope();
    }

    public static <T> T withAllowedTarget(String allowedTargetUuid, Supplier<T> action) {
        try (Scope ignored = enter(allowedTargetUuid)) {
            return action.get();
        }
    }

    public static void withAllowedTarget(String allowedTargetUuid, Runnable action) {
        withAllowedTarget(allowedTargetUuid, () -> {
            action.run();
            return null;
        });
    }

    public static boolean isActive() {
        return !CONTEXTS.get().isEmpty();
    }

    public static boolean shouldRestrictCandidate(@Nullable String candidateUuid) {
        Deque<String> contexts = CONTEXTS.get();
        return !contexts.isEmpty() && !contexts.peek().equals(normalizeTargetUuid(candidateUuid));
    }

    private static void exit() {
        Deque<String> contexts = CONTEXTS.get();
        if (!contexts.isEmpty()) {
            contexts.pop();
        }
        if (contexts.isEmpty()) {
            CONTEXTS.remove();
        }
    }

    private static String normalizeTargetUuid(@Nullable String targetUuid) {
        return targetUuid == null ? "" : targetUuid.trim();
    }

    public static final class Scope implements AutoCloseable {
        private boolean closed;

        private Scope() {
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            exit();
        }
    }
}
