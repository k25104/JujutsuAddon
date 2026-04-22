package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import net.minecraft.world.entity.LivingEntity;

public final class KeepRCTOnCTEffectStart {
    private KeepRCTOnCTEffectStart() {
    }

    public static boolean shouldKeepReverseCursedTechniqueOnStart(LivingEntity entity) {
        return RctStateService.canKeepRctOnCtStart(entity) || ReviveFlowService.shouldKeepCoreBranchRctOnStart(entity);
    }
}
