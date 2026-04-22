package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.targeting.AttackTargetSelectionRestrictionService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class PrayerSongOnEffectActiveTickProcedureHook {
    private PrayerSongOnEffectActiveTickProcedureHook() {
    }

    public static boolean allowWeakness(boolean original, LevelAccessor world, Entity source, Entity target) {
        return original && AttackTargetSelectionRestrictionService.hasRegisteredAttackTarget(world, source, target);
    }
}