package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.feature.combat.bf.DisableBrainDamageShortenOnBF;
import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyRuntimeStateAccess;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyCombatPassContext;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidMutationService;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniqueHeldItemRules;
import com.arf8vhg7.jja.feature.player.state.AddonStatCounter;
import com.arf8vhg7.jja.feature.player.state.AddonStatsAccess;
import com.arf8vhg7.jja.feature.combat.zone.ZoneEffectOverrides;
import java.util.Set;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;

public final class RangeAttackProcedureHook {
    private RangeAttackProcedureHook() {
    }

    public static boolean removeEffect(LivingEntity livingEntity, MobEffect effect) {
        return DisableBrainDamageShortenOnBF.removeEffect(livingEntity, effect);
    }

    public static boolean addEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return DisableBrainDamageShortenOnBF.addEffect(livingEntity, effectInstance);
    }

    public static double adjustBlackFlashChance(Entity entity, double num1) {
        double adjustedNum1 = ZoneEffectOverrides.stripBlackFlashZoneBonus(entity, num1);
        return switch (TechniqueHeldItemRules.resolveBlackFlashPenalty(entity, TwinnedBodyCombatPassContext.isExtraArmAttack())) {
            case NONE -> adjustedNum1;
            case BLUNT -> adjustedNum1 * 0.5D;
            case SLASH -> 0.0D;
        };
    }

    public static void incrementBfRanded(Entity entity) {
        AddonStatsAccess.incrementCounter(entity, AddonStatCounter.BF_RANDED);
    }

    public static void refreshPlayerCursePowerFormer(Entity entity) {
        JjaCursePowerAccountingService.refreshPlayerCursePowerFormer(entity);
    }

    public static CompoundTag captureTrackedHurtSnapshot(Entity entity) {
        return FirstAidMutationService.capturePartHealthSnapshot(entity);
    }

    public static float getEffectiveHealth(Entity entity, float original) {
        return entity instanceof LivingEntity livingEntity ? FirstAidHealthAccess.getEffectiveHealth(livingEntity) : original;
    }

    public static boolean resolveTrackedHurtDamageApplied(
        Entity entity,
        DamageSource damageSource,
        float amount,
        CompoundTag snapshot,
        boolean recordMahoragaPendingDamage
    ) {
        return FirstAidMutationService.handlePostHurt(entity, damageSource, amount, snapshot, recordMahoragaPendingDamage);
    }

    public static float forceChangedHealthForComparison(float currentHealth, double oldHealth, boolean firstAidDamageApplied) {
        return FirstAidMutationService.forceChangedHealthForComparison(currentHealth, oldHealth, firstAidDamageApplied);
    }

    public static double resolveCurrentRadius(Entity entity, double radius) {
        return DomainExpansionHookSupport.resolveMovableRadius(entity, radius);
    }

    public static boolean shouldReplayCombatEcho(Entity entity) {
        if (!(entity instanceof Player player) || !player.getPersistentData().getBoolean("attack")) {
            return false;
        }

        if (!TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(player) || JjaJujutsucraftDataAccess.jjaGetCurrentSkillValue(player) < 100.0D) {
            return false;
        }

        return !TwinnedBodyRuntimeStateAccess.isCombatEchoConsumed(player, player.level().getGameTime());
    }

    public static void markCombatEchoConsumed(Entity entity) {
        if (entity instanceof Player player) {
            TwinnedBodyRuntimeStateAccess.markCombatEchoConsumed(player, player.level().getGameTime());
        }
    }

    public static boolean clearCombatEchoConsumption(Entity entity) {
        return entity instanceof Player player && TwinnedBodyRuntimeStateAccess.clearCombatEchoConsumption(player);
    }

    public static CompoundTag captureCombatRuntimeSnapshot(Entity entity) {
        CompoundTag snapshot = entity.getPersistentData().copy();
        if (entity instanceof Player player && TwinnedBodyRuntimeStateAccess.isTwinnedBodyMarked(player)) {
            double heldItemAttackDamageBonus = DamageFixProcedureHook.resolveMainHandAttackDamageBonus(player.getMainHandItem());
            double heldItemDamageMultiplier = DamageFixProcedureHook.resolveHeldItemDamageMultiplier(
                player.getMainHandItem(),
                snapshot.getDouble("cnt6")
            );
            double currentAttackDamage = player.getAttributeValue(Objects.requireNonNull(Attributes.ATTACK_DAMAGE));
            snapshot.putDouble(
                "Damage",
                DamageFixProcedureHook.resolveTwinnedBodyEchoSnapshotDamage(
                    snapshot.getDouble("Damage"),
                    currentAttackDamage,
                    heldItemAttackDamageBonus,
                    heldItemDamageMultiplier,
                    resolveStrengthEffectBonus(player),
                    resolveWeaknessEffectPenalty(player)
                )
            );
        }

        return snapshot;
    }

    private static double resolveStrengthEffectBonus(LivingEntity livingEntity) {
        MobEffectInstance strengthEffect = livingEntity.getEffect(Objects.requireNonNull(MobEffects.DAMAGE_BOOST));
        return strengthEffect == null ? 0.0D : 1.0D + strengthEffect.getAmplifier();
    }

    private static double resolveWeaknessEffectPenalty(LivingEntity livingEntity) {
        MobEffectInstance weaknessEffect = livingEntity.getEffect(Objects.requireNonNull(MobEffects.WEAKNESS));
        return weaknessEffect == null ? 0.0D : 1.0D + weaknessEffect.getAmplifier();
    }

    public static void restoreCombatRuntimeSnapshot(Entity entity, CompoundTag snapshot) {
        CompoundTag persistentData = entity.getPersistentData();
        for (String key : Set.copyOf(persistentData.getAllKeys())) {
            persistentData.remove(Objects.requireNonNull(key));
        }
        persistentData.merge(Objects.requireNonNull(snapshot.copy()));
    }
}
