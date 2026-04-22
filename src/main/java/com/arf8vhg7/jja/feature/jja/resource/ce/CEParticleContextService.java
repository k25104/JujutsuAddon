package com.arf8vhg7.jja.feature.jja.resource.ce;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public final class CEParticleContextService {
    private static final ThreadLocal<Deque<Context>> CONTEXTS = ThreadLocal.withInitial(ArrayDeque::new);

    private CEParticleContextService() {
    }

    public static void enter(@Nullable Entity sourceEntity) {
        enter(sourceEntity, null);
    }

    public static void enter(@Nullable Entity sourceEntity, @Nullable Entity explicitOwner) {
        CONTEXTS.get().push(new Context(sourceEntity, CEColorService.resolveParticleOwner(sourceEntity, explicitOwner)));
    }

    public static void exit() {
        Deque<Context> contexts = CONTEXTS.get();
        if (!contexts.isEmpty()) {
            contexts.pop();
        }
        if (contexts.isEmpty()) {
            CONTEXTS.remove();
        }
    }

    public static boolean hasContext() {
        return !CONTEXTS.get().isEmpty();
    }

    public static @Nullable Entity currentSourceEntity() {
        Deque<Context> contexts = CONTEXTS.get();
        return contexts.isEmpty() ? null : contexts.peek().sourceEntity();
    }

    public static @Nullable Entity currentResolvedOwner() {
        Deque<Context> contexts = CONTEXTS.get();
        return contexts.isEmpty() ? null : contexts.peek().resolvedOwner();
    }

    private record Context(@Nullable Entity sourceEntity, @Nullable Entity resolvedOwner) {
    }
}
