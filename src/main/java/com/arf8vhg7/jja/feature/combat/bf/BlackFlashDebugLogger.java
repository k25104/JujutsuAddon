package com.arf8vhg7.jja.feature.combat.bf;

import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.HandItemState;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.PlayerHandStateRules;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyCombatPassContext.PassKind;
import com.mojang.logging.LogUtils;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import org.slf4j.Logger;

public final class BlackFlashDebugLogger {
    private static final Logger LOGGER = LogUtils.getLogger();

    private BlackFlashDebugLogger() {
    }

    public static void logDamage(Entity attacker, @Nullable Entity target, @Nullable PassKind passKind, double rawDamage, double dealtDamage) {
        if (!shouldLog(attacker)) {
            return;
        }

        LOGGER.info(
            "[JJA][BF] damage pass={} hand={} attackType={} attacker={} target={} raw={} dealt={}",
            resolvePassLabel(passKind),
            resolveHandLabel(attacker, passKind),
            resolveAttackTypeLabel(attacker, passKind),
            describeEntity(attacker),
            describeEntity(target),
            formatDamage(rawDamage),
            formatDamage(dealtDamage)
        );
    }

    public static void logBlackFlash(Entity attacker, @Nullable Entity target, @Nullable PassKind passKind, double dealtDamage) {
        if (!shouldLog(attacker)) {
            return;
        }

        LOGGER.info(
            "[JJA][BF] black flash pass={} hand={} attackType={} attacker={} target={} dealt={}",
            resolvePassLabel(passKind),
            resolveHandLabel(attacker, passKind),
            resolveAttackTypeLabel(attacker, passKind),
            describeEntity(attacker),
            describeEntity(target),
            formatDamage(dealtDamage)
        );
    }

    private static String resolvePassLabel(@Nullable PassKind passKind) {
        return passKind == null ? "unknown" : passKind.name().toLowerCase(Locale.ROOT);
    }

    private static boolean shouldLog(Entity attacker) {
        return JjaCommonConfig.ENABLE_DEBUG.get() && !attacker.level().isClientSide();
    }

    private static String resolveHandLabel(Entity attacker, @Nullable PassKind passKind) {
        if (passKind == PassKind.ECHO) {
            return "extra-arm";
        }

        if (attacker instanceof Player player) {
            return "main-hand(" + player.getMainArm().name().toLowerCase(Locale.ROOT) + ")";
        }

        return "main-hand";
    }

    private static String resolveAttackTypeLabel(Entity attacker, @Nullable PassKind passKind) {
        if (passKind == PassKind.ECHO) {
            return "barehand";
        }

        if (attacker instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity owner) {
            return resolveAttackTypeLabel(owner, passKind);
        }

        if (attacker instanceof LivingEntity livingAttacker) {
            HandItemState handItemState = PlayerHandStateRules.classifyHandItem(
                livingAttacker instanceof Player player ? player : null,
                livingAttacker.getMainHandItem()
            );
            if (!handItemState.isMeaningfulHeldItem()) {
                return "barehand";
            }
            return handItemState.isSlashWeapon() ? "slash" : "blunt";
        }

        return "unknown";
    }

    private static String describeEntity(@Nullable Entity entity) {
        if (entity == null) {
            return "none";
        }

        return entity.getDisplayName().getString() + "[" + entity.getUUID() + "]";
    }

    private static String formatDamage(double damage) {
        return String.format(Locale.ROOT, "%.2f", damage);
    }
}