package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.PlayerTickEventProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.PlayerTickEventProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerTickEventProcedure.class, remap = false)
public abstract class PlayerTickEventProcedureMixin {
    @ModifyExpressionValue(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;m_21223_()F"
        ),
        remap = false
    ,
        require = 1
    )
    private static float jja$useFirstAidAwareHealthForCeRecovery(float currentHealth, @Local(argsOnly = true) Entity entity) {
        return PlayerTickEventProcedureHook.getEffectiveHealth(entity, currentHealth);
    }

    @ModifyConstant(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        constant = @Constant(intValue = 10),
        remap = false
    ,
        require = 1
    )
    private static int jja$modifyTickInterval(int original) {
        return PlayerTickEventProcedureHook.modifyTickInterval(original);
    }

    @WrapOperation(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;min(DD)D"
        ),
        remap = false
    ,
        require = 1
    )
    private static double jja$normalizeCursePowerChangeBeforeApplying(
        double powerAfterQueuedChange,
        double maxPower,
        Operation<Double> original,
        @Local(index = 11) double healCursePower,
        @Local(index = 14) JujutsucraftModVariables.PlayerVariables playerVars,
        @Local(argsOnly = true) Entity entity
    ) {
        double normalizedPowerAfterQueuedChange = PlayerTickEventProcedureHook.normalizeAppliedCursePowerChange(
            entity,
            playerVars,
            powerAfterQueuedChange,
            healCursePower
        );
        return original.call(normalizedPowerAfterQueuedChange, maxPower);
    }

    @Inject(
        method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At("TAIL"),
        remap = false
    ,
        require = 1
    )
    private static void jja$applyCTStepHeight(
        Event event,
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        PlayerTickEventProcedureHook.applyCTStepHeight(entity);
        PlayerTickEventProcedureHook.tickSimpleDomainAnimationStop(entity);
    }
}
