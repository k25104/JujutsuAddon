package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.BulletNailWhileProjectileFlyingTickProcedureHook;
import net.mcreator.jujutsucraft.procedures.BulletNailWhileProjectileFlyingTickProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BulletNailWhileProjectileFlyingTickProcedure.class, remap = false)
public abstract class BulletNailWhileProjectileFlyingTickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$enterCeParticleContext(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        Entity immediatesourceentity,
        CallbackInfo ci
    ) {
        BulletNailWhileProjectileFlyingTickProcedureHook.enterCeParticleContext(entity, immediatesourceentity);
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false, require = 1)
    private static void jja$exitCeParticleContext(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        Entity immediatesourceentity,
        CallbackInfo ci
    ) {
        BulletNailWhileProjectileFlyingTickProcedureHook.exitCeParticleContext();
    }
}
