package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainDefenseAttributeService;
import com.arf8vhg7.jja.feature.jja.domain.fbe.FallingBlossomEmotionProgression;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainPressDecision;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueOption;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueService;
import com.arf8vhg7.jja.feature.jja.domain.sd.SimpleDomainHoldService;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;

public final class KeySimpleDomainOnKeyPressedProcedureHook {
    private KeySimpleDomainOnKeyPressedProcedureHook() {
    }

    public static void beginPress(Entity entity) {
        if (entity instanceof Player player) {
            SimpleDomainHoldService.beginPress(player);
        }
    }

    public static AntiDomainPressDecision resolvePressDecision(Entity entity) {
        return SimpleDomainHoldService.resolvePressDecision(entity);
    }

    public static long resolveRuntimeCost(long original) {
        return 50L;
    }

    public static double getEffectiveCursePower(Entity entity, double originalCursePower) {
        return JjaCursePowerAccountingService.resolveEffectivePower(entity, originalCursePower);
    }

    public static void queueSimpleDomainCost(JujutsucraftModVariables.PlayerVariables playerVariables, double resultingCursePower) {
        JjaCursePowerAccountingService.queueSpentPowerFromResult(playerVariables, playerVariables != null ? playerVariables.PlayerCursePower : 0.0D, resultingCursePower);
    }

    static double resolveQueuedSimpleDomainCost(double currentCursePower, double resultingCursePower) {
        return Math.max(currentCursePower - resultingCursePower, 0.0D);
    }

    public static boolean shouldCancelUpstreamPress(AntiDomainPressDecision decision) {
        return SimpleDomainHoldService.shouldCancelUpstreamPress(decision);
    }

    public static boolean shouldUseFbeBranch(boolean original, Entity entity) {
        return SimpleDomainHoldService.shouldUseFbeBranch(original, entity);
    }

    public static void prepareFbeReplacement(Entity entity) {
        SimpleDomainHoldService.prepareFbeReplacement(entity);
    }

    public static boolean resolveSimpleDomainMastery(boolean original, Entity entity) {
        if (!(entity instanceof Player player)) {
            return original;
        }
        boolean canActivateFallingBlossomEmotion = player instanceof ServerPlayer serverPlayer
            && FallingBlossomEmotionProgression.hasUnlocked(serverPlayer);
        return resolveSimpleDomainMastery(
            original,
            AntiDomainTechniqueService.getCurrentSelection(player),
            AntiDomainTechniqueService.canUseHollowWickerBasket(player),
            canActivateFallingBlossomEmotion
        );
    }

    public static void finishPress(Entity entity) {
        SimpleDomainHoldService.finishPress(entity);
        if (entity instanceof LivingEntity livingEntity) {
            DomainDefenseAttributeService.sync(livingEntity);
        }
    }

    public static MutableComponent resolveSimpleDomainTechniqueLabel(Entity entity, String translationKey, MutableComponent original) {
        if (!"effect.simple_domain".equals(translationKey)) {
            return original;
        }
        return AntiDomainTechniqueService.getResolvedSimpleDomainTechniqueName(entity);
    }

    public static MutableComponent buildSimpleDomainStateMessage(Entity entity, boolean active) {
        return buildTechniqueStateMessage(resolveSimpleDomainStateLabel(entity, active), active);
    }

    public static MutableComponent buildDontUseMessage() {
        return Component.translatable("jujutsu.message.dont_use");
    }

    public static MutableComponent buildNotMasteredMessage() {
        return Component.translatable("jujutsu.message.not_mastered");
    }

    public static MutableComponent buildFallingBlossomEmotionMessage() {
        return Component.translatable("effect.jujutsucraft.falling_blossom_emotion").withStyle(ChatFormatting.BOLD);
    }

    public static boolean resolveHollowWickerBasketAnimationCondition(Entity entity, boolean original) {
        return AntiDomainTechniqueService.shouldUseHwbVisual(entity, original);
    }

    static MutableComponent resolveSimpleDomainStateLabel(Entity entity, boolean active) {
        return active
            ? AntiDomainTechniqueService.getResolvedSimpleDomainTechniqueName(entity)
            : AntiDomainTechniqueService.getLastActivatedSimpleDomainTechniqueName(entity);
    }

    static boolean resolveSimpleDomainMastery(
        boolean original,
        AntiDomainTechniqueOption selectedOption,
        boolean canUseHollowWickerBasket,
        boolean canActivateFallingBlossomEmotion
    ) {
        return switch (selectedOption != null ? selectedOption : AntiDomainTechniqueOption.NONE) {
            case HOLLOW_WICKER_BASKET -> canUseHollowWickerBasket;
            case FALLING_BLOSSOM_EMOTION -> canActivateFallingBlossomEmotion;
            case SIMPLE_DOMAIN, NONE -> original;
        };
    }

    static MutableComponent buildTechniqueStateMessage(MutableComponent techniqueLabel, boolean active) {
        MutableComponent message = techniqueLabel == null ? Component.empty() : techniqueLabel.copy();
        message.append(Component.literal(": "));
        message.append(Component.literal(Boolean.toString(active)));
        return message;
    }
}
