package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AIProjectileSlashProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mcreator.jujutsucraft.procedures.AIProjectileSlashProcedure;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AIProjectileSlashProcedure.class, remap = false)
public abstract class AIProjectileSlashProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128347_(Ljava/lang/String;D)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$disableProjectileSlashKnockback(
        CompoundTag tag,
        String key,
        double value,
        Operation<Void> original
    ) {
        original.call(tag, key, AIProjectileSlashProcedureHook.resolvePersistentDouble(key, value));
    }
}
