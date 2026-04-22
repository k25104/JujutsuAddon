package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.activation.CutTheWorldWitnessService;
import net.minecraft.world.entity.Entity;

public final class MahoragaCutTheWorldProcedureHook {
    private MahoragaCutTheWorldProcedureHook() {
    }

    public static void observeCutTheWorld(Entity entity) {
        if (entity != null && shouldObserveCutTheWorld(entity.getPersistentData().getDouble("cnt1"))) {
            CutTheWorldWitnessService.witness(entity);
        }
    }

    static boolean shouldObserveCutTheWorld(double cnt1) {
        return Double.compare(cnt1, 4.0D) == 0;
    }
}
