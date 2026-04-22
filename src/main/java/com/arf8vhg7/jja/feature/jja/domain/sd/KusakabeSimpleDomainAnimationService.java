package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import net.mcreator.jujutsucraft.init.JujutsucraftModAttributes;
import net.mcreator.jujutsucraft.procedures.PlayAnimationProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class KusakabeSimpleDomainAnimationService {
    private static final String KEY_SLOWNESS_EXPIRE_TICK = "jjaKusakabeSdSlownessExpireTick";
    private static final long KUSAKABE_SIMPLE_DOMAIN_SKILL = 3105L;
    private static final double RESET_ANIMATION_1 = -49.0D;
    private static final double RESET_ANIMATION_2 = 0.0D;

    private KusakabeSimpleDomainAnimationService() {
    }

    public static boolean isTrackedSlowness(MobEffectInstance effectInstance) {
        return effectInstance != null
            && effectInstance.getEffect() == MobEffects.MOVEMENT_SLOWDOWN
            && effectInstance.getDuration() == 60
            && effectInstance.getAmplifier() == 9
            && !effectInstance.isAmbient()
            && !effectInstance.isVisible();
    }

    public static void onTrackedSlownessApplied(Entity entity, MobEffectInstance effectInstance) {
        if (entity == null) {
            return;
        }
        if (!isTrackedSlowness(effectInstance)) {
            return;
        }
        entity.getPersistentData().putLong(KEY_SLOWNESS_EXPIRE_TICK, entity.level().getGameTime() + effectInstance.getDuration());
    }

    public static void onTechniqueTickFinished(Entity entity) {
        if (entity == null || !entity.getPersistentData().contains(KEY_SLOWNESS_EXPIRE_TICK)) {
            return;
        }
        if (JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(entity) != KUSAKABE_SIMPLE_DOMAIN_SKILL) {
            resetAnimation(entity);
            return;
        }
        if (entity.level().getGameTime() >= entity.getPersistentData().getLong(KEY_SLOWNESS_EXPIRE_TICK)) {
            resetAnimation(entity);
        }
    }

    private static void resetAnimation(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.getAttribute(JujutsucraftModAttributes.ANIMATION_1.get()) != null) {
                livingEntity.getAttribute(JujutsucraftModAttributes.ANIMATION_1.get()).setBaseValue(RESET_ANIMATION_1);
            }
            if (livingEntity.getAttribute(JujutsucraftModAttributes.ANIMATION_2.get()) != null) {
                livingEntity.getAttribute(JujutsucraftModAttributes.ANIMATION_2.get()).setBaseValue(RESET_ANIMATION_2);
            }
        }
        PlayAnimationProcedure.execute(entity.level(), entity);
        clearTracking(entity);
    }

    private static void clearTracking(Entity entity) {
        entity.getPersistentData().remove(KEY_SLOWNESS_EXPIRE_TICK);
    }
}
