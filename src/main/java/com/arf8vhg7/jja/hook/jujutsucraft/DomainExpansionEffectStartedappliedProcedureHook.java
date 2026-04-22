package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import com.arf8vhg7.jja.feature.jja.domain.de.OpenBarrierMasteryReservationService;
import com.arf8vhg7.jja.feature.player.state.AddonStatCounter;
import com.arf8vhg7.jja.feature.player.state.AddonStatsAccess;
import net.minecraft.world.entity.Entity;

public final class DomainExpansionEffectStartedappliedProcedureHook {
    private DomainExpansionEffectStartedappliedProcedureHook() {
    }

    public static void resetCounter(Entity entity) {
        DomainExpansionHookSupport.resetCounter(entity);
        AddonStatsAccess.incrementCounter(entity, AddonStatCounter.DE_USED);
        OpenBarrierMasteryReservationService.maybeAwardFromDeUsed(entity);
    }
}
