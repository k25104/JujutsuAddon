package com.arf8vhg7.jja.feature.player.health.firstaid;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.compat.firstaid.FirstAidDamageModelCompat;
import com.arf8vhg7.jja.feature.jja.technique.family.mahoraga.MahoragaAdaptation;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.OgiZeninPassiveSkillProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;

public final class FirstAidMutationService {
    private FirstAidMutationService() {
    }

    @Nullable
    public static CompoundTag capturePartHealthSnapshot(Entity entity) {
        Player player = FirstAidPlayerResolver.resolve(entity);
        return player == null ? null : FirstAidDamageModelCompat.snapshotPartHealth(player);
    }

    public static boolean handlePostHurt(
        Entity entity,
        DamageSource damageSource,
        float amount,
        @Nullable CompoundTag snapshot,
        boolean recordMahoragaPendingDamage
    ) {
        Player player = FirstAidPlayerResolver.resolve(entity);
        if (player == null || snapshot == null || !FirstAidDamageModelCompat.hasDamageApplied(player, snapshot)) {
            return false;
        }
        retargetDamagedMob(player, damageSource == null ? null : damageSource.getEntity());
        FirstAidHealthSyncService.finalizeMutation(player, true, FirstAidHealthSyncService.DamageModelSyncMode.NONE);
        if (recordMahoragaPendingDamage && entity instanceof LivingEntity livingEntity && damageSource != null && amount > 0.0F) {
            MahoragaAdaptation.recordPendingDamage(livingEntity, damageSource, amount);
        }
        return true;
    }

    public static float forceChangedHealthForComparison(float currentHealth, double oldHealth, boolean firstAidDamageApplied) {
        if (!firstAidDamageApplied || Math.abs(currentHealth - oldHealth) > 1.0E-4D) {
            return currentHealth;
        }
        return (float) Math.max(0.0D, oldHealth - 0.5D);
    }

    public static boolean applyDistributedHeal(LivingEntity entity, double amount) {
        Player player = FirstAidPlayerResolver.resolve(entity);
        if (player == null || amount <= 0.0) {
            return false;
        }
        if (!FirstAidDamageModelCompat.distributeHeal(player, (float) amount)) {
            return false;
        }
        FirstAidHealthSyncService.finalizeMutation(player, true, FirstAidHealthSyncService.DamageModelSyncMode.IMMEDIATE_ABSOLUTE);
        OgiZeninPassiveSkillProcedure.execute(player);
        return true;
    }

    public static boolean syncDirectHealthToDamageModel(LivingEntity entity) {
        Player player = FirstAidPlayerResolver.resolve(entity);
        if (player == null) {
            return false;
        }
        float currentHealth = Math.max(0.0F, Math.min(player.getHealth(), player.getMaxHealth()));
        FirstAidDamageModelCompat.setUniformHealthRatio(player, FirstAidHealthSyncService.resolveCurrentHealthRatio(currentHealth, player.getMaxHealth()));
        FirstAidDamageModelCompat.setTrackedHealthDirect(player, currentHealth);
        return true;
    }

    public static boolean applyDistributedMaxFractionHeal(LivingEntity entity, double maxHealthFraction) {
        return entity != null && maxHealthFraction > 0.0 && applyDistributedHeal(entity, entity.getMaxHealth() * maxHealthFraction);
    }

    public static boolean applyPlayerPhysicalAbilityHealthBoostRestore(LivingEntity entity) {
        Player player = FirstAidPlayerResolver.resolve(entity);
        if (player == null) {
            return false;
        }
        CompoundTag snapshot = FirstAidDamageModelCompat.snapshotMissingHealth(player);
        if (snapshot == null) {
            return false;
        }
        FirstAidHealthSyncService.queuePendingHealthBoostRestore(player, snapshot);
        return true;
    }

    private static void retargetDamagedMob(Entity damagedEntity, @Nullable Entity sourceEntity) {
        if (damagedEntity == null || sourceEntity == null || damagedEntity == sourceEntity || !damagedEntity.isAlive() || !sourceEntity.isAlive()) {
            return;
        }

        Entity ownerEntity = JjaJujutsucraftDataAccess.jjaResolveDirectOwner(damagedEntity.level(), sourceEntity);
        if (ownerEntity instanceof LivingEntity) {
            sourceEntity = ownerEntity;
        }

        if (damagedEntity instanceof Mob mob && sourceEntity instanceof LivingEntity livingSource) {
            mob.setTarget(livingSource);
            JjaJujutsucraftDataAccess.jjaClearTargetUuid(mob);
        }
        if (damagedEntity instanceof PathfinderMob pathfinderMob && sourceEntity instanceof LivingEntity) {
            pathfinderMob.targetSelector.addGoal(1, new HurtByTargetGoal(pathfinderMob));
        }
    }
}
