package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.targeting.JjaAttackTargetSelectionContextService;

public final class JjaAttackTargetSelectionContextHook {
    private JjaAttackTargetSelectionContextHook() {
    }

    public static void jjaEnterAttackTargetSelectionContext() {
        JjaAttackTargetSelectionContextService.jjaEnter();
    }

    public static void jjaExitAttackTargetSelectionContext() {
        JjaAttackTargetSelectionContextService.jjaExit();
    }
}