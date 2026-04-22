package com.arf8vhg7.jja.feature.jja.technique.shared.summon;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public final class SummonDeathCooldownPolicy {
    private static final String MOON_DREGS_CLASS_NAME = "net.mcreator.jujutsucraft.entity.MoonDregsEntity";
    private static final String HETEROCEPHALUS_GLABER_CLASS_NAME = "net.mcreator.jujutsucraft.entity.ShikigamiHeterocephalusGlaberEntity";
    private static final String PTEROSAUR_CLASS_NAME = "net.mcreator.jujutsucraft.entity.ShikigamiPterosaurEntity";

    private SummonDeathCooldownPolicy() {
    }

    public static boolean shouldApplyDeathCooldown(@Nullable Entity defeatedSummon) {
        return defeatedSummon == null || !isSuppressedDeathCooldownClass(defeatedSummon.getClass());
    }

    public static boolean shouldApplyDeathCooldownForClassName(String className) {
        return !isSuppressedDeathCooldownClassName(className);
    }

    private static boolean isSuppressedDeathCooldownClass(Class<?> entityClass) {
        Class<?> current = entityClass;
        while (current != null) {
            if (isSuppressedDeathCooldownClassName(current.getName())) {
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }

    private static boolean isSuppressedDeathCooldownClassName(String className) {
        return MOON_DREGS_CLASS_NAME.equals(className)
            || HETEROCEPHALUS_GLABER_CLASS_NAME.equals(className)
            || PTEROSAUR_CLASS_NAME.equals(className);
    }
}
