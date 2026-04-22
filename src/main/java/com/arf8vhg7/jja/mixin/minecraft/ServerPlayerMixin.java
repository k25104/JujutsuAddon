package com.arf8vhg7.jja.mixin.minecraft;

import com.arf8vhg7.jja.hook.minecraft.ServerPlayerHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @ModifyExpressionValue(
        method = "die",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z",
            ordinal = 0
        ),
        require = 1
    )
    private boolean jja$suppressReviveDeferredDeathMessage(boolean original) {
        return ServerPlayerHook.resolveShowDeathMessages((ServerPlayer) (Object) this, original);
    }

    @Inject(
        method = "die",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;removeEntitiesOnShoulder()V"
        ),
        cancellable = true,
        require = 1
    )
    private void jja$enterReviveWaitingAfterDeathMessage(DamageSource damageSource, CallbackInfo ci) {
        if (ServerPlayerHook.tryEnterReviveWaitingAfterDeathMessage((ServerPlayer) (Object) this, damageSource)) {
            ci.cancel();
        }
    }
}
