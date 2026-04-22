package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.fbe.FallingBlossomEmotionTickService;
import com.arf8vhg7.jja.feature.jja.resource.ce.CEParticleContextService;
import net.minecraft.world.entity.Entity;

public final class FallingBlossomEmotionOnEffectActiveTickProcedureHook {
    private FallingBlossomEmotionOnEffectActiveTickProcedureHook() {
    }

    public static boolean shouldSkipInfinityRemoval() {
        return true;
    }

    public static void onActiveTick(Entity entity) {
        FallingBlossomEmotionTickService.onActiveTick(entity);
    }

    public static void enterCeParticleContext(Entity entity) {
        CEParticleContextService.enter(entity);
    }

    public static void exitCeParticleContext() {
        CEParticleContextService.exit();
    }

    public static void queueCursePowerDrain(Entity entity) {
        FallingBlossomEmotionTickService.queueCursePowerDrain(entity);
    }

    public static boolean shouldSkipUpstreamBurstDrain(Entity entity) {
        return FallingBlossomEmotionTickService.shouldSkipUpstreamBurstDrain(entity);
    }

    static boolean shouldDrainCursePowerEachTick(Entity entity) {
        return FallingBlossomEmotionTickService.shouldDrainCursePowerEachTick(entity);
    }

    static boolean shouldDrainCursePowerEachTick(boolean player, boolean hasSixEyes) {
        return FallingBlossomEmotionTickService.shouldDrainCursePowerEachTick(player, hasSixEyes);
    }
}
