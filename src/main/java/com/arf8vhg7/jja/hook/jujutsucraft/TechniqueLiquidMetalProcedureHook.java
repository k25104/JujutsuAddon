package com.arf8vhg7.jja.hook.jujutsucraft;

public final class TechniqueLiquidMetalProcedureHook {
    private static final String LIQUID_METAL_INVULNERABLE_COMMAND = "data merge entity @s {Invulnerable:1b}";

    private TechniqueLiquidMetalProcedureHook() {
    }

    public static String getLiquidMetalInvulnerableCommand() {
        return LIQUID_METAL_INVULNERABLE_COMMAND;
    }
}