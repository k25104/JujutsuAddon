package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.rct.KeepRCTOnCTEffectStart;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public final class CursedTechniqueEffectStartedappliedProcedureHook {
    private CursedTechniqueEffectStartedappliedProcedureHook() {
    }

    public static boolean handleReverseCursedTechniqueRemovalOnStart(
        LivingEntity livingEntity,
        MobEffect effect,
        Operation<Boolean> original
    ) {
        if (KeepRCTOnCTEffectStart.shouldKeepReverseCursedTechniqueOnStart(livingEntity)) {
            return false;
        }
        return original.call(livingEntity, effect);
    }
}
