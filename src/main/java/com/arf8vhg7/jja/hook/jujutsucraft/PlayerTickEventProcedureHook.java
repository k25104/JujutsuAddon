package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.resource.ce.PlayerTickCursePowerNormalizationService;
import com.arf8vhg7.jja.feature.player.mobility.CTStepHeight;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import com.arf8vhg7.jja.feature.jja.domain.sd.SimpleDomainHoldService;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class PlayerTickEventProcedureHook {
    private PlayerTickEventProcedureHook() {
    }

    public static int modifyTickInterval(int original) {
        return 1;
    }

    public static double normalizeCursePowerChange(
        Entity entity,
        JujutsucraftModVariables.PlayerVariables playerVars,
        double currentCursePowerChangeAfterHealRound,
        double healCursePower
    ) {
        return PlayerTickCursePowerNormalizationService.normalize(
            entity,
            playerVars,
            currentCursePowerChangeAfterHealRound,
            healCursePower
        );
    }

    public static double normalizeAppliedCursePowerChange(
        Entity entity,
        JujutsucraftModVariables.PlayerVariables playerVars,
        double powerAfterQueuedChange,
        double healCursePower
    ) {
        if (playerVars == null) {
            return powerAfterQueuedChange;
        }
        double normalizedChange = normalizeCursePowerChange(
            entity,
            playerVars,
            powerAfterQueuedChange - playerVars.PlayerCursePower,
            healCursePower
        );
        return playerVars.PlayerCursePower + normalizedChange;
    }

    public static float getEffectiveHealth(Entity entity, float original) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return original;
        }
        if (FirstAidHealthAccess.isEffectivelyAtFullHealth(livingEntity)) {
            return livingEntity.getMaxHealth();
        }
        return FirstAidHealthAccess.getEffectiveHealth(livingEntity);
    }

    public static void applyCTStepHeight(Entity entity) {
        CTStepHeight.apply(entity);
    }

    public static void tickSimpleDomainAnimationStop(Entity entity) {
        SimpleDomainHoldService.tickPendingAnimationStop(entity);
    }
}
