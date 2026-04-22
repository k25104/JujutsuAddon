package com.arf8vhg7.jja.hook.jujutsucraft;

import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Objects;

public final class TechniqueTrueSphereProcedureHook {
    private static final String TRUE_SPHERE_INVULNERABLE_COMMAND = "data merge entity @s {Invulnerable:1b}";

    private TechniqueTrueSphereProcedureHook() {
    }

    public static boolean canUseTrueSphere(Entity entity) {
        return entity instanceof LivingEntity livingEntity
            && livingEntity.hasEffect(Objects.requireNonNull(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get()));
    }

    public static String getTrueSphereInvulnerableCommand() {
        return TRUE_SPHERE_INVULNERABLE_COMMAND;
    }
}