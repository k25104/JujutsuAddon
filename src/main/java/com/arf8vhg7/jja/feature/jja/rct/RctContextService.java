package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.ActiveTickConditionProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public final class RctContextService {
    private RctContextService() {
    }

    public static boolean isReviveWaiting(ServerPlayer player) {
        return player != null && ReviveFlowService.isWaiting(player);
    }

    public static boolean hasCurseEnergy(ServerPlayer player) {
        return player != null && RctMath.hasCurseEnergy(player);
    }

    public static boolean passesActiveTickCondition(ServerPlayer player) {
        return player != null && ActiveTickConditionProcedure.execute(player);
    }

    public static boolean shouldKeepBrainRegeneration(ServerPlayer player) {
        return RctStateService.shouldKeepRctChannelForBrainRegeneration(player);
    }

    public static boolean isSelfHealComplete(ServerPlayer player) {
        return player != null && RctHealGate.isRctFullyHealed(player) && !shouldKeepBrainRegeneration(player);
    }

    public static boolean shouldStopSelfHealing(ServerPlayer player, boolean hasJackpot) {
        return RctChannelTransitionResolver.shouldStopSelfHealing(
            hasJackpot,
            isSelfHealComplete(player),
            canKeepRctChannelWithoutEffect(player)
        );
    }

    public static boolean canKeepRctChannelWithoutEffect(ServerPlayer player) {
        return player != null && RctChannelTransitionResolver.canKeepRctChannelWithoutEffect(
            isSelfHealComplete(player),
            shouldKeepOutputChannel(player),
            shouldKeepBrainRegeneration(player),
            passesActiveTickCondition(player)
        );
    }

    public static boolean shouldKeepOutputChannel(ServerPlayer player) {
        return player != null && canApplyOutput(player);
    }

    public static boolean canApplyOutput(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        boolean runtimeChannelActive =
            RctRuntimeStateAccess.isManualPressActive(player) || RctStateService.isAutoRctRunning(player);
        return runtimeChannelActive && RctTechniqueRestrictionRules.canApplyOutput(
            RctMath.isCursedSpirit(player),
            RctAdvancementHelper.hasAdvancementOrSukunaEffect(player, RctAdvancementHelper.MASTERY_RCT_OUTPUT_ID),
            RctStateService.isOutputEnabled(player),
            runtimeChannelActive
        );
    }

    public static boolean canUseOutput(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        boolean runtimeChannelActive =
            RctRuntimeStateAccess.isManualPressActive(player) || RctStateService.isAutoRctRunning(player);
        return RctTechniqueRestrictionRules.canUseOutput(
            RctMath.isCursedSpirit(player),
            RctAdvancementHelper.hasAdvancementOrSukunaEffect(player, RctAdvancementHelper.MASTERY_RCT_OUTPUT_ID),
            RctStateService.isOutputEnabled(player),
            runtimeChannelActive
        );
    }

    public static boolean canUseBrainRegeneration(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        return RctTechniqueRestrictionRules.canUseBrainRegeneration(
            RctMath.isCursedSpirit(player),
            player.hasEffect((MobEffect) JujutsucraftModMobEffects.BRAIN_DAMAGE.get()),
            RctAdvancementHelper.hasAdvancementOrSukunaEffect(player, RctAdvancementHelper.MASTERY_RCT_BRAIN_REGENERATION_ID),
            RctStateService.isBrainRegenerationEnabled(player)
        );
    }

    public static boolean canReceiveRctOutput(LivingEntity target) {
        return target != null && (RctMath.isCursedSpirit(target) || !RctHealGate.isRctFullyHealed(target));
    }

}
