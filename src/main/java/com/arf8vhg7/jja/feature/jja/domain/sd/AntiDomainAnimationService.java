package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainPresentation;
import net.mcreator.jujutsucraft.init.JujutsucraftModAttributes;
import net.mcreator.jujutsucraft.procedures.PlayAnimationProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

final class AntiDomainAnimationService {
    private static final double PLAY_ANIMATION_1 = -16.0D;
    private static final double CANCEL_ANIMATION_1 = -49.0D;
    private static final double HOLLOW_WICKER_BASKET_ANIMATION_2 = 1.0D;
    private static final double SIMPLE_DOMAIN_ANIMATION_2 = 0.0D;

    private AntiDomainAnimationService() {
    }

    static void onPressStarted(AntiDomainRuntimeState state) {
        if (state == null) {
            return;
        }
        AntiDomainAnimationStateMachine.onPressStarted(state.animation);
    }

    static void onActivationSuccess(AntiDomainRuntimeState state, long gameTime) {
        if (state == null) {
            return;
        }
        AntiDomainAnimationStateMachine.onActivationSuccess(state.animation, gameTime);
    }

    static void replayHoldAnimation(ServerPlayer player, AntiDomainRuntimeState state, AntiDomainPresentation presentation) {
        if (player == null) {
            return;
        }
        setAnimationValues(player, PLAY_ANIMATION_1, presentation == AntiDomainPresentation.HOLLOW_WICKER_BASKET ? HOLLOW_WICKER_BASKET_ANIMATION_2 : SIMPLE_DOMAIN_ANIMATION_2);
        PlayAnimationProcedure.execute(player.level(), player);
        if (state != null) {
            AntiDomainAnimationStateMachine.onHoldAnimationReplay(state.animation);
        }
    }

    static void requestReleaseStop(Entity entity, AntiDomainRuntimeState state) {
        requestStop(entity, state, AntiDomainAnimationStopReason.RELEASE);
    }

    static void requestTerminalStop(Entity entity, AntiDomainRuntimeState state) {
        requestStop(entity, state, AntiDomainAnimationStopReason.TERMINAL);
    }

    static void tickPendingStop(Entity entity) {
        AntiDomainRuntimeState state = AntiDomainRuntimeStore.get(entity);
        if (state == null) {
            return;
        }
        flushPendingStop(entity, state);
        AntiDomainRuntimeStore.removeIfIdle(entity, state);
    }

    private static void requestStop(Entity entity, AntiDomainRuntimeState state, AntiDomainAnimationStopReason stopReason) {
        if (state == null) {
            return;
        }
        if (stopReason == AntiDomainAnimationStopReason.RELEASE) {
            AntiDomainAnimationStateMachine.requestReleaseStop(state.animation);
        } else {
            AntiDomainAnimationStateMachine.requestTerminalStop(state.animation);
        }
        flushPendingStop(entity, state);
    }

    private static void flushPendingStop(Entity entity, AntiDomainRuntimeState state) {
        if (!(entity instanceof ServerPlayer player) || state == null) {
            return;
        }
        if (!AntiDomainAnimationStateMachine.shouldSendPendingCancel(state.animation, player.level().getGameTime())) {
            return;
        }
        setAnimationValues(player, CANCEL_ANIMATION_1, SIMPLE_DOMAIN_ANIMATION_2);
        PlayAnimationProcedure.execute(player.level(), player);
        AntiDomainAnimationStateMachine.onCancelSent(state.animation);
    }

    private static void setAnimationValues(ServerPlayer player, double animation1Value, double animation2Value) {
        if (player.getAttribute(JujutsucraftModAttributes.ANIMATION_1.get()) != null) {
            player.getAttribute(JujutsucraftModAttributes.ANIMATION_1.get()).setBaseValue(animation1Value);
        }
        if (player.getAttribute(JujutsucraftModAttributes.ANIMATION_2.get()) != null) {
            player.getAttribute(JujutsucraftModAttributes.ANIMATION_2.get()).setBaseValue(animation2Value);
        }
    }
}
