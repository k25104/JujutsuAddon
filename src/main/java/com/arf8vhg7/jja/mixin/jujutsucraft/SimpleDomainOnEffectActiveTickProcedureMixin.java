package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.SimpleDomainOnEffectActiveTickProcedureHook;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import java.util.function.Predicate;
import net.mcreator.jujutsucraft.procedures.SimpleDomainOnEffectActiveTickProcedure;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SimpleDomainOnEffectActiveTickProcedure.class, remap = false)
public abstract class SimpleDomainOnEffectActiveTickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$countSimpleDomainTick(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        SimpleDomainOnEffectActiveTickProcedureHook.onActiveTick(entity);
    }

    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$extendSimpleDomainHold(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        SimpleDomainOnEffectActiveTickProcedureHook.extendSimpleDomainHold(entity);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/SimpleDomainEffectStartedappliedProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$skipRingParticleForHollowWickerBasket(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        Operation<Void> original
    ) {
        if (SimpleDomainOnEffectActiveTickProcedureHook.shouldSpawnRingParticle(entity)) {
            original.call(world, x, y, z, entity);
        }
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/EntityType;m_204039_(Lnet/minecraft/tags/TagKey;)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$forceHollowWickerBasketParticle(
        EntityType<?> entityType,
        TagKey<EntityType<?>> tagKey,
        Operation<Boolean> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return SimpleDomainOnEffectActiveTickProcedureHook.shouldPlayHollowWickerBasketParticle(entity, original.call(entityType, tagKey));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/LevelAccessor;m_6443_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
        ),
        remap = false,
        require = 1
    )
    private static List<Entity> jja$skipNearbySimpleDomainGrantForHollowWickerBasket(
        LevelAccessor world,
        Class<Entity> entityClass,
        AABB area,
        Predicate<? super Entity> predicate,
        Operation<List<Entity>> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return SimpleDomainOnEffectActiveTickProcedureHook.resolveNearbySimpleDomainTargets(
            entity,
            original.call(world, entityClass, area, predicate)
        );
    }

    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(DD)D"),
        remap = false,
        require = 1
    )
    private static double jja$capActiveSimpleDomainRadiusAtSixteen(double left, double right, Operation<Double> original) {
        return SimpleDomainOnEffectActiveTickProcedureHook.resolveSimpleDomainRadius(left);
    }
}
