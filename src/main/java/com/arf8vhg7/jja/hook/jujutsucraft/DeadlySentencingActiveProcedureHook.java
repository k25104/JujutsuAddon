package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainExpansionHookSupport;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.effect.MobEffects;

public final class DeadlySentencingActiveProcedureHook {
    private static final double JUDGEMENT_START_TICK = 1000.0;
    private static final int ORIGINAL_CONFISCATION_DURATION = 900;
    private static final int EXTENDED_CONFISCATION_DURATION = 6000;
    private static final int CONFISCATION_COOLDOWN_AMPLIFIER = 0;

    private DeadlySentencingActiveProcedureHook() {
    }

    public static MobEffectInstance getEffect(LivingEntity livingEntity, MobEffect effect) {
        return DomainExpansionHookSupport.getCountDurationEffect(livingEntity, effect);
    }

    public static int resolveJudgementStartDuration(Entity entity, int originalDuration) {
        if (!(entity instanceof LivingEntity livingEntity) || !livingEntity.hasEffect(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get())) {
            return originalDuration;
        }

        return DomainExpansionHookSupport.getCounter(entity) >= JUDGEMENT_START_TICK ? 0 : Integer.MAX_VALUE;
    }

    public static double adjustDomainRange(LivingEntity livingEntity, double radius) {
        return DomainExpansionHookSupport.adjustOpenBarrierRange(livingEntity, radius, 2.0);
    }

    public static double resolveFeetAnchorDistanceSquared(Entity owner, Entity target, double originalDistance) {
        return DomainExpansionHookSupport.resolveFeetAnchorDistanceSquared(owner, target, originalDistance);
    }

    public static boolean addConfiscationPenaltyEffect(
        LivingEntity livingEntity,
        MobEffectInstance effectInstance,
        Operation<Boolean> original
    ) {
        MobEffectInstance adjusted = adjustConfiscationPenaltyEffect(effectInstance);
        boolean added = original.call(livingEntity, adjusted);
        if (shouldAddCooldownTime(effectInstance)) {
            livingEntity.addEffect(createCooldownTimePenalty());
        }
        return added;
    }

    public static int adjustConfiscationCooldown(Item item, int originalTicks) {
        if (item != JujutsucraftModItems.CONFISCATION.get()) {
            return originalTicks;
        }
        return resolveExtendedConfiscationDuration(originalTicks);
    }

    static MobEffectInstance adjustConfiscationPenaltyEffect(MobEffectInstance effectInstance) {
        return adjustConfiscationPenaltyEffect(effectInstance, effectInstance != null && isExtendedConfiscationPenaltyEffect(effectInstance.getEffect()));
    }

    static MobEffectInstance adjustConfiscationPenaltyEffect(MobEffectInstance effectInstance, boolean extendedPenaltyEffect) {
        if (effectInstance == null) {
            return null;
        }
        if (!extendedPenaltyEffect) {
            return effectInstance;
        }
        int duration = resolveExtendedConfiscationDuration(effectInstance.getDuration());
        if (duration == effectInstance.getDuration()) {
            return effectInstance;
        }
        return new MobEffectInstance(
            effectInstance.getEffect(),
            duration,
            effectInstance.getAmplifier(),
            effectInstance.isAmbient(),
            effectInstance.isVisible(),
            effectInstance.showIcon()
        );
    }

    static boolean shouldAddCooldownTime(MobEffectInstance effectInstance) {
        return effectInstance != null
            && shouldAddCooldownTime(effectInstance.getEffect() == JujutsucraftModMobEffects.UNSTABLE.get(), effectInstance.getDuration());
    }

    static boolean shouldAddCooldownTime(boolean unstablePenalty, int originalDuration) {
        return unstablePenalty && originalDuration == ORIGINAL_CONFISCATION_DURATION;
    }

    static MobEffectInstance createCooldownTimePenalty() {
        return new MobEffectInstance(
            JujutsucraftModMobEffects.COOLDOWN_TIME.get(),
            EXTENDED_CONFISCATION_DURATION,
            CONFISCATION_COOLDOWN_AMPLIFIER,
            false,
            false
        );
    }

    static int resolveExtendedConfiscationDuration(int originalDuration) {
        return originalDuration == ORIGINAL_CONFISCATION_DURATION ? EXTENDED_CONFISCATION_DURATION : originalDuration;
    }

    private static boolean isExtendedConfiscationPenaltyEffect(MobEffect effect) {
        return effect == JujutsucraftModMobEffects.UNSTABLE.get()
            || effect == JujutsucraftModMobEffects.COOLDOWN_TIME_COMBAT.get()
            || effect == MobEffects.WEAKNESS;
    }
}
