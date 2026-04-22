package com.arf8vhg7.jja.feature.jja.technique.family.kugisaki;

import java.util.UUID;
import net.minecraft.world.entity.Entity;

public final class KugisakiHairpinTargetingContext {
    private static final ThreadLocal<Context> CURRENT = new ThreadLocal<>();

    private KugisakiHairpinTargetingContext() {
    }

    public static Scope enter(Entity attacker, Entity target) {
        if (attacker == null || target == null) {
            return Scope.NOOP;
        }

        Context previous = CURRENT.get();
        CURRENT.set(new Context(attacker.getUUID(), target.getUUID()));
        return new Scope(previous);
    }

    public static boolean shouldRejectCandidate(Entity attacker, Entity candidate) {
        Context context = CURRENT.get();
        if (context == null || attacker == null || candidate == null) {
            return false;
        }

        return context.attackerId.equals(attacker.getUUID()) && !context.targetId.equals(candidate.getUUID());
    }

    private record Context(UUID attackerId, UUID targetId) {
    }

    public static final class Scope implements AutoCloseable {
        private static final Scope NOOP = new Scope(null, true);

        private final Context previous;
        private final boolean noop;

        private Scope(Context previous) {
            this(previous, false);
        }

        private Scope(Context previous, boolean noop) {
            this.previous = previous;
            this.noop = noop;
        }

        @Override
        public void close() {
            if (this.noop) {
                return;
            }

            if (this.previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(this.previous);
            }
        }
    }
}
