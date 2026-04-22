package com.arf8vhg7.jja.feature.jja.domain.fbe;

import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import com.arf8vhg7.jja.feature.player.state.AddonStatCounter;
import com.arf8vhg7.jja.feature.player.state.AddonStatsAccess;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class FallingBlossomEmotionTickService {
    public static final double CURSE_POWER_DRAIN_PER_TICK = 1.0D;

    private FallingBlossomEmotionTickService() {
    }

    public static void onActiveTick(Entity entity) {
        queueCursePowerDrain(entity);
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        MobEffectInstance effectInstance = livingEntity.getEffect(JujutsucraftModMobEffects.FALLING_BLOSSOM_EMOTION.get());
        if (effectInstance == null) {
            return;
        }
        AddonStatsAccess.incrementCounter(entity, AddonStatCounter.FBE_USED);
    }

    public static void queueCursePowerDrain(Entity entity) {
        if (!shouldDrainCursePowerEachTick(entity)) {
            return;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables =
            entity.getCapability(JujutsucraftModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(null);
        JjaCursePowerAccountingService.queueSpentPower(playerVariables, CURSE_POWER_DRAIN_PER_TICK);
    }

    public static boolean shouldSkipUpstreamBurstDrain(Entity entity) {
        return shouldDrainCursePowerEachTick(entity);
    }

    public static boolean shouldDrainCursePowerEachTick(Entity entity) {
        return entity instanceof Player
            && entity instanceof LivingEntity livingEntity
            && shouldDrainCursePowerEachTick(true, livingEntity.hasEffect(JujutsucraftModMobEffects.SIX_EYES.get()));
    }

    public static boolean shouldDrainCursePowerEachTick(boolean player, boolean hasSixEyes) {
        return player && !hasSixEyes;
    }
}
