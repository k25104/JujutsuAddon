package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.InfinityActiveTickProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.mcreator.jujutsucraft.procedures.InfinityActiveTickProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = InfinityActiveTickProcedure.class, remap = false)
public abstract class InfinityActiveTickProcedureMixin {
    @ModifyConstant(method = "execute", constant = @Constant(intValue = 10), remap = false, require = 1)
    private static int jja$modifyDrainInterval(int original) {
        return InfinityActiveTickProcedureHook.modifyDrainInterval(original);
    }

    @ModifyConstant(method = "execute", constant = @Constant(intValue = 5), remap = false, require = 1)
    private static int jja$modifyDrainOffset(int original) {
        return InfinityActiveTickProcedureHook.modifyDrainOffset(original);
    }

    @ModifyConstant(method = "execute", constant = @Constant(doubleValue = 5.0), remap = false, require = 1)
    private static double jja$modifyCursePowerDrain(double original) {
        return InfinityActiveTickProcedureHook.modifyCursePowerDrain(original);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$ignoreNeutralizationForInfinity(boolean original) {
        return InfinityActiveTickProcedureHook.ignoreNeutralizationForInfinity(original);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 2
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$ignoreSimpleDomainForInfinity(boolean original) {
        return InfinityActiveTickProcedureHook.ignoreNeutralizationForInfinity(original);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21023_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 3
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$removeSixEyesFreeDrain(boolean original) {
        return InfinityActiveTickProcedureHook.disableSixEyesFreeDrain(original);
    }

    @WrapWithCondition(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128379_(Ljava/lang/String;Z)V"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$skipInfinityStopFlagForDomainBypass(
        CompoundTag compoundTag,
        String key,
        boolean value,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true) Entity entity,
        @Local(name = "entityiterator") Entity entityiterator
    ) {
        return InfinityActiveTickProcedureHook.shouldApplyInfinityStop(world, entity, entityiterator);
    }

    @WrapWithCondition(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_20256_(Lnet/minecraft/world/phys/Vec3;)V",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$skipProjectileInfinityStopForDomainBypass(
        Entity entityiterator,
        Vec3 vec3,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true) Entity entity
    ) {
        return InfinityActiveTickProcedureHook.shouldApplyInfinityStop(world, entity, entityiterator);
    }

    @WrapWithCondition(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_20256_(Lnet/minecraft/world/phys/Vec3;)V",
            ordinal = 2
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$skipHitboxInfinityStopForDomainBypass(
        Entity entityiterator,
        Vec3 vec3,
        @Local(argsOnly = true) LevelAccessor world,
        @Local(argsOnly = true) Entity entity
    ) {
        return InfinityActiveTickProcedureHook.shouldApplyInfinityStop(world, entity, entityiterator);
    }
}
