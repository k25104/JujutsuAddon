package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.zone.ZoneChargeScalingService;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;

public final class TechniqueBlueProcedureHook {
    private static final String BLUE_INVULNERABLE_COMMAND = "data merge entity @s {Invulnerable:1b}";

    private TechniqueBlueProcedureHook() {
    }

    public static MutableComponent buildChantMessage(int chantStep) {
        return Component.translatable("chant.jujutsucraft.blue" + chantStep);
    }

    public static boolean isChantStepReady(Entity entity, boolean original) {
        return ZoneChargeScalingService.isCnt5ChantStepReady(entity, original);
    }

    public static String getBlueInvulnerableCommand() {
        return BLUE_INVULNERABLE_COMMAND;
    }
}
