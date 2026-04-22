package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.KeySpaceOnKeyReleasedProcedureHook;
import net.mcreator.jujutsucraft.procedures.KeySpaceOnKeyReleasedProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeySpaceOnKeyReleasedProcedure.class, remap = false)
public abstract class KeySpaceOnKeyReleasedProcedureMixin {
    @Inject(
        method = "execute(Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128379_(Ljava/lang/String;Z)V",
            shift = At.Shift.AFTER
        ),
        remap = false,
        require = 1
    )
    private static void jja$handleSpaceRelease(Entity entity, CallbackInfo ci) {
        KeySpaceOnKeyReleasedProcedureHook.onSpaceReleased(entity);
    }
}
