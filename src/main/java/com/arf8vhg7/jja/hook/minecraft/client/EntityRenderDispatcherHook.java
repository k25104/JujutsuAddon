package com.arf8vhg7.jja.hook.minecraft.client;

import com.arf8vhg7.jja.feature.jja.domain.de.curtain.client.CurtainClientState;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public final class EntityRenderDispatcherHook {
    private EntityRenderDispatcherHook() {
    }

    public static boolean shouldSuppressCurtainTargetRender(@Nullable Entity entity) {
        return CurtainClientState.shouldSuppressEntityRender(entity);
    }
}
