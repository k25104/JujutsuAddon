package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.technique.shared.effect.InsectArmorEffectVisibility;
import com.mojang.logging.LogUtils;
import java.util.function.IntSupplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

public final class TechniqueInsectArmorProcedureHook {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String INSECT_ARMOR_EFFECT_COMMAND_PREFIX =
        "effect give @s jujutsucraft:insect_armor_technique infinite ";

    private TechniqueInsectArmorProcedureHook() {
    }

    public static int runEffectCommand(String command, Entity entity, IntSupplier fallback) {
        if (!(entity instanceof LivingEntity livingEntity)
            || livingEntity.level().isClientSide()
            || command == null
            || !command.startsWith(INSECT_ARMOR_EFFECT_COMMAND_PREFIX)) {
            return fallback.getAsInt();
        }
        Integer amplifier = parseAmplifier(command);
        if (amplifier == null) {
            return fallback.getAsInt();
        }
        InsectArmorEffectVisibility.applyHiddenEffect(livingEntity, amplifier);
        return 1;
    }

    private static Integer parseAmplifier(String command) {
        try {
            return Integer.parseInt(command.substring(INSECT_ARMOR_EFFECT_COMMAND_PREFIX.length()).trim());
        } catch (NumberFormatException exception) {
            LOGGER.warn("Failed to parse insect armor amplifier from command '{}'.", command, exception);
            return null;
        }
    }
}
