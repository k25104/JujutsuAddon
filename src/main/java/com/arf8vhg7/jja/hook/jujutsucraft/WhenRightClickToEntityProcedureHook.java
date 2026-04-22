package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.family.naoya.NaoyaProjectionSorceryService;
import com.arf8vhg7.jja.feature.player.revive.ReviveRightClickGuardService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class WhenRightClickToEntityProcedureHook {
    private WhenRightClickToEntityProcedureHook() {
    }

    public static boolean shouldCancel(Entity source, @Nullable Entity target) {
        return ReviveRightClickGuardService.shouldBlockJujutsucraftEntityRightClick(target);
    }

    public static boolean applyProjectionSorceryEffect(
        LevelAccessor world,
        Entity source,
        LivingEntity target,
        MobEffectInstance effectInstance,
        Operation<Boolean> original
    ) {
        boolean applied = original.call(target, effectInstance);
        NaoyaProjectionSorceryService.handleProjectionFreezeEffectApplied(world, source, target, effectInstance, applied);
        return applied;
    }
}
