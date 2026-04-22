package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueService;
import com.arf8vhg7.jja.feature.jja.domain.sd.SimpleDomainExpireGuard;
import com.arf8vhg7.jja.feature.jja.domain.sd.SimpleDomainHoldService;
import net.minecraft.world.entity.Entity;

public final class SimpleDomainEffectExpiresProcedureHook {
    private SimpleDomainEffectExpiresProcedureHook() {
    }

    public static void onExpire(Entity entity) {
        if (!shouldHandleExpire(AntiDomainTechniqueService.hasOwnedSimpleDomain(entity), SimpleDomainExpireGuard.isGuarded(entity))) {
            return;
        }
        SimpleDomainHoldService.onExpire(entity);
    }

    static boolean shouldHandleExpire(boolean ownsSimpleDomain, boolean guardedDomainRewriteExpire) {
        return !ownsSimpleDomain && !guardedDomainRewriteExpire;
    }
}
