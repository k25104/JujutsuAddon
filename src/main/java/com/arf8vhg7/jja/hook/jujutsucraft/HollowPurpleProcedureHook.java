package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import com.arf8vhg7.jja.feature.combat.zone.ZoneChargeScalingService;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class HollowPurpleProcedureHook {
    private HollowPurpleProcedureHook() {
    }

    public static MutableComponent buildChantMessage(int chantStep) {
        return Component.translatable("chant.jujutsucraft.purple" + chantStep);
    }

    public static boolean isChantStepReady(Entity entity, boolean original) {
        return ZoneChargeScalingService.isCnt5ChantStepReady(entity, original);
    }

    public static float getEffectiveHealth(Entity entity, float original) {
        return entity instanceof LivingEntity livingEntity ? FirstAidHealthAccess.getEffectiveHealth(livingEntity) : original;
    }
}
