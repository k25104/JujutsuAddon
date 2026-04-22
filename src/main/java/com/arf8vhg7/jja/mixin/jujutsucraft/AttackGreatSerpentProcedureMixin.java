package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.AttackGreatSerpentProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.AttackGreatSerpentProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttackGreatSerpentProcedure.class, remap = false)
public abstract class AttackGreatSerpentProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$tickHeldEntity(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        AttackGreatSerpentProcedureHook.tickActiveGrab(entity);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_20329_(Lnet/minecraft/world/entity/Entity;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$grabEntityInsteadOfMounting(
        Entity target,
        Entity vehicle,
        Operation<Boolean> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        if (AttackGreatSerpentProcedureHook.tryGrabTarget(entity, target)) {
            return true;
        }
        return original.call(target, vehicle);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_20202_()Lnet/minecraft/world/entity/Entity;"
        ),
        remap = false,
        require = 1
    )
    private static Entity jja$treatHeldEntityAsMounted(
        Entity candidate,
        Operation<Entity> original,
        @Local(argsOnly = true, ordinal = 0) Entity entity
    ) {
        return AttackGreatSerpentProcedureHook.resolveHeldVehicle(candidate, entity, original.call(candidate));
    }

    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$clearInactiveGrab(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        AttackGreatSerpentProcedureHook.tryGrabNearbyTarget(world, x, y, z, entity);
        AttackGreatSerpentProcedureHook.clearGrabWhenInactive(entity);
    }
}
