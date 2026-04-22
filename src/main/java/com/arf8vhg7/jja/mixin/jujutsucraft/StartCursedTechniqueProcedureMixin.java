package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.hook.jujutsucraft.StartCursedTechniqueProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.StartCursedTechniqueProcedure;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StartCursedTechniqueProcedure.class, remap = false)
public abstract class StartCursedTechniqueProcedureMixin {
    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/MutableComponent;getString()Ljava/lang/String;"
        ),
        slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=jujutsu.technique.attack1"),
            to = @At(value = "CONSTANT", args = "stringValue=jujutsu.technique.choso3")
        ),
        remap = false
    ,
        require = 1
    )
    private static String jja$getTechniqueKeyOrString(MutableComponent component, Operation<String> original) {
        return StartCursedTechniqueProcedureHook.jjaGetTechniqueKeyOrString(component);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;max(DD)D",
            ordinal = 1
        ),
        remap = false
    ,
        require = 1
    )
    private static double jja$scaleCombatCooldownAttackSpeedPenalty(double original, @Local(argsOnly = true) Entity entity) {
        return StartCursedTechniqueProcedureHook.scaleCombatCooldownAttackSpeedPenalty(entity, original);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerCursePower:D",
            opcode = Opcodes.GETFIELD,
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static double jja$useEffectiveCursePowerForNoUseBranch(double original, @Local(argsOnly = true) Entity entity) {
        return StartCursedTechniqueProcedureHook.getEffectiveCursePower(entity, original);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerCursePower:D",
            opcode = Opcodes.GETFIELD,
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static double jja$useEffectiveCursePowerForSpendBranch(double original, @Local(argsOnly = true) Entity entity) {
        return StartCursedTechniqueProcedureHook.getEffectiveCursePower(entity, original);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerSelectCurseTechniqueCost:D",
            opcode = Opcodes.GETFIELD
        ),
        remap = false,
        require = 1
    )
    private static double jja$useRuntimeTechniqueCost(double original, @Local(argsOnly = true) Entity entity) {
        return StartCursedTechniqueProcedureHook.resolveRuntimeTechniqueCost(entity, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerCursePower:D",
            opcode = Opcodes.PUTFIELD
        ),
        remap = false,
        require = 1
    )
    private static void jja$queueTechniqueCost(
        JujutsucraftModVariables.PlayerVariables playerVariables,
        double value,
        Operation<Void> original
    ) {
        StartCursedTechniqueProcedureHook.queueTechniqueCost(playerVariables, value);
    }

    @Inject(
        method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerTechniqueUsedNumber:D",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
        ),
        remap = false,
        require = 1
    )
    private static void jja$refreshCursePowerFormerAfterTechniqueUse(
        net.minecraft.world.level.LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        StartCursedTechniqueProcedureHook.refreshPlayerCursePowerFormer(entity);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/mcreator/jujutsucraft/procedures/ResetCounterProcedure;execute(Lnet/minecraft/world/entity/Entity;)V"
        ),
        remap = false,
        require = 1
    )
    private static void jja$issueSummonEnhancementPending(Entity entity, Operation<Void> original) {
        original.call(entity);
        StartCursedTechniqueProcedureHook.onTechniqueStarted(entity);
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
    private static boolean jja$scaleZoneCooldownTimeDuration(
        LivingEntity entity,
        MobEffectInstance effectInstance,
        Operation<Boolean> original,
        @Local(argsOnly = true) Entity rawEntity
    ) {
        net.minecraft.world.effect.MobEffect effect = java.util.Objects.requireNonNull(effectInstance.getEffect());
        if (effect != JujutsucraftModMobEffects.COOLDOWN_TIME.get()) {
            return original.call(entity, effectInstance);
        }

        int originalDuration = effectInstance.getDuration();
        int scaledDuration = StartCursedTechniqueProcedureHook.scaleCooldownTimeDuration(rawEntity, originalDuration);
        if (scaledDuration == originalDuration) {
            return original.call(entity, effectInstance);
        }

        MobEffectInstance scaledInstance = new MobEffectInstance(
            effect,
            scaledDuration,
            effectInstance.getAmplifier(),
            effectInstance.isAmbient(),
            effectInstance.isVisible(),
            effectInstance.showIcon()
        );
        return original.call(entity, scaledInstance);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/ServerAdvancementManager;m_136041_(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/advancements/Advancement;"
        ),
        remap = false,
        require = 1
    )
    private static Advancement jja$remapTechniqueUsedAdvancement(
        ServerAdvancementManager advancementManager,
        ResourceLocation advancementId,
        Operation<Advancement> original
    ) {
        return original.call(advancementManager, StartCursedTechniqueProcedureHook.remapTechniqueUsedAdvancement(advancementId));
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;m_41720_()Lnet/minecraft/world/item/Item;",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static Item jja$recognizeOffhandMegaphoneDuringTechniqueStart(Item original, @Local(argsOnly = true) Entity entity) {
        return StartCursedTechniqueProcedureHook.resolveHeldLoudspeakerForTechniqueStart(entity, original);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/CompoundTag;m_128379_(Ljava/lang/String;Z)V",
            ordinal = 1
        ),
        remap = false,
        require = 1
    )
    private static void jja$markActualHeldMegaphoneUsed(
        CompoundTag tag,
        String key,
        boolean value,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        if (StartCursedTechniqueProcedureHook.shouldWriteUsedFlagToOriginalStack(entity)) {
            original.call(tag, key, value);
        }
    }
}
