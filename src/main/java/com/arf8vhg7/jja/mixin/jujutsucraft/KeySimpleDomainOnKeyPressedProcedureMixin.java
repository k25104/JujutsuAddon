package com.arf8vhg7.jja.mixin.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainPressDecision;
import com.arf8vhg7.jja.hook.jujutsucraft.KeySimpleDomainOnKeyPressedProcedureHook;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.KeySimpleDomainOnKeyPressedProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeySimpleDomainOnKeyPressedProcedure.class, remap = false)
public abstract class KeySimpleDomainOnKeyPressedProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void jja$routeSimpleDomainPress(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci,
        @Share("jjaPressDecision") LocalRef<AntiDomainPressDecision> pressDecision
    ) {
        KeySimpleDomainOnKeyPressedProcedureHook.beginPress(entity);
        AntiDomainPressDecision decision = KeySimpleDomainOnKeyPressedProcedureHook.resolvePressDecision(entity);
        pressDecision.set(decision);
        if (decision == AntiDomainPressDecision.PRE_CLEAR_FBE) {
            KeySimpleDomainOnKeyPressedProcedureHook.prepareFbeReplacement(entity);
        }
        if (KeySimpleDomainOnKeyPressedProcedureHook.shouldCancelUpstreamPress(decision)) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;round(D)J"
        ),
        remap = false,
        require = 1
    )
    private static long jja$useRawSimpleDomainCost(long original) {
        return KeySimpleDomainOnKeyPressedProcedureHook.resolveRuntimeCost(original);
    }

    @ModifyExpressionValue(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancements/AdvancementProgress;m_8193_()Z"
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$allowHwbWithoutSimpleDomainMastery(boolean original, @Local(argsOnly = true) Entity entity) {
        return KeySimpleDomainOnKeyPressedProcedureHook.resolveSimpleDomainMastery(original, entity);
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
    private static double jja$useEffectiveCursePowerForSimpleDomain(double original, @Local(argsOnly = true) Entity entity) {
        return KeySimpleDomainOnKeyPressedProcedureHook.getEffectiveCursePower(entity, original);
    }

    @WrapOperation(
        method = "lambda$execute$0",
        at = @At(
            value = "FIELD",
            target = "Lnet/mcreator/jujutsucraft/network/JujutsucraftModVariables$PlayerVariables;PlayerCursePower:D",
            opcode = Opcodes.PUTFIELD
        ),
        remap = false,
        require = 1
    )
    private static void jja$queueSimpleDomainCost(
        JujutsucraftModVariables.PlayerVariables playerVariables,
        double value,
        Operation<Void> original
    ) {
        KeySimpleDomainOnKeyPressedProcedureHook.queueSimpleDomainCost(playerVariables, value);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;m_6144_()Z",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$resolveFbeSelectionCrouchCheck(
        Entity entity,
        Operation<Boolean> original,
        @Share("jjaPressDecision") LocalRef<AntiDomainPressDecision> pressDecision
    ) {
        if (pressDecision.get() == null) {
            KeySimpleDomainOnKeyPressedProcedureHook.beginPress(entity);
            pressDecision.set(KeySimpleDomainOnKeyPressedProcedureHook.resolvePressDecision(entity));
        }
        return KeySimpleDomainOnKeyPressedProcedureHook.shouldUseFbeBranch(original.call(entity), entity);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;m_237115_(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
        ),
        remap = false,
        require = 1
    )
    private static MutableComponent jja$replaceSimpleDomainMessageLabel(
        String translationKey,
        Operation<MutableComponent> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return KeySimpleDomainOnKeyPressedProcedureHook.resolveSimpleDomainTechniqueLabel(entity, translationKey, original.call(translationKey));
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V"
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/entity/LivingEntity;m_21195_(Lnet/minecraft/world/effect/MobEffect;)Z"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/server/MinecraftServer;m_129892_()Lnet/minecraft/commands/Commands;",
                ordinal = 0
            )
        ),
        remap = false,
        require = 1
    )
    private static void jja$sendSimpleDomainOffMessage(
        Player player,
        Component component,
        boolean actionBar,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        original.call(player, KeySimpleDomainOnKeyPressedProcedureHook.buildSimpleDomainStateMessage(entity, false), actionBar);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V",
            ordinal = 0
        ),
        slice = @Slice(
            from = @At(
                value = "CONSTANT",
                args = "stringValue=jujutsu.message.dont_use",
                ordinal = 0
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/server/MinecraftServer;m_129892_()Lnet/minecraft/commands/Commands;",
                ordinal = 1
            )
        ),
        remap = false,
        require = 1
    )
    private static void jja$sendDontUseMessageOnCursePowerShortage(
        Player player,
        Component component,
        boolean actionBar,
        Operation<Void> original
    ) {
        original.call(player, KeySimpleDomainOnKeyPressedProcedureHook.buildDontUseMessage(), actionBar);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V",
            ordinal = 0
        ),
        slice = @Slice(
            from = @At(
                value = "CONSTANT",
                args = "stringValue=jujutsu.message.not_mastered"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/server/MinecraftServer;m_129892_()Lnet/minecraft/commands/Commands;",
                ordinal = 1
            )
        ),
        remap = false,
        require = 1
    )
    private static void jja$sendNotMasteredMessage(
        Player player,
        Component component,
        boolean actionBar,
        Operation<Void> original
    ) {
        original.call(player, KeySimpleDomainOnKeyPressedProcedureHook.buildNotMasteredMessage(), actionBar);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V"
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/entity/LivingEntity;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
                ordinal = 1
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/mcreator/jujutsucraft/procedures/PlayAnimationProcedure;execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V"
            )
        ),
        remap = false,
        require = 1
    )
    private static void jja$sendSimpleDomainOnMessage(
        Player player,
        Component component,
        boolean actionBar,
        Operation<Void> original,
        @Local(argsOnly = true) Entity entity
    ) {
        original.call(player, KeySimpleDomainOnKeyPressedProcedureHook.buildSimpleDomainStateMessage(entity, true), actionBar);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V",
            ordinal = 5
        ),
        remap = false,
        require = 1
    )
    private static void jja$sendDontUseMessageOnBlockedPress(
        Player player,
        Component component,
        boolean actionBar,
        Operation<Void> original
    ) {
        original.call(player, KeySimpleDomainOnKeyPressedProcedureHook.buildDontUseMessage(), actionBar);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V",
            ordinal = 0
        ),
        remap = false,
        require = 1
    )
    private static void jja$sendFbeMessage(
        Player player,
        Component component,
        boolean actionBar,
        Operation<Void> original
    ) {
        original.call(player, KeySimpleDomainOnKeyPressedProcedureHook.buildFallingBlossomEmotionMessage(), actionBar);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/EntityType;m_204039_(Lnet/minecraft/tags/TagKey;)Z",
            ordinal = 3
        ),
        remap = false,
        require = 1
    )
    private static boolean jja$forceHollowWickerBasketAnimation(
        EntityType<?> entityType,
        TagKey<EntityType<?>> tagKey,
        Operation<Boolean> original,
        @Local(argsOnly = true) Entity entity
    ) {
        return KeySimpleDomainOnKeyPressedProcedureHook.resolveHollowWickerBasketAnimationCondition(entity, original.call(entityType, tagKey));
    }

    @Inject(method = "execute", at = @At("TAIL"), remap = false, require = 1)
    private static void jja$finishSimpleDomainPress(
        LevelAccessor world,
        double x,
        double y,
        double z,
        Entity entity,
        CallbackInfo ci
    ) {
        KeySimpleDomainOnKeyPressedProcedureHook.finishPress(entity);
    }
}
