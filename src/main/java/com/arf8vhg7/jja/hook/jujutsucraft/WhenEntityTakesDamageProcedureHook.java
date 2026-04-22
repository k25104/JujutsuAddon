package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.feature.combat.targeting.AttackTargetActionRules;
import com.arf8vhg7.jja.feature.combat.targeting.AttackTargetSelectionRestrictionService;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import com.arf8vhg7.jja.feature.jja.domain.de.DomainInfinityBypassService;
import com.arf8vhg7.jja.feature.jja.domain.fbe.FallingBlossomEmotionDamageRules;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiShadowImmersionService;
import com.arf8vhg7.jja.feature.jja.technique.family.mahoraga.MahoragaAdaptation;
import com.arf8vhg7.jja.feature.jja.technique.family.mahito.MahitoCounterRetaliationRules;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidMutationService;
import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.BreakDomainProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public final class WhenEntityTakesDamageProcedureHook {
    private WhenEntityTakesDamageProcedureHook() {
    }

    public static boolean shouldIgnoreInfinityForDomainAttack(
        @Nullable LevelAccessor world,
        @Nullable Entity sourceEntity,
        @Nullable Entity target
    ) {
        return DomainInfinityBypassService.shouldBypassInfinity(world, sourceEntity, target);
    }

    public static boolean resolveInfinityProtection(boolean original, @Nullable Entity entity) {
        return original || MegumiShadowImmersionService.isShadowInvulnerable(entity);
    }

    public static boolean shouldSuppressCounterForMahitoAttack(
        @Nullable LevelAccessor world,
        @Nullable Entity defender,
        @Nullable Entity immediateSourceEntity,
        @Nullable Entity sourceEntity
    ) {
        return MahitoCounterRetaliationRules.shouldSuppressCounter(world, defender, immediateSourceEntity, sourceEntity);
    }

    static boolean shouldIgnoreInfinityForDomainAttack(boolean domainAttack) {
        return domainAttack;
    }

    static boolean shouldSuppressCounterForMahitoAttack(
        boolean mahitoIdleTransfigurationAttack,
        int defenderStrengthLevel,
        int attackerStrengthLevel
    ) {
        return MahitoCounterRetaliationRules.shouldSuppressCounter(
            mahitoIdleTransfigurationAttack,
            defenderStrengthLevel,
            attackerStrengthLevel
        );
    }

    public static MobEffectInstance resolveDomainBreakDuration(MobEffectInstance original, MobEffect effect, Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)
            || effect != Objects.requireNonNull(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get())) {
            return original;
        }

        return resolveDomainBreakDuration(
            original,
            DomainExpansionHookSupport.getThresholdDurationEffect(livingEntity, effect),
            true
        );
    }

    public static void breakMahoragaDomainOnRegisteredAttack(LevelAccessor world, Entity target, Entity immediateSourceEntity, Entity sourceEntity) {
        if (world == null || target == null) {
            return;
        }
        if (world instanceof Level level && level.isClientSide()) {
            return;
        }

        Entity attackSource = sourceEntity != null ? sourceEntity : immediateSourceEntity;
        if (attackSource == null) {
            return;
        }

        boolean registeredTarget = AttackTargetSelectionRestrictionService.hasRegisteredAttackTarget(world, attackSource, target);
        Entity mahoragaSource = JjaJujutsucraftDataAccess.jjaResolveRootLivingOwner(world, attackSource);
        if (mahoragaSource == null) {
            mahoragaSource = attackSource;
        }

        boolean targetHasActiveDomain = target instanceof LivingEntity livingTarget
            && livingTarget.hasEffect(Objects.requireNonNull(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get()));
        if (AttackTargetActionRules.shouldBreakMahoragaDomain(
            MahoragaAdaptation.isMahoragaUser(mahoragaSource),
            registeredTarget,
            targetHasActiveDomain
        )) {
            BreakDomainProcedure.execute(world, target);
        }
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

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return CuriosEquipmentReadService.resolveEquipmentRead(livingEntity, equipmentSlot, original);
    }

    public static boolean allowFbeOutsideDomain(boolean original, Entity defender) {
        return FallingBlossomEmotionDamageRules.allowOutsideDomain(original, defender);
    }

    public static boolean allowFbeWithoutNeutralization(boolean original, Entity defender) {
        return FallingBlossomEmotionDamageRules.allowWithoutNeutralization(original, defender);
    }

    public static MobEffectInstance resolveFbeNeutralizationGateEffect(MobEffectInstance original, MobEffect effect, Entity defender) {
        return FallingBlossomEmotionDamageRules.resolveNeutralizationGateEffect(original, effect, defender);
    }

    static boolean allowFbeOutsideDomain(boolean original, boolean hasFallingBlossomEmotion) {
        return original || hasFallingBlossomEmotion;
    }

    static boolean allowFbeWithoutNeutralization(boolean original, boolean hasFallingBlossomEmotion) {
        return original || hasFallingBlossomEmotion;
    }

    static MobEffectInstance resolveFbeNeutralizationGateEffect(
        MobEffectInstance original,
        MobEffect effect,
        boolean hasFallingBlossomEmotion
    ) {
        if (!hasFallingBlossomEmotion) {
            return original;
        }
        if (original != null && original.getAmplifier() > 0) {
            return original;
        }
        return new MobEffectInstance(Objects.requireNonNull(effect), 1, 1, false, false);
    }

    static MobEffectInstance resolveDomainBreakDuration(
        MobEffectInstance original,
        MobEffectInstance thresholdDuration,
        boolean shouldUseThresholdDuration
    ) {
        return shouldUseThresholdDuration ? thresholdDuration : original;
    }
}
