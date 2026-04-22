package com.arf8vhg7.jja.compat;

import java.util.function.BooleanSupplier;
import org.slf4j.Logger;

public final class JjaReflectiveCompatSupport {
    private JjaReflectiveCompatSupport() {
    }

    public static boolean ensureInitialized(
        InitState state,
        Object lock,
        BooleanSupplier modLoaded,
        Initializer initializer,
        FailureLogger failureLogger
    ) {
        if (state.initialized) {
            return state.available;
        }
        synchronized (lock) {
            if (state.initialized) {
                return state.available;
            }
            if (!modLoaded.getAsBoolean()) {
                state.initialized = true;
                state.available = false;
                return false;
            }
            try {
                initializer.run();
                state.available = true;
            } catch (ReflectiveOperationException | RuntimeException exception) {
                failureLogger.log(exception);
                state.available = false;
            }
            state.initialized = true;
            return state.available;
        }
    }

    public static void logCompatError(Logger logger, String compatName, String action, Exception exception) {
        logger.warn("Failed to {} via {} compat reflection.", action, compatName, exception);
    }

    public static final class InitState {
        private boolean initialized;
        private boolean available;
    }

    @FunctionalInterface
    public interface Initializer {
        void run() throws ReflectiveOperationException;
    }

    @FunctionalInterface
    public interface FailureLogger {
        void log(Exception exception);
    }
}
