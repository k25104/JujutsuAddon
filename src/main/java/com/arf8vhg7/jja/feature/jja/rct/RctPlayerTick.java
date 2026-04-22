package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.init.JujutsucraftModParticleTypes;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public final class RctPlayerTick {
    private static final TagKey<net.minecraft.world.entity.EntityType<?>> NOT_LIVING_TAG =
        TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("forge", "not_living"));

    private RctPlayerTick() {
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Player player) {
        if (player == null || player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!(JjaCommonConfig.RCT_OUTPUT_ENABLED.get()
            || JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get()
            || JjaCommonConfig.AUTO_RCT_ENABLED.get())) {
            return;
        }
        if (player.getType().is(NOT_LIVING_TAG)) {
            player.removeEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get());
            return;
        }
        if (!player.isAlive()) {
            return;
        }
        int effectLevel = RctEffectSupport.getRctEffectLevel(player);
        double amount = 12.5 + Math.abs(effectLevel) * 7.5;
        boolean manualPress = RctRuntimeStateAccess.isManualPressActive(player);
        boolean autoRunning = RctStateService.isAutoRctRunning(player);
        boolean hasJackpot = RctStateService.hasJackpot(player);
        boolean affectedByFatigue = manualPress || autoRunning;
        double fatigueFactor = RctMath.getFatigueFactor(player);
        boolean cursedSpirit = RctMath.isCursedSpirit(player);
        boolean heal = cursedSpirit ? effectLevel < 0 : true;
        boolean keepChannelWithoutEffect = RctContextService.canKeepRctChannelWithoutEffect(serverPlayer);
        boolean using = false;
        double selfEquivalentHeal = 0.0;
        boolean reachedFullHeal = false;
        RctStateService.rememberRctEffectLevel(player, effectLevel);

        if (heal && RctChannelTransitionResolver.shouldStopSelfHealing(
            hasJackpot,
            RctContextService.isSelfHealComplete(serverPlayer),
            keepChannelWithoutEffect
        )) {
            stopHealingChannel(serverPlayer, autoRunning);
            return;
        }

        if (heal) {
            if (!RctContextService.isSelfHealComplete(serverPlayer)) {
                selfEquivalentHeal = RctMath.getSelfRctFinalAmount(player, effectLevel, affectedByFatigue);
                RctEffectSupport.applyJjcHeal(player, selfEquivalentHeal);
                if (RctHealTrackingRules.shouldTrackSelfHeal(manualPress, autoRunning)) {
                    RctHealTracker.addHealed(player, Math.abs(selfEquivalentHeal));
                }
                reachedFullHeal = RctChannelTransitionResolver.shouldStopSelfHealing(
                    hasJackpot,
                    RctContextService.isSelfHealComplete(serverPlayer)
                );
                using = true;
            }
        } else {
            double selfDamage = player.getMaxHealth() * 0.25D;
            RctEffectSupport.applyJjcHealthDelta(player, -selfDamage);
            using = true;
        }

        if (using) {
            if (effectLevel >= 0 && Math.random() < 1.0 / fatigueFactor && world instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                    (SimpleParticleType) JujutsucraftModParticleTypes.PARTICLE_CURSE_POWER_WHITE.get(),
                    x,
                    y + player.getBbHeight() * 0.5,
                    z,
                    1,
                    player.getBbWidth() * 0.25,
                    player.getBbHeight() * 0.25,
                    player.getBbWidth() * 0.25,
                    0.1
                );
            }
            if (!player.hasEffect((MobEffect) JujutsucraftModMobEffects.CURSED_TECHNIQUE.get()) && player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                RctEffectSupport.reduceEffectDuration(player, MobEffects.MOVEMENT_SLOWDOWN, 4.0 * amount * 0.1);
            }
            if (manualPress || autoRunning) {
                JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
                JjaCursePowerAccountingService.queueSpentPower(variables, RctMath.getActiveCeCost(player));
                if (!RctContextService.passesActiveTickCondition(serverPlayer)) {
                    if (autoRunning) {
                        RctAutoService.handleActiveTickFailure(serverPlayer);
                    } else {
                        player.removeEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get());
                    }
                }
                RctFatigueHelper.addRctFatigueIfPresent(player, RctFatigueConfig.getRctFatigueRate());
            }
        }

        if (reachedFullHeal) {
            if (!RctContextService.canKeepRctChannelWithoutEffect(serverPlayer)) {
                stopHealingChannel(serverPlayer, autoRunning);
            }
        }
    }

    private static void stopHealingChannel(ServerPlayer player, boolean autoRunning) {
        RctRuntimeStateAccess.setManualPressActive(player, false);
        if (autoRunning) {
            RctAutoService.stopAutoRct(player, true);
            return;
        }
    }
}
