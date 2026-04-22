package com.arf8vhg7.jja.hook.jujutsucraft;

public final class AIHakariProcedureHook {
    private AIHakariProcedureHook() {
    }

    public static boolean ignoreJackpotDomainBlock(boolean original) {
        return false;
    }
}