package com.arf8vhg7.jja.hook.jujutsucraft;

public final class SkillRantaEyeProcedureHook {
    private static final String RANTA_EYE_INVULNERABLE_COMMAND = "data merge entity @s {Invulnerable:1b}";

    private SkillRantaEyeProcedureHook() {
    }

    public static double getInfiniteActivationLimit() {
        return Double.MAX_VALUE;
    }

    public static String getRantaEyeInvulnerableCommand() {
        return RANTA_EYE_INVULNERABLE_COMMAND;
    }
}