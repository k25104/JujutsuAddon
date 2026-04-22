package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.KeySpaceOnKeyPressedProcedureHook;
import net.mcreator.jujutsucraft.procedures.KeySpaceOnKeyPressedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeySpaceOnKeyPressedProcedure.class, remap = false)
public abstract class KeySpaceOnKeyPressedProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128379_(Ljava/lang/String;Z)V",
            shift = At.Shift.AFTER
        ),
        cancellable = true,
        remap = false,
        require = 1
    )
    private static void jja$stopPlayerImmediateDoubleJump(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        KeySpaceOnKeyPressedProcedureHook.onSpacePressed(entity);
        if (KeySpaceOnKeyPressedProcedureHook.shouldCancelPlayerContinuation(entity)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            shift = At.Shift.AFTER
        ),
        remap = false,
        require = 1
    )
    private static void jja$observeUpstreamDoubleJump(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        KeySpaceOnKeyPressedProcedureHook.onUpstreamDoubleJump(entity);
    }
}
