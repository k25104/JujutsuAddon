package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class DomainExpansionRadiusRuntime {
    private static final ResourceLocation ANTI_OPEN_BARRIER_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "anti_open_barrier_type_domain"
    );
    private static final String KEY_BASE_RADIUS = "jja_de_base_radius";
    private static final String KEY_CURRENT_RADIUS = "jja_de_current_radius";
    private static final String KEY_OPEN_CLASH_SUPPRESSED_AT = "jja_open_barrier_clash_suppressed_at";
    private static final double RADIUS_STEP = 1.0D;
    private static final double MIN_ANTI_OPEN_RADIUS = 3.0D;
    private static final double MAX_RADIUS_FACTOR = 2.0D;

    private DomainExpansionRadiusRuntime() {
    }

    public static void clear(@Nullable Entity entity) {
        if (entity == null) {
            return;
        }

        entity.getPersistentData().remove(KEY_BASE_RADIUS);
        entity.getPersistentData().remove(KEY_CURRENT_RADIUS);
        entity.getPersistentData().remove(KEY_OPEN_CLASH_SUPPRESSED_AT);
    }

    public static double resolveBaseRadius(@Nullable Entity entity, double fallbackRadius) {
        if (entity == null || entity.getPersistentData().getDouble(KEY_BASE_RADIUS) <= 0.0D) {
            return sanitizeRadius(fallbackRadius);
        }

        return sanitizeRadius(entity.getPersistentData().getDouble(KEY_BASE_RADIUS));
    }

    public static double resolveCurrentRadius(@Nullable Entity entity, double fallbackRadius) {
        if (entity == null) {
            return sanitizeRadius(fallbackRadius);
        }

        double baseRadius = resolveBaseRadius(entity, fallbackRadius);
        double currentRadius = entity.getPersistentData().getDouble(KEY_CURRENT_RADIUS);
        if (currentRadius <= 0.0D) {
            return baseRadius;
        }

        return Mth.clamp(currentRadius, resolveMinimumRadius(entity, baseRadius), resolveMaximumRadius(baseRadius));
    }

    public static double resolveMovableRadius(@Nullable Entity entity, double fallbackRadius) {
        if (entity == null) {
            return fallbackRadius;
        }

        return DomainExpansionRuntimeMath.resolveEntityMovableRadius(resolveCurrentRadius(entity, fallbackRadius));
    }

    public static double resolveInitialCurrentRadius(@Nullable Entity entity, double fallbackRadius) {
        double baseRadius = sanitizeRadius(fallbackRadius);
        if (entity == null) {
            return baseRadius;
        }

        if (entity.getPersistentData().getDouble(KEY_BASE_RADIUS) <= 0.0D) {
            entity.getPersistentData().putDouble(KEY_BASE_RADIUS, baseRadius);
            entity.getPersistentData().putDouble(KEY_CURRENT_RADIUS, baseRadius);
        }

        return resolveCurrentRadius(entity, fallbackRadius);
    }

    public static double resolveActiveRange(@Nullable LivingEntity livingEntity, double fallbackRadius, double openMultiplierInCode) {
        if (livingEntity == null) {
            return fallbackRadius;
        }

        return resolveActiveRangeForState(
            isOpenBarrierActive(livingEntity),
            resolveCurrentRadius(livingEntity, fallbackRadius),
            fallbackRadius,
            openMultiplierInCode
        );
    }

    static double resolveBarrierEnvelopeRadius(double radius) {
        return radius > 0.0D ? radius + 0.5D : radius;
    }

    static double resolveActiveRangeForState(boolean openBarrierActive, double currentRadius, double fallbackRadius, double openMultiplierInCode) {
        if (openBarrierActive) {
            if (openMultiplierInCode <= 0.0D) {
                return fallbackRadius;
            }
            return fallbackRadius * (18.0D / openMultiplierInCode);
        }

        return resolveBarrierEnvelopeRadius(currentRadius);
    }

    public static boolean isAntiOpenBarrierEligible(@Nullable Entity entity) {
        if (entity == null) {
            return false;
        }

        if (entity instanceof ServerPlayer player) {
            return JjaAdvancementHelper.has(player, ANTI_OPEN_BARRIER_ADVANCEMENT_ID);
        }

        return entity.getPersistentData().getDouble("cnt_learn_domain") > 0.0D;
    }

    public static boolean expandActiveRadius(ServerPlayer player) {
        return adjustActiveRadius(player, RADIUS_STEP);
    }

    public static boolean shrinkActiveRadius(ServerPlayer player) {
        return adjustActiveRadius(player, -RADIUS_STEP);
    }

    public static boolean applyOpenBarrierClashDamage(@Nullable Entity openOwner, @Nullable Entity closedOwner) {
        if (!(openOwner != null && closedOwner instanceof LivingEntity closedLiving && openOwner.level() instanceof ServerLevel level)
            || openOwner.level() != closedOwner.level()) {
            return false;
        }

        if (!isOpenBarrierActive(openOwner) || !isClosedDomainActive(closedOwner) || !isAntiOpenBarrierEligible(closedOwner)) {
            return false;
        }

        double damage = DomainClashDamagePenalty.computeOpenBarrierClashPressureDamage(closedLiving.getMaxHealth());
        if (damage > 0.0D) {
            closedOwner.getPersistentData().putDouble(
                "totalDamage",
                Math.max(0.0D, closedOwner.getPersistentData().getDouble("totalDamage")) + damage
            );
        }

        openOwner.getPersistentData().putLong(KEY_OPEN_CLASH_SUPPRESSED_AT, level.getGameTime());
        openOwner.getPersistentData().putBoolean("Failed", false);
        openOwner.getPersistentData().putBoolean("Cover", false);
        openOwner.getPersistentData().putDouble("cnt_cover", 0.0D);
        return true;
    }

    public static boolean suppressOpenBarrierFailureAndApplyClashDamage(@Nullable Entity openOwner) {
        if (!(openOwner instanceof LivingEntity openLiving && openOwner.level() instanceof ServerLevel level) || !isOpenBarrierActive(openOwner)) {
            return false;
        }

        Vec3 center = resolveDomainCenter(openOwner);
        if (center == null) {
            return false;
        }

        double range = resolveActiveRange(openLiving, DomainExpansionConfiguredRadiusSync.getConfiguredRadius(), 2.0D);
        boolean applied = false;
        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(range), LivingEntity::isAlive)) {
            if (candidate == openOwner || !DomainExpansionContainmentHelper.isWithinOwnerRadius(openOwner, candidate, range)) {
                continue;
            }
            applied |= applyOpenBarrierClashDamage(openOwner, candidate);
        }
        return applied;
    }

    public static boolean shouldSuppressOpenBarrierRepaint(@Nullable Entity entity) {
        if (!(entity != null && entity.level() instanceof ServerLevel level) || !isOpenBarrierActive(entity)) {
            return false;
        }

        long suppressedAt = entity.getPersistentData().getLong(KEY_OPEN_CLASH_SUPPRESSED_AT);
        return suppressedAt > 0L && level.getGameTime() - suppressedAt <= 1L;
    }

    static boolean isOpenBarrierActive(@Nullable Entity entity) {
        return entity != null && getDomainEffectAmplifier(entity) > 0;
    }

    static boolean isClosedDomainActive(@Nullable Entity entity) {
        if (entity == null) {
            return false;
        }

        MobEffect domainExpansionEffect = Objects.requireNonNull(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get());
        return getDomainEffectAmplifier(entity) == 0 && entity instanceof LivingEntity livingEntity
            && livingEntity.hasEffect(domainExpansionEffect);
    }

    @Nullable
    static Vec3 resolveDomainCenter(@Nullable Entity entity) {
        return entity != null && hasDomainCenter(entity) ? getDomainCenter(entity) : null;
    }

    private static boolean hasDomainCenter(Entity entity) {
        return JjaJujutsucraftDataAccess.jjaHasDomainCenter(entity);
    }

    private static Vec3 getDomainCenter(Entity entity) {
        Vec3 center = JjaJujutsucraftDataAccess.jjaGetDomainCenter(entity);
        return center == null ? Vec3.ZERO : center;
    }

    private static int getDomainEffectAmplifier(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return -1;
        }

        MobEffect domainExpansionEffect = Objects.requireNonNull(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get());
        MobEffectInstance effect = livingEntity.getEffect(domainExpansionEffect);
        return effect == null ? -1 : effect.getAmplifier();
    }

    private static boolean adjustActiveRadius(ServerPlayer player, double delta) {
        if (player == null || !isClosedDomainActive(player)) {
            return false;
        }

        double configuredRadius = DomainExpansionConfiguredRadiusSync.getConfiguredRadius();
        double baseRadius = ensureBaseRadius(player, configuredRadius);
        double oldRadius = resolveCurrentRadius(player, configuredRadius);
        double nextRadius = Mth.clamp(oldRadius + delta, resolveMinimumRadius(player, baseRadius), resolveMaximumRadius(baseRadius));
        if (Double.compare(nextRadius, oldRadius) == 0) {
            return false;
        }

        Vec3 center = resolveDomainCenter(player);
        player.getPersistentData().putDouble(KEY_CURRENT_RADIUS, nextRadius);
        rebuildAdjustedBarrierBand(player, center, oldRadius, nextRadius);
        DomainExpansionImmersivePortalsService.onRadiusChanged(player, oldRadius, nextRadius);
        player.displayClientMessage(Component.translatable("message.jja.domain_radius", formatRadius(nextRadius)), true);
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
        return true;
    }

    private static double ensureBaseRadius(Entity entity, double fallbackRadius) {
        double baseRadius = entity.getPersistentData().getDouble(KEY_BASE_RADIUS);
        if (baseRadius <= 0.0D) {
            baseRadius = sanitizeRadius(fallbackRadius);
            entity.getPersistentData().putDouble(KEY_BASE_RADIUS, baseRadius);
            entity.getPersistentData().putDouble(KEY_CURRENT_RADIUS, baseRadius);
        }
        return baseRadius;
    }

    private static double resolveMinimumRadius(Entity entity, double baseRadius) {
        return isAntiOpenBarrierEligible(entity) ? Math.min(baseRadius, MIN_ANTI_OPEN_RADIUS) : baseRadius;
    }

    private static double resolveMaximumRadius(double baseRadius) {
        return Math.max(baseRadius, baseRadius * MAX_RADIUS_FACTOR);
    }

    private static double sanitizeRadius(double radius) {
        return Math.max(radius, 1.0D);
    }

    private static void rebuildAdjustedBarrierBand(ServerPlayer player, @Nullable Vec3 center, double oldRadius, double newRadius) {
        if (center == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        DomainBarrierCleanup.rebuildChangedBand(level, player, center, oldRadius, newRadius);
    }

    private static String formatRadius(double radius) {
        if (Math.rint(radius) == radius) {
            return Integer.toString((int) radius);
        }
        return String.format(java.util.Locale.ROOT, "%.1f", radius);
    }
}
