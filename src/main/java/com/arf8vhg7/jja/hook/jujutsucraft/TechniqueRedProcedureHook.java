package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.zone.ZoneChargeScalingService;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;

public final class TechniqueRedProcedureHook {
    private TechniqueRedProcedureHook() {
    }

    public static MutableComponent buildChantMessage(int chantStep) {
        return Component.translatable("chant.jujutsucraft.red" + chantStep);
    }

    public static boolean isChargeWindowReady(Entity entity, boolean original) {
        return ZoneChargeScalingService.isCnt1ChargeWindowReady(entity, original);
    }

    public static double getChargeWindowClamp(Entity entity, double original) {
        return ZoneChargeScalingService.scaleThreshold(entity, original);
    }

    public static boolean isChantStepReady(Entity entity, boolean original) {
        return ZoneChargeScalingService.isCnt5ChantStepReady(entity, original);
    }

    public static boolean isChargeWindowExpired(Entity entity, boolean original) {
        return ZoneChargeScalingService.isCnt1ChargeWindowExpired(entity, original);
    }
}
