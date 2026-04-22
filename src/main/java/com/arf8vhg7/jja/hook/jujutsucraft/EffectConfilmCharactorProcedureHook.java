package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainInfinityBypassService;
import com.arf8vhg7.jja.feature.jja.technique.shared.effect.InfinityEffectVisibility;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidMutationService;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class EffectConfilmCharactorProcedureHook {
    private EffectConfilmCharactorProcedureHook() {
    }

    public static boolean shouldProcessInfinityBlock(
        @Nullable LevelAccessor world,
        @Nullable Entity entity,
        @Nullable Entity entityiterator,
        boolean targetHasInfinity
    ) {
        return targetHasInfinity && !DomainInfinityBypassService.shouldBypassInfinity(world, entity, entityiterator);
    }

    public static boolean addInfinityEffect(LivingEntity livingEntity, MobEffectInstance effectInstance) {
        return livingEntity.addEffect(InfinityEffectVisibility.normalize(effectInstance));
    }

    public static ItemStack resolveEquipmentRead(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack original) {
        return original;
    }

    public static CompoundTag captureTrackedHurtSnapshot(Entity entity) {
        return FirstAidMutationService.capturePartHealthSnapshot(entity);
    }

    public static float getEffectiveHealth(Entity entity, float original) {
        return entity instanceof LivingEntity livingEntity ? FirstAidHealthAccess.getEffectiveHealth(livingEntity) : original;
    }

    public static boolean resolveTrackedHurtDamageApplied(Entity entity, DamageSource damageSource, float amount, CompoundTag snapshot) {
        return FirstAidMutationService.handlePostHurt(entity, damageSource, amount, snapshot, false);
    }

    public static float forceChangedHealthForComparison(float currentHealth, double oldHealth, boolean firstAidDamageApplied) {
        return FirstAidMutationService.forceChangedHealthForComparison(currentHealth, oldHealth, firstAidDamageApplied);
    }
}
