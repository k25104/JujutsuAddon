package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import net.mcreator.jujutsucraft.init.JujutsucraftModGameRules;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class RctHealTracker {
    private RctHealTracker() {
    }

    public static void addHealed(Entity entity, double amount) {
        if (!(entity instanceof ServerPlayer player) || amount <= 0.0) {
            return;
        }
        if (!(JjaCommonConfig.RCT_OUTPUT_ENABLED.get()
            || JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get()
            || JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get()
            || JjaCommonConfig.AUTO_RCT_ENABLED.get())) {
            return;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        if (rctState == null) {
            return;
        }
        rctState.addRctHealed(amount);
        awardProgression(player);
    }

    public static void awardProgression(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        boolean outputEnabled = JjaCommonConfig.RCT_OUTPUT_ENABLED.get();
        boolean brainDestructionEnabled = JjaCommonConfig.BRAIN_DESTRUCTION_ENABLED.get();
        boolean brainRegenerationEnabled = JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get();
        boolean autoEnabled = JjaCommonConfig.AUTO_RCT_ENABLED.get();
        if (!(outputEnabled || brainDestructionEnabled || brainRegenerationEnabled || autoEnabled)) {
            return;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(player);
        if (rctState == null) {
            return;
        }
        double healed = rctState.getRctHealed();
        double threshold = getBaseThreshold(player);
        boolean cursedSpirit = RctMath.isCursedSpirit(player);
        if (!cursedSpirit && healed >= threshold && outputEnabled) {
            RctAdvancementHelper.award(player, RctAdvancementHelper.MASTERY_RCT_OUTPUT_ID);
        }
        if (healed >= threshold * 3.0) {
            RctAdvancementHelper.award(player, RctAdvancementHelper.RCT_2_ID);
        }
        if (!cursedSpirit && healed >= threshold * 5.0 && brainDestructionEnabled) {
            RctAdvancementHelper.award(player, RctAdvancementHelper.MASTERY_RCT_BRAIN_DESTRUCTION_ID);
        }
        if (!cursedSpirit && healed >= threshold * 6.0 && brainRegenerationEnabled) {
            RctAdvancementHelper.award(player, RctAdvancementHelper.MASTERY_RCT_BRAIN_REGENERATION_ID);
        }
        if (healed >= threshold * 8.0 && autoEnabled) {
            RctAdvancementHelper.award(player, RctAdvancementHelper.MASTERY_RCT_AUTO_ID);
        }
    }

    private static double getBaseThreshold(ServerPlayer player) {
        int difficulty = player.level().getGameRules().getInt(JujutsucraftModGameRules.JUJUTSUUPGRADEDIFFICULTY);
        return RctProgressionThresholdRules.resolveThreshold(player, difficulty);
    }
}
