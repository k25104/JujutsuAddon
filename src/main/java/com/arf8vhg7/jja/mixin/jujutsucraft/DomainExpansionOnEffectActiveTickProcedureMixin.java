package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.DomainExpansionOnEffectActiveTickProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.procedures.DomainExpansionOnEffectActiveTickProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.objectweb.asm.Opcodes;

@Mixin(value = DomainExpansionOnEffectActiveTickProcedure.class, remap = false)
public abstract class DomainExpansionOnEffectActiveTickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), remap = false, require = 1)
    private static void jja$observeOpenBarrierWitnesses(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        DomainExpansionOnEffectActiveTickProcedureHook.onActiveTick(entity);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F"
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareOldHealthTracking(float currentHealth, @Local(argsOnly = true) Entity entity) {
        return DomainExpansionOnEffectActiveTickProcedureHook.getEffectiveHealth(entity, currentHealth);
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
    private static double jja$resolveCurrentRadius(
        double radius,
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.resolveCurrentRadius(entity, radius);
    }

    @ModifyVariable(
        method = "execute",
        at = @At(value = "STORE", ordinal = 1),
        index = 31,
        remap = false,
        require = 1
    )
    private static double jja$useFeetAnchorDistanceForSelfPresence(
        double originalDistance,
        @Local(argsOnly = true) Entity entity,
        @Local(name = "entityiterator") Entity target
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.resolveFeetAnchorDistanceSquared(entity, target, originalDistance);
    }

    @ModifyVariable(
        method = "execute",
        at = @At(value = "STORE", ordinal = 2),
        index = 31,
        remap = false,
        require = 1
    )
    private static double jja$useFeetAnchorDistanceForClashDetection(
        double originalDistance,
        @Local(argsOnly = true) Entity entity,
        @Local(name = "entityiterator") Entity target
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.resolveFeetAnchorDistanceSquared(entity, target, originalDistance);
    }

    @ModifyVariable(
        method = "execute",
        at = @At(value = "STORE", ordinal = 3),
        index = 31,
        remap = false,
        require = 1
    )
    private static double jja$useFeetAnchorDistanceForCoverRecovery(
        double originalDistance,
        @Local(argsOnly = true) Entity entity
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.resolveFeetAnchorDistanceSquared(entity, entity, originalDistance);
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
    private static void jja$useImmersivePortalsPocketBuildCenter(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        Operation<Void> original
    ) {
        DomainExpansionOnEffectActiveTickProcedureHook.runImmersivePortalsBattleBuild(world, x, y, z, entity, original);
    }

    @ModifyConstant(
        method = "execute",
        constant = @Constant(doubleValue = 10.0D, ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$MapVariables;DomainExpansionRadius:D",
                opcode = Opcodes.GETFIELD,
                ordinal = 0
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
                ordinal = 0
            )
        ),
        remap = false,
        require = 1
    )
    private static double jja$runSimpleDomainDecayBranchEveryTick(double original) {
        return DomainExpansionOnEffectActiveTickProcedureHook.resolveSimpleDomainTickInterval(original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21124_(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"
        ),
        remap = false
    ,
        require = 1
    )
    private static MobEffectInstance jja$replaceDomainDuration(LivingEntity livingEntity, MobEffect effect, Operation<MobEffectInstance> original) {
        return DomainExpansionOnEffectActiveTickProcedureHook.getEffect(livingEntity, effect);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
        ),
        remap = false
    ,
        require = 1
    )
    private static boolean jja$normalizeDomainDuration(
        LivingEntity livingEntity,
        MobEffectInstance effectInstance,
        Operation<Boolean> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.addEffect(livingEntity, effectInstance);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;sqrt(D)D"
        ),
        remap = false,
        require = 1
    )
    private static double jja$normalizeSimpleDomainDurationDecay(double root) {
        return DomainExpansionOnEffectActiveTickProcedureHook.normalizeSimpleDomainDurationDecayRoot(root);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$guardSimpleDomainExpireDuringDomainRewrite(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.removeSimpleDomainDuringDomainRewrite(livingEntity, effect, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$guardDomainExpansionRemoval1(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.removeSimpleDomainDuringDomainRewrite(livingEntity, effect, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 2
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$guardDomainExpansionRemoval2(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.removeSimpleDomainDuringDomainRewrite(livingEntity, effect, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 3
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$guardDomainExpansionRemoval3(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.removeSimpleDomainDuringDomainRewrite(livingEntity, effect, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 4
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$guardDomainExpansionRemoval4(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.removeSimpleDomainDuringDomainRewrite(livingEntity, effect, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z",
            ordinal = 5
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$guardDomainExpansionRemoval5(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        return DomainExpansionOnEffectActiveTickProcedureHook.removeSimpleDomainDuringDomainRewrite(livingEntity, effect, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(DD)D"
        ),
        remap = false
    ,
        require = 1
    )
    private static double jja$disableDurationDecay(double left, double right, Operation<Double> original) {
        return DomainExpansionOnEffectActiveTickProcedureHook.disableDurationDecay(left, right);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;max(DD)D",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static double jja$removeClashDamagePenaltyFromSelf(double left, double right, Operation<Double> original) {
        return DomainExpansionOnEffectActiveTickProcedureHook.removeClashDamagePenalty(original.call(left, right));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;max(DD)D",
            ordinal = 2
        ),
        remap = false,
        require = 1
    )
    private static double jja$removeClashDamagePenaltyFromOpponent(double left, double right, Operation<Double> original) {
        return DomainExpansionOnEffectActiveTickProcedureHook.removeClashDamagePenalty(original.call(left, right));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128347_(Ljava/lang/String;D)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$accumulateEffectiveClashDamage(
        CompoundTag tag,
        String key,
        double value,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        original.call(tag, key, DomainExpansionOnEffectActiveTickProcedureHook.resolveAccumulatedTotalDamage(entity, tag, key, value));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128379_(Ljava/lang/String;Z)V",
            ordinal = 2
        ),
        remap = false,
        require = 1
    )
    private static void jja$recordAndSuppressAuthoritativeOpenBarrierDistanceFailure(
        CompoundTag tag,
        String key,
        boolean value,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        boolean suppressDomainRemoval = DomainExpansionOnEffectActiveTickProcedureHook.shouldSuppressDomainExpansionRemoval(entity);
        if (suppressDomainRemoval) {
            return;
        }

        original.call(tag, key, value);
    }

    @ModifyConstant(
        method = "execute",
        constant = @Constant(doubleValue = 20.0, ordinal = 2),
        remap = false
    ,
        require = 1
    )
    private static double jja$modifyDrainInterval(double original) {
        return DomainExpansionOnEffectActiveTickProcedureHook.modifyDrainInterval(original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128379_(Ljava/lang/String;Z)V",
            ordinal = 3
        ),
        remap = false,
        require = 1
    )
    private static void jja$recordAndSuppressAuthoritativeOpenBarrierStateFailure(
        CompoundTag tag,
        String key,
        boolean value,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        boolean suppressDomainRemoval = DomainExpansionOnEffectActiveTickProcedureHook.shouldSuppressDomainExpansionRemoval(entity);
        if (suppressDomainRemoval) {
            return;
        }

        original.call(tag, key, value);
    }

    @ModifyConstant(method = "lambda$execute$2", constant = @Constant(doubleValue = 20.0), remap = false, require = 1)
    private static double jja$modifyCursePowerDrain(double original) {
        return DomainExpansionOnEffectActiveTickProcedureHook.modifyCursePowerDrain(original);
    }

    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$tickCounter(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo callbackInfo
    ) {
        DomainExpansionOnEffectActiveTickProcedureHook.tickCounter(entity);
    }

}
