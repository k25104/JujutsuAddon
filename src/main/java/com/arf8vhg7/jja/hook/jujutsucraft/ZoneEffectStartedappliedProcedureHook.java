package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.zone.ZoneEffectOverrides;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.LazyOptional;

public final class ZoneEffectStartedappliedProcedureHook {
    private ZoneEffectStartedappliedProcedureHook() {
    }

    public static void applyCursePowerRecovery(
        LazyOptional<JujutsucraftModVariables.PlayerVariables> optional,
        NonNullConsumer<JujutsucraftModVariables.PlayerVariables> consumer
    ) {
        if (!ZoneEffectOverrides.shouldRecoverCursePower()) {
            return;
        }

        optional.ifPresent(consumer::accept);
    }
}
