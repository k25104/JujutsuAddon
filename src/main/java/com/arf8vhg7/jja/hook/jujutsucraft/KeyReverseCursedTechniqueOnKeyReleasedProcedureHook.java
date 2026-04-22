package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public final class KeyReverseCursedTechniqueOnKeyReleasedProcedureHook {
    private KeyReverseCursedTechniqueOnKeyReleasedProcedureHook() {
    }

    public static boolean shouldSuppressRctRemoval(LivingEntity livingEntity, MobEffect effect) {
        return effect == JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get()
            && ReviveFlowService.shouldSuppressSpecialBranchRctKeyRelease(livingEntity);
    }
}
