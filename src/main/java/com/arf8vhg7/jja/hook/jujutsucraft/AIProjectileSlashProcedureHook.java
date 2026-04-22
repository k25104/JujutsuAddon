package com.arf8vhg7.jja.hook.jujutsucraft;

public final class AIProjectileSlashProcedureHook {
    private static final String KNOCKBACK_KEY = "knockback";

    private AIProjectileSlashProcedureHook() {
    }

    public static double resolvePersistentDouble(String key, double value) {
        return KNOCKBACK_KEY.equals(key) ? 0.0D : value;
    }
}
