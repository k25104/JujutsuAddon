package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.PlayerPhysicalAbilityProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.PlayerPhysicalAbilityProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerPhysicalAbilityProcedure.class, remap = false)
public abstract class PlayerPhysicalAbilityProcedureMixin {
    @ModifyExpressionValue(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/level/ServerPlayer;f_19797_:I",
            opcode = Opcodes.GETFIELD,
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static int jja$allowEveryTickPhysicalAbilityExecution(int tickCount) {
        return PlayerPhysicalAbilityProcedureHook.bypassOuterTickParity(tickCount);
    }

    @WrapOperation(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;round(D)J",
            ordinal = 4
        ),
        remap = false
    ,
        require = 1
    )
    private static long jja$scaleHighLevelHealthBoost(
        double value,
        Operation<Long> original,
        @Local(index = 8) double level,
        @Local(index = 41) JujutsucraftModVariables.PlayerVariables playerVars
    ) {
        if (level < 19.0) {
            return original.call(value);
        }
        return PlayerPhysicalAbilityProcedureHook.highLevelHealthBoost(playerVars);
    }

    @WrapOperation(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(DD)D",
            ordinal = 10
        ),
        remap = false
    ,
        require = 1
    )
    private static double jja$removeArmorCap(double value, double max, Operation<Double> original) {
        return PlayerPhysicalAbilityProcedureHook.uncapArmor(value, max);
    }

    @WrapOperation(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(DD)D",
            ordinal = 11
        ),
        remap = false
    ,
        require = 1
    )
    private static double jja$removeArmorToughnessCap(double value, double max, Operation<Double> original) {
        return PlayerPhysicalAbilityProcedureHook.uncapArmorToughness(value, max);
    }

    @WrapOperation(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21153_(F)V"
        ),
        remap = false
    ,
        require = 1
    )
    private static void jja$applyFirstAidAwareHealthBoostRestore(LivingEntity livingEntity, float health, Operation<Void> original) {
        PlayerPhysicalAbilityProcedureHook.applyHealthBoostSetHealth(livingEntity, health, original);
    }

    @ModifyArg(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            ordinal = 0
        ),
        index = 0,
        remap = false,
        require = 1
    )
    private static MobEffectInstance jja$configureInitialSpeedDuration(MobEffectInstance effectInstance) {
        return PlayerPhysicalAbilityProcedureHook.configureSpeedEffect(effectInstance);
    }

    @ModifyArg(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            ordinal = 1
        ),
        index = 0,
        remap = false,
        require = 1
    )
    private static MobEffectInstance jja$configureRepeatSpeedDuration(MobEffectInstance effectInstance) {
        return PlayerPhysicalAbilityProcedureHook.configureSpeedEffect(effectInstance);
    }

    @ModifyArg(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            ordinal = 2
        ),
        index = 0,
        remap = false,
        require = 1
    )
    private static MobEffectInstance jja$configureJumpDuration(
        MobEffectInstance effectInstance,
        @Local(argsOnly = true) Entity entity
    ) {
        return PlayerPhysicalAbilityProcedureHook.configureJumpEffect(entity, effectInstance);
    }

    @ModifyExpressionValue(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;m_19557_()I",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static int jja$shiftSpeedReapplyThresholdForEveryTick(int remainingDuration) {
        return PlayerPhysicalAbilityProcedureHook.adjustProgressiveBuffRemainingDuration(remainingDuration);
    }

    @ModifyExpressionValue(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;m_19557_()I",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static int jja$shiftJumpReapplyThresholdForEveryTick(int remainingDuration) {
        return PlayerPhysicalAbilityProcedureHook.adjustProgressiveBuffRemainingDuration(remainingDuration);
    }

    @WrapOperation(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            ordinal = 4
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$keepPhysicalFlyEffectOnEvenTicks(ServerPlayer serverPlayer, MobEffectInstance effectInstance, Operation<Boolean> original) {
        if (PlayerPhysicalAbilityProcedureHook.shouldApplyFlyEffectThisTick(serverPlayer)) {
            return original.call(serverPlayer, effectInstance);
        }
        return false;
    }

    @Inject(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
        at = @At("TAIL"),
        remap = false,
        require = 1
    )
    private static void jja$applyKaoriFlyEffect(net.minecraftforge.eventbus.api.Event event, Entity entity, CallbackInfo ci) {
        PlayerPhysicalAbilityProcedureHook.applyKaoriFlyEffect(entity);
        PlayerPhysicalAbilityProcedureHook.applyCompletePhysicalGiftedRegeneration(entity);
        PlayerPhysicalAbilityProcedureHook.applyObservedDoubleJumpEffect(entity);
        PlayerPhysicalAbilityProcedureHook.applyGojoProgression(entity);
    }
}
