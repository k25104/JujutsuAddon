package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.jja.rct.RctChannelTransitionResolver;
import com.arf8vhg7.jja.feature.jja.rct.RctContextService;
import com.arf8vhg7.jja.feature.jja.rct.RctHealGate;
import com.arf8vhg7.jja.feature.jja.rct.RctMath;
import com.arf8vhg7.jja.feature.jja.rct.RctRuntimeStateAccess;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class KeyReverseCursedTechniqueOnKeyPressedProcedureHook {
    private KeyReverseCursedTechniqueOnKeyPressedProcedureHook() {
    }

    public static boolean shouldCancelStart(Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        if (shouldCancelManualStart(
            ReviveFlowService.isWaiting(player),
            RctMath.isCursedSpirit(player),
            player.hasEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get()),
            resolveSelfHealComplete(player),
            resolveCanUseOutputAtFullHeal(player)
        )) {
            RctRuntimeStateAccess.setManualPressActive(player, false);
            return true;
        }
        return false;
    }

    static boolean shouldCancelManualStart(
        boolean reviveWaiting,
        boolean cursedSpirit,
        boolean hasRctEffect,
        boolean selfHealComplete,
        boolean canUseOutputAtFullHeal
    ) {
        return RctChannelTransitionResolver.shouldCancelManualStart(
            reviveWaiting,
            cursedSpirit,
            hasRctEffect,
            selfHealComplete,
            canUseOutputAtFullHeal
        );
    }

    private static boolean resolveSelfHealComplete(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return RctContextService.isSelfHealComplete(serverPlayer);
        }
        return RctHealGate.isRctFullyHealed(player);
    }

    private static boolean resolveCanUseOutputAtFullHeal(Player player) {
        return !JjaCommonConfig.RCT_OUTPUT_ENABLED.get()
            || player instanceof ServerPlayer serverPlayer && RctContextService.canUseOutput(serverPlayer);
    }
}
