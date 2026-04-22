package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.server.level.ServerPlayer;

public final class RctAddonTickService {
    private RctAddonTickService() {
    }

    public static void tick(ServerPlayer player) {
        if (player == null || !RctStateService.isAddonRctChannelActive(player)) {
            return;
        }
        boolean shouldApplyOutput = JjaCommonConfig.RCT_OUTPUT_ENABLED.get() && RctContextService.canApplyOutput(player);
        boolean shouldApplyBrainRegeneration = JjaCommonConfig.BRAIN_REGENERATION_ENABLED.get() && RctContextService.canUseBrainRegeneration(player);
        if (!shouldApplyOutput && !shouldApplyBrainRegeneration) {
            return;
        }
        int effectLevel = RctStateService.resolveAddonRctEffectLevel(player);
        double rctMultiplier = RctMath.getRctMultiplier(player, effectLevel, RctStateService.isFatigueAffectedChannel(player));

        int outputCount = shouldApplyOutput ? RctOutputService.applyPlayerOutput(player.level(), player) : 0;
        if (outputCount > 0) {
            JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
            JjaCursePowerAccountingService.queueSpentPower(variables, RctMath.getActiveCeCost(player) * outputCount);
            RctFatigueHelper.addRctFatigueIfPresent(player, RctFatigueConfig.resolveRctFatigueTicks(outputCount));
        }
        if (shouldApplyBrainRegeneration && RctBrainService.applyBrainRegeneration(player, rctMultiplier)) {
            JujutsucraftModVariables.PlayerVariables variables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
            JjaCursePowerAccountingService.queueSpentPower(variables, RctMath.getActiveCeCost(player));
        }
    }
}
