package com.arf8vhg7.jja.mixin.minecraft.client;

import com.arf8vhg7.jja.hook.minecraft.client.EntityRenderDispatcherHook;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(
        method = "shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z",
        at = @At("HEAD"),
        cancellable = true,
        require = 1
    )
    private <E extends Entity> void jja$suppressCurtainTargetRender(
        E entity,
        Frustum frustum,
        double x,
        double y,
        double z,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (EntityRenderDispatcherHook.shouldSuppressCurtainTargetRender(entity)) {
            cir.setReturnValue(false);
        }
    }
}
