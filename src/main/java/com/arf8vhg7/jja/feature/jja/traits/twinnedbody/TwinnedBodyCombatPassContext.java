package com.arf8vhg7.jja.feature.jja.traits.twinnedbody;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public final class TwinnedBodyCombatPassContext {
    private static final ThreadLocal<Deque<Context>> CONTEXTS = ThreadLocal.withInitial(ArrayDeque::new);

    private TwinnedBodyCombatPassContext() {
    }

    public static Scope enter(@Nullable Entity attacker, @Nullable Entity target, PassKind passKind) {
        return enter(attacker != null ? attacker.getUUID() : null, target != null ? target.getUUID() : null, passKind);
    }

    public static <T> T withScope(@Nullable Entity attacker, @Nullable Entity target, PassKind passKind, Supplier<T> action) {
        try (Scope ignored = enter(attacker, target, passKind)) {
            return action.get();
        }
    }

    public static void withScope(@Nullable Entity attacker, @Nullable Entity target, PassKind passKind, Runnable action) {
        withScope(attacker, target, passKind, () -> {
            action.run();
            return null;
        });
    }

    public static boolean hasContext() {
        return !CONTEXTS.get().isEmpty();
    }

    public static boolean isPrimaryPass() {
        return currentPassKind() == PassKind.PRIMARY;
    }

    public static boolean isEchoPass() {
        return currentPassKind() == PassKind.ECHO;
    }

    public static boolean isExtraArmAttack() {
        return isEchoPass();
    }

    public static @Nullable PassKind currentPassKind() {
        Context context = peek();
        return context == null ? null : context.passKind();
    }

    public static boolean matches(@Nullable Entity attacker, @Nullable Entity target) {
        Context context = peek();
        return context != null && context.matches(attacker, target);
    }

    public static boolean matchesAttacker(@Nullable Entity attacker) {
        Context context = peek();
        return context != null && context.matchesAttacker(attacker);
    }

    public static boolean matchesTarget(@Nullable Entity target) {
        Context context = peek();
        return context != null && context.matchesTarget(target);
    }

    public static @Nullable UUID currentAttackerUuid() {
        Context context = peek();
        return context == null ? null : context.attackerUuid();
    }

    public static @Nullable UUID currentTargetUuid() {
        Context context = peek();
        return context == null ? null : context.targetUuid();
    }

    private static Scope enter(@Nullable UUID attackerUuid, @Nullable UUID targetUuid, PassKind passKind) {
        CONTEXTS.get().push(new Context(attackerUuid, targetUuid, passKind));
        return new Scope();
    }

    private static @Nullable Context peek() {
        Deque<Context> contexts = CONTEXTS.get();
        return contexts.isEmpty() ? null : contexts.peek();
    }

    private static void exit() {
        Deque<Context> contexts = CONTEXTS.get();
        if (!contexts.isEmpty()) {
            contexts.pop();
        }
        if (contexts.isEmpty()) {
            CONTEXTS.remove();
        }
    }

    public enum PassKind {
        PRIMARY,
        ECHO
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

    private record Context(@Nullable UUID attackerUuid, @Nullable UUID targetUuid, PassKind passKind) {
        private boolean matches(@Nullable Entity attacker, @Nullable Entity target) {
            return matchesAttacker(attacker) && matchesTarget(target);
        }

        private boolean matchesAttacker(@Nullable Entity attacker) {
            return attacker != null && this.attackerUuid != null && this.attackerUuid.equals(attacker.getUUID());
        }

        private boolean matchesTarget(@Nullable Entity target) {
            return target != null && this.targetUuid != null && this.targetUuid.equals(target.getUUID());
        }
    }
}