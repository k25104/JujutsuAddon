package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DomainExpansionCreateBarrierProcedureHook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.DomainExpansionCreateBarrierProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.objectweb.asm.Opcodes;

@Mixin(value = DomainExpansionCreateBarrierProcedure.class, remap = false)
public abstract class DomainExpansionCreateBarrierProcedureMixin {
    @Inject(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/ChangeCurseEnergyProcedure;execute(Lnet/minecraft/world/entity/Entity;D)V",
            ordinal = 1,
            shift = At.Shift.AFTER
        ),
        remap = false,
        require = 1
    )
    private static void jja$observeHigurumaBarrierStart(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        DomainExpansionCreateBarrierProcedureHook.onBarrierCreateStart(entity);
    }

    @Definition(id = "getPersistentData", method = "Lnet/minecraft/world/entity/Entity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;")
    @Definition(id = "getDouble", method = "Lnet/minecraft/nbt/CompoundTag;m_128459_(Ljava/lang/String;)D")
    @Expression("?.getPersistentData().getDouble('select') == 1.0")
    @ModifyExpressionValue(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"), require = 1)
    private static boolean jja$forceOpenBarrierSelectCheck(boolean original) {
        return DomainExpansionCreateBarrierProcedureHook.forceOpenBarrierSelectCheck(original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            ordinal = 3
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$disableBarrierCreateNearbySlowness(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        if (DomainExpansionCreateBarrierProcedureHook.shouldApplyNearbySlowness(livingEntity, effectInstance)) {
            return original.call(livingEntity, effectInstance);
        }
        return false;
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$normalizeDomainDuration(LivingEntity livingEntity, MobEffectInstance effectInstance, Operation<Boolean> original) {
        return DomainExpansionCreateBarrierProcedureHook.addNormalizedDomainExpansionEffect(livingEntity, effectInstance, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;random()D"),
        remap = false,
        require = 1
    )
    private static double jja$reserveOpenBarrierRandomGrant(Operation<Double> original, @Local(argsOnly = true) Entity entity) {
        return DomainExpansionCreateBarrierProcedureHook.resolveReservedOpenBarrierRoll(original.call(), entity);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_6144_()Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$resolveConfiguredOpenBarrierInput(Entity entity, Operation<Boolean> original) {
        return DomainExpansionCreateBarrierProcedureHook.resolveOpenBarrierCrouchCheck(entity, original.call(entity));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/DomainExpansionBattleProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$runImmersivePortalsBarrierBuildInBothDimensions(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        Operation<Void> original
    ) {
        DomainExpansionCreateBarrierProcedureHook.runImmersivePortalsBarrierBuild(world, x, y, z, entity, original);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$MapVariables;DomainExpansionRadius:D",
            opcode = Opcodes.GETFIELD
        ),
        remap = false,
        require = 1
    )
    private static double jja$resolveInitialCurrentRadius(
        double radius,
        net.minecraft.world.level.LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity
    ) {
        return DomainExpansionCreateBarrierProcedureHook.resolveInitialCurrentRadius(entity, radius);
    }
}
