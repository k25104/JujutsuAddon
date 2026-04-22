package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.da.DomainAmplificationWitnessService;

public final class KenjakuRightClickedOnEntityProcedureHook {
    private KenjakuRightClickedOnEntityProcedureHook() {
    }

    public static boolean shouldSuppressLegacyDomainAmplificationGrant() {
        return DomainAmplificationWitnessService.shouldSuppressLegacyGrant();
    }
}
