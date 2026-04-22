package com.arf8vhg7.jja.feature.jja.resource.ce;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables.PlayerVariables;
import net.mcreator.jujutsucraft.init.JujutsucraftModGameRules;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;

public final class JjaCursePowerAccountingService {
    private JjaCursePowerAccountingService() {
    }

    public static double resolveEffectivePower(Entity entity, double fallback) {
        return resolveEffectivePower(JjaJujutsucraftCompat.jjaGetPlayerVariables(entity), fallback);
    }

    public static double resolveEffectivePower(@Nullable JujutsucraftModVariables.PlayerVariables playerVariables, double fallback) {
        if (playerVariables == null) {
            return fallback;
        }
        return resolveEffectivePower(playerVariables.PlayerCursePower, playerVariables.PlayerCursePowerChange);
    }

    public static boolean hasEffectivePower(Entity entity) {
        return resolveEffectivePower(entity, 0.0D) > 0.0D;
    }

    public static boolean hasFormerPowerAtLeast(@Nullable JujutsucraftModVariables.PlayerVariables playerVariables, double threshold) {
        return playerVariables != null && playerVariables.PlayerCursePowerFormer > threshold;
    }

    public static void refreshPlayerCursePowerFormer(Entity entity) {
        if (!(entity instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(serverPlayer);
        if (playerVariables == null) {
            return;
        }

        int jujutsuUpgradeDifficulty = serverPlayer.level().getGameRules().getInt(Objects.requireNonNull(JujutsucraftModGameRules.JUJUTSUUPGRADEDIFFICULTY));
        MobEffect sukunaEffect = Objects.requireNonNull(JujutsucraftModMobEffects.SUKUNA_EFFECT.get());
        MobEffectInstance sukunaEffectInstance = serverPlayer.getEffect(sukunaEffect);
        boolean hasSukunaEffect = sukunaEffectInstance != null;
        int sukunaEffectAmplifier = -1;
        if (sukunaEffectInstance != null) {
            sukunaEffectAmplifier = sukunaEffectInstance.getAmplifier();
        }
        playerVariables.PlayerCursePowerFormer = resolvePlayerCursePowerFormer(
            playerVariables.PlayerCursePowerFormer,
            playerVariables.PlayerCurseTechnique,
            playerVariables.PlayerTechniqueUsedNumber,
            jujutsuUpgradeDifficulty
        );
        playerVariables.PlayerCursePowerMAX = resolvePlayerCursePowerMax(
            playerVariables.PlayerCursePowerFormer,
            playerVariables.PlayerLevel,
            hasSukunaEffect,
            sukunaEffectAmplifier
        );
        playerVariables.syncPlayerVariables(serverPlayer);
    }

    static double resolvePlayerCursePowerFormer(
        double currentFormer,
        double playerCurseTechnique,
        double playerTechniqueUsedNumber,
        int jujutsuUpgradeDifficulty
    ) {
        return resolvePlayerCursePowerFormer(
            currentFormer,
            playerCurseTechnique,
            playerTechniqueUsedNumber,
            jujutsuUpgradeDifficulty,
            CeScalingConfig.isCePoolScalingEnabled()
        );
    }

    static double resolvePlayerCursePowerFormer(
        double currentFormer,
        double playerCurseTechnique,
        double playerTechniqueUsedNumber,
        int jujutsuUpgradeDifficulty,
        boolean cePoolScalingEnabled
    ) {
        if (CursePowerScalingRules.isMakiCurseTechnique(playerCurseTechnique)) {
            return currentFormer;
        }

        return CursePowerScalingRules.resolvePlayerCursePowerFormer(
            playerCurseTechnique,
            playerTechniqueUsedNumber,
            jujutsuUpgradeDifficulty,
            currentFormer,
            cePoolScalingEnabled
        );
    }

    static double resolvePlayerCursePowerMax(
        double playerCursePowerFormer,
        double playerLevel,
        boolean hasSukunaEffect,
        int sukunaEffectAmplifier
    ) {
        return CursePowerScalingRules.resolvePlayerCursePowerMax(
            playerCursePowerFormer,
            playerLevel,
            hasSukunaEffect,
            sukunaEffectAmplifier
        );
    }

    public static void queueSpentPower(@Nullable JujutsucraftModVariables.PlayerVariables playerVariables, double spentAmount) {
        if (playerVariables == null) {
            return;
        }
        playerVariables.PlayerCursePowerChange = applyQueuedSpend(playerVariables.PlayerCursePowerChange, spentAmount);
    }

    public static void queueSpentPowerFromResult(
        @Nullable JujutsucraftModVariables.PlayerVariables playerVariables,
        double powerBefore,
        double powerAfter
    ) {
        queueSpentPower(playerVariables, resolveSpentPower(powerBefore, powerAfter));
    }

    static double resolveSpentPower(double powerBefore, double powerAfter) {
        return Math.max(powerBefore - powerAfter, 0.0D);
    }

    static double resolveEffectivePower(double currentPower, double queuedChange) {
        return currentPower + queuedChange;
    }

    static double applyQueuedSpend(double currentChange, double spentAmount) {
        double normalizedSpentAmount = Math.max(spentAmount, 0.0D);
        return normalizedSpentAmount > 0.0D ? currentChange - normalizedSpentAmount : currentChange;
    }
}
