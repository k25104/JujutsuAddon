package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.feature.jja.domain.fbe.FallingBlossomEmotionEffectService;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainPresentation;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueOption;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class SimpleDomainHoldService {
    private SimpleDomainHoldService() {
    }

    public static void beginPress(Player player) {
        if (player == null) {
            return;
        }
        AntiDomainRuntimeState state = AntiDomainRuntimeStore.getOrCreate(player);
        state.press.keyHeld = true;
        state.press.selectedOptionAtPress = AntiDomainTechniqueService.getCurrentSelection(player);
        state.animation.pressStartedTick = player.level().getGameTime();
        AntiDomainAnimationService.onPressStarted(state);
    }

    public static AntiDomainPressDecision resolvePressDecision(Entity entity) {
        if (!(entity instanceof Player player)) {
            return AntiDomainPressDecision.PASS_THROUGH;
        }
        AntiDomainRuntimeState state = AntiDomainRuntimeStore.getOrCreate(player);
        AntiDomainTechniqueOption selectedOption = getSelectedOptionAtPress(player, state);
        boolean ownsSimpleDomain = AntiDomainTechniqueRules.hasOwnedSimpleDomain(player);
        AntiDomainPressDecision decision = resolvePressDecision(selectedOption, ownsSimpleDomain, FallingBlossomEmotionEffectService.canActivate(player));
        if (decision == AntiDomainPressDecision.CANCEL_HOLD) {
            syncActivePresentation(player, state);
            armHold(state.session, AntiDomainEffectService.resolveCurrentSimpleDomainAmplifier(player));
        }
        return decision;
    }

    public static boolean shouldCancelUpstreamPress(AntiDomainPressDecision decision) {
        return decision != null && decision.cancelUpstream();
    }

    public static boolean shouldUseFbeBranch(boolean original, Entity entity) {
        if (!(entity instanceof Player)) {
            return original;
        }
        return getSelectedOptionAtPress(entity) == AntiDomainTechniqueOption.FALLING_BLOSSOM_EMOTION;
    }

    public static void prepareFbeReplacement(Entity entity) {
        if (entity == null) {
            return;
        }
        AntiDomainRuntimeState state = AntiDomainRuntimeStore.getOrCreate(entity);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.removeEffect(JujutsucraftModMobEffects.SIMPLE_DOMAIN.get());
        }
        clearOwnedState(state.session);
        AntiDomainAnimationService.requestTerminalStop(entity, state);
        if (!entity.level().isClientSide()) {
            AntiDomainTechniqueService.clearActivePresentation(entity);
        }
        AntiDomainRuntimeStore.removeIfIdle(entity, state);
    }

    public static void finishPress(Entity entity) {
        AntiDomainRuntimeState state = AntiDomainRuntimeStore.get(entity);
        if (!(entity instanceof Player player) || state == null) {
            return;
        }
        MobEffectInstance simpleDomain = AntiDomainEffectService.getSimpleDomainEffect(player);
        if (simpleDomain != null && simpleDomain.getAmplifier() > 0) {
            finishSuccessfulPress(state, resolveActivationPresentation(player, state), player.level().getGameTime(), simpleDomain.getAmplifier());
            if (!player.level().isClientSide()) {
                AntiDomainTechniqueService.latchActivePresentation(player, state.session.activePresentation);
            }
            return;
        }
        finishFailedPress(state);
        AntiDomainRuntimeStore.removeIfIdle(player, state);
    }

    public static void onRelease(Entity entity) {
        AntiDomainRuntimeState state = AntiDomainRuntimeStore.get(entity);
        if (state == null) {
            return;
        }
        applyRelease(state, AntiDomainTechniqueRules.shouldAutoExtendHollowWickerBasket(entity, state.session.activePresentation));
        AntiDomainAnimationService.requestReleaseStop(entity, state);
        AntiDomainRuntimeStore.removeIfIdle(entity, state);
    }

    public static void onExpire(Entity entity) {
        AntiDomainRuntimeState state = AntiDomainRuntimeStore.get(entity);
        if (state == null) {
            AntiDomainTechniqueService.clearActivePresentation(entity);
            return;
        }
        applyExpire(state);
        AntiDomainAnimationService.requestTerminalStop(entity, state);
        if (!entity.level().isClientSide()) {
            AntiDomainTechniqueService.clearActivePresentation(entity);
        }
        AntiDomainRuntimeStore.removeIfIdle(entity, state);
    }

    public static void clearRuntimeState(Entity entity) {
        AntiDomainRuntimeState state = AntiDomainRuntimeStore.get(entity);
        if (state != null) {
            AntiDomainAnimationStateMachine.resetHoldRun(state.animation);
            AntiDomainAnimationService.requestTerminalStop(entity, state);
        }
        AntiDomainTechniqueService.clearActivePresentation(entity);
        AntiDomainRuntimeStore.remove(entity);
    }

    public static void extendHoldOnActiveTick(Entity entity) {
        AntiDomainHoldTickService.extendHoldOnActiveTick(entity);
    }

    public static void tickPendingAnimationStop(Entity entity) {
        AntiDomainAnimationService.tickPendingStop(entity);
    }

    public static AntiDomainPresentation getRuntimeActivePresentation(Entity entity) {
        return AntiDomainRuntimeStore.getRuntimeActivePresentation(entity);
    }

    static AntiDomainTechniqueOption getRuntimeSelectedOptionAtPress(Entity entity) {
        return AntiDomainRuntimeStore.getRuntimeSelectedOptionAtPress(entity);
    }

    static AntiDomainPressDecision resolvePressDecision(
        AntiDomainTechniqueOption selectedOption,
        boolean ownsSimpleDomain,
        boolean canActivateFbe
    ) {
        if (selectedOption == null || selectedOption == AntiDomainTechniqueOption.NONE) {
            return ownsSimpleDomain ? AntiDomainPressDecision.RELEASE_ONLY : AntiDomainPressDecision.CANCEL_NOOP;
        }
        if (selectedOption == AntiDomainTechniqueOption.FALLING_BLOSSOM_EMOTION) {
            if (!canActivateFbe) {
                return AntiDomainPressDecision.CANCEL_NOOP;
            }
            return ownsSimpleDomain ? AntiDomainPressDecision.PRE_CLEAR_FBE : AntiDomainPressDecision.PASS_THROUGH;
        }
        return ownsSimpleDomain ? AntiDomainPressDecision.CANCEL_HOLD : AntiDomainPressDecision.PASS_THROUGH;
    }

    static void finishSuccessfulPress(
        AntiDomainRuntimeState state,
        AntiDomainPresentation activatedPresentation,
        long gameTime,
        int holdAmplifier
    ) {
        state.session.activePresentation = activatedPresentation;
        armHold(state.session, holdAmplifier);
        AntiDomainAnimationService.onActivationSuccess(state, gameTime);
    }

    static void finishFailedPress(AntiDomainRuntimeState state) {
        clearOwnedState(state.session);
    }

    static void applyRelease(AntiDomainRuntimeState state, boolean preserveHoldRequest) {
        state.press.keyHeld = false;
        AntiDomainAnimationStateMachine.resetHoldRun(state.animation);
        if (!preserveHoldRequest) {
            clearHoldRequest(state.session);
        }
    }

    static void applyExpire(AntiDomainRuntimeState state) {
        AntiDomainAnimationStateMachine.resetHoldRun(state.animation);
        clearOwnedState(state.session);
    }

    private static AntiDomainTechniqueOption getSelectedOptionAtPress(Entity entity) {
        AntiDomainRuntimeState state = AntiDomainRuntimeStore.get(entity);
        return getSelectedOptionAtPress(entity, state);
    }

    private static AntiDomainTechniqueOption getSelectedOptionAtPress(Entity entity, AntiDomainRuntimeState state) {
        AntiDomainTechniqueOption selectedOption = state != null ? state.press.selectedOptionAtPress : null;
        return selectedOption != null ? selectedOption : AntiDomainTechniqueService.getCurrentSelection(entity);
    }

    private static AntiDomainPresentation resolveActivationPresentation(Entity entity, AntiDomainRuntimeState state) {
        AntiDomainPresentation selectedPresentation = AntiDomainPresentationResolver.resolveSelection(state.press.selectedOptionAtPress);
        return selectedPresentation != AntiDomainPresentation.NONE
            ? selectedPresentation
            : AntiDomainPresentationResolver.resolveSelection(AntiDomainTechniqueService.getCurrentSelection(entity));
    }

    private static void syncActivePresentation(Entity entity, AntiDomainRuntimeState state) {
        if (state == null) {
            return;
        }
        state.session.activePresentation = AntiDomainTechniqueService.resolveActivePresentation(entity);
    }

    private static void armHold(AntiDomainActiveSessionState session, int holdAmplifier) {
        session.holdRequested = true;
        session.holdAmplifier = holdAmplifier;
    }

    private static void clearHoldRequest(AntiDomainActiveSessionState session) {
        session.holdRequested = false;
        session.holdAmplifier = 0;
    }

    private static void clearOwnedState(AntiDomainActiveSessionState session) {
        session.activePresentation = AntiDomainPresentation.NONE;
        clearHoldRequest(session);
    }
}
