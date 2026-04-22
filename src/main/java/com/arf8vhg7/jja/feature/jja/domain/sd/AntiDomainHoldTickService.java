package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainPresentation;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.ActiveTickConditionProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;

final class AntiDomainHoldTickService {
    private static final int HOLD_SLOWNESS_START_DURATION = 20;
    private static final int HOLD_SLOWNESS_CONTINUE_DURATION = 5;
    private static final int HOLD_SLOWNESS_AMPLIFIER = 6;

    private AntiDomainHoldTickService() {
    }

    static void extendHoldOnActiveTick(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        AntiDomainRuntimeState state = AntiDomainRuntimeStore.get(player);
        MobEffectInstance simpleDomain = AntiDomainEffectService.getSimpleDomainEffect(player);
        if (state == null || simpleDomain == null || simpleDomain.getAmplifier() <= 0) {
            return;
        }

        state.session.activePresentation = AntiDomainTechniqueService.resolveActivePresentation(player);
        HoldTickPlan plan = planHoldExtension(
            state.session,
            state.press.keyHeld,
            AntiDomainTechniqueRules.shouldAutoExtendHollowWickerBasket(player, state.session.activePresentation),
            ActiveTickConditionProcedure.execute(player),
            player.hasEffect(JujutsucraftModMobEffects.CURSED_TECHNIQUE.get()),
            simpleDomain.getDuration(),
            simpleDomain.getAmplifier()
        );
        boolean holdRunStartedThisTick = updateHoldRunState(state.animation, plan, player.level().getGameTime());
        if (shouldReplayHoldAnimation(plan, holdRunStartedThisTick)) {
            AntiDomainAnimationService.replayHoldAnimation(player, state, state.session.activePresentation);
        }
        if (!plan.extensionSucceeded()) {
            return;
        }

        player.addEffect(new MobEffectInstance(JujutsucraftModMobEffects.SIMPLE_DOMAIN.get(), plan.nextDuration(), plan.holdAmplifier(), true, true));
        AntiDomainEffectService.queueSimpleDomainExtensionCost(player);
        onExtensionApplied(state.session, plan);
        if (shouldApplyHoldSlowness(state.session.activePresentation)) {
            player.addEffect(
                new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,
                    resolveHoldSlownessDuration(holdRunStartedThisTick),
                    HOLD_SLOWNESS_AMPLIFIER,
                    false,
                    false
                )
            );
        }
    }

    static HoldTickPlan planHoldExtension(
        AntiDomainActiveSessionState session,
        boolean keyHeld,
        boolean autoExtend,
        boolean activeTickAllowed,
        boolean pausedByCursedTechnique,
        int currentDuration,
        int currentAmplifier
    ) {
        boolean mayAttemptExtension = AntiDomainHoldExtensionPolicy.shouldAttemptExtension(
            session.holdRequested,
            keyHeld,
            autoExtend,
            activeTickAllowed,
            pausedByCursedTechnique
        );
        if (!mayAttemptExtension) {
            return new HoldTickPlan(false, false, currentAmplifier, currentDuration);
        }

        int holdAmplifier = session.holdAmplifier > 0 ? session.holdAmplifier : currentAmplifier;
        int nextDuration = AntiDomainHoldExtensionPolicy.computeNextDuration(currentDuration, holdAmplifier);
        boolean extensionSucceeded = nextDuration > currentDuration;
        return new HoldTickPlan(mayAttemptExtension, extensionSucceeded, holdAmplifier, nextDuration);
    }

    static boolean updateHoldRunState(AntiDomainAnimationState animation, HoldTickPlan plan, long gameTime) {
        return plan != null && AntiDomainAnimationStateMachine.updateHoldRun(animation, plan.mayAttemptExtension(), gameTime);
    }

    static boolean shouldReplayHoldAnimation(HoldTickPlan plan, boolean holdRunStartedThisTick) {
        return plan != null && plan.extensionSucceeded() && holdRunStartedThisTick;
    }

    static void onExtensionApplied(AntiDomainActiveSessionState session, HoldTickPlan plan) {
        if (session == null || plan == null || !plan.extensionSucceeded()) {
            return;
        }
        session.holdRequested = true;
        session.holdAmplifier = plan.holdAmplifier();
    }

    static boolean shouldApplyHoldSlowness(AntiDomainPresentation presentation) {
        return presentation != AntiDomainPresentation.HOLLOW_WICKER_BASKET;
    }

    static int resolveHoldSlownessDuration(boolean holdRunStartedThisTick) {
        return holdRunStartedThisTick ? HOLD_SLOWNESS_START_DURATION : HOLD_SLOWNESS_CONTINUE_DURATION;
    }

    record HoldTickPlan(
        boolean mayAttemptExtension,
        boolean extensionSucceeded,
        int holdAmplifier,
        int nextDuration
    ) {
    }
}
