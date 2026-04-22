package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSync;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import java.util.function.BooleanSupplier;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class RctStateService {
    private RctStateService() {
    }

    public static void clearRuntimeState(Entity entity) {
        RctRuntimeStateAccess.clearRuntimeState(entity);
    }

    public static boolean isAutoRctRunning(Entity entity) {
        return RctRuntimeStateAccess.isAutoRctRunning(entity);
    }

    public static void setAutoRctRunning(Entity entity, boolean running) {
        RctRuntimeStateAccess.setAutoRctRunning(entity, running);
    }

    public static boolean isBrainDestructionHolding(Entity entity) {
        return RctRuntimeStateAccess.isBrainDestructionHolding(entity);
    }

    public static void setBrainDestructionHolding(Entity entity, boolean holding) {
        RctRuntimeStateAccess.setBrainDestructionHolding(entity, holding);
    }

    public static int getBrainDestructionTicks(Entity entity) {
        return RctRuntimeStateAccess.getBrainDestructionTicks(entity);
    }

    public static void setBrainDestructionTicks(Entity entity, int ticks) {
        RctRuntimeStateAccess.setBrainDestructionTicks(entity, ticks);
    }

    public static void rememberRctEffectLevel(Entity entity, int effectLevel) {
        RctRuntimeStateAccess.rememberRctEffectLevel(entity, effectLevel);
    }

    public static int resolveAddonRctEffectLevel(LivingEntity entity) {
        if (entity == null) {
            return 0;
        }
        MobEffectInstance rct = entity.getEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get());
        if (rct != null) {
            int effectLevel = rct.getAmplifier();
            rememberRctEffectLevel(entity, effectLevel);
            return effectLevel;
        }
        MobEffectInstance jackpot = entity.getEffect((MobEffect) JujutsucraftModMobEffects.JACKPOT.get());
        if (jackpot != null) {
            int effectLevel = 5 + jackpot.getAmplifier();
            rememberRctEffectLevel(entity, effectLevel);
            return effectLevel;
        }
        return RctRuntimeStateAccess.getRememberedRctEffectLevel(entity);
    }

    public static boolean hasJackpot(LivingEntity entity) {
        return entity != null && entity.hasEffect((MobEffect) JujutsucraftModMobEffects.JACKPOT.get());
    }

    public static boolean isFatigueAffectedChannel(Entity entity) {
        return (JjaCommonConfig.RCT_OUTPUT_ENABLED.get()
            || JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get()
            || JjaCommonConfig.AUTO_RCT_ENABLED.get())
            && entity != null
            && (RctRuntimeStateAccess.isManualPressActive(entity) || isAutoRctRunning(entity));
    }

    public static boolean shouldKeepRctChannelForBrainRegeneration(ServerPlayer player) {
        return RctContextService.canUseBrainRegeneration(player);
    }

    public static boolean isAddonRctChannelActive(ServerPlayer player) {
        if (player == null || !player.isAlive()) {
            return false;
        }
        if (!(JjaCommonConfig.RCT_OUTPUT_ENABLED.get()
            || JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get()
            || JjaCommonConfig.AUTO_RCT_ENABLED.get())) {
            return false;
        }
        if (hasJackpot(player)) {
            return true;
        }
        boolean canKeepWithoutEffect = RctContextService.canKeepRctChannelWithoutEffect(player);
        if (isAutoRctRunning(player)) {
            if (player.hasEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get())) {
                return !RctContextService.isSelfHealComplete(player) || canKeepWithoutEffect;
            }
            return canKeepWithoutEffect;
        }
        if (!RctRuntimeStateAccess.isManualPressActive(player)) {
            return false;
        }
        if (player.hasEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get())) {
            return !RctContextService.isSelfHealComplete(player) || canKeepWithoutEffect;
        }
        if (player.hasEffect((MobEffect) JujutsucraftModMobEffects.CURSED_TECHNIQUE.get())) {
            return false;
        }
        return canKeepWithoutEffect;
    }

    public static boolean canKeepRctOnCtStart(LivingEntity entity) {
        if (hasJackpot(entity)) {
            return true;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(entity);
        return rctState != null && rctState.isAutoRctEnabled();
    }

    public static boolean toggleOutput(ServerPlayer player) {
        if (!JjaCommonConfig.RCT_OUTPUT_ENABLED.get()) {
            return false;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        if (rctState == null) {
            return false;
        }
        return toggleRctState(
            player,
            rctState::isRctOutputEnabled,
            rctState::setRctOutputEnabled,
            () -> RctTechniqueRestrictionRules.canUseOutput(
                RctMath.isCursedSpirit(player),
                RctAdvancementHelper.hasAdvancementOrSukunaEffect(player, RctAdvancementHelper.MASTERY_RCT_OUTPUT_ID),
                true
            ),
            null
        );
    }

    public static boolean isOutputEnabled(ServerPlayer player) {
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        return JjaCommonConfig.RCT_OUTPUT_ENABLED.get() && rctState != null && rctState.isRctOutputEnabled();
    }

    public static boolean toggleBrainRegeneration(ServerPlayer player) {
        if (!JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get()) {
            return false;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        if (rctState == null) {
            return false;
        }
        return toggleRctState(
            player,
            rctState::isBrainRegenerationEnabled,
            rctState::setBrainRegenerationEnabled,
            () -> RctTechniqueRestrictionRules.canUseBrainRegeneration(
                RctMath.isCursedSpirit(player),
                true,
                RctAdvancementHelper.hasAdvancementOrSukunaEffect(player, RctAdvancementHelper.MASTERY_RCT_BRAIN_REGENERATION_ID),
                true
            ),
            null
        );
    }

    public static boolean isBrainRegenerationEnabled(ServerPlayer player) {
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        return JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get() && rctState != null && rctState.isBrainRegenerationEnabled();
    }

    public static boolean toggleAutoRct(ServerPlayer player) {
        if (!JjaCommonConfig.AUTO_RCT_ENABLED.get()) {
            return false;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        if (rctState == null) {
            return false;
        }
        return toggleRctState(
            player,
            rctState::isAutoRctEnabled,
            rctState::setAutoRctEnabled,
            () -> RctAdvancementHelper.hasAdvancement(player, RctAdvancementHelper.MASTERY_RCT_AUTO_ID),
            enabled -> {
                if (!enabled) {
                    RctAutoService.stopAutoRct(player, true);
                }
            }
        );
    }

    public static boolean isAutoRctEnabled(ServerPlayer player) {
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        return JjaCommonConfig.AUTO_RCT_ENABLED.get() && rctState != null && rctState.isAutoRctEnabled();
    }

    public static void showNotMastered(ServerPlayer player) {
        if (player != null) {
            player.displayClientMessage(Component.translatable("jujutsu.message.not_mastered"), false);
        }
    }

    private static boolean toggleRctState(
        ServerPlayer player,
        BooleanSupplier currentValueSupplier,
        BooleanStateSetter setter,
        BooleanSupplier unlockCheck,
        BooleanStateConsumer afterToggle
    ) {
        boolean currentValue = currentValueSupplier.getAsBoolean();
        if (!currentValue && !unlockCheck.getAsBoolean()) {
            showNotMastered(player);
            return false;
        }
        boolean nextValue = !currentValue;
        setter.set(nextValue);
        if (afterToggle != null) {
            afterToggle.accept(nextValue);
        }
        JjaPlayerStateSync.sync(player);
        return true;
    }

    @FunctionalInterface
    private interface BooleanStateSetter {
        void set(boolean value);
    }

    @FunctionalInterface
    private interface BooleanStateConsumer {
        void accept(boolean value);
    }
}
