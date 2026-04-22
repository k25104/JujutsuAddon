package com.arf8vhg7.jja.feature.jja.rct;

import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidMutationService;
import com.arf8vhg7.jja.util.JjaCommandHelper;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class RctEffectSupport {
    private RctEffectSupport() {
    }

    public static int getRctEffectLevel(LivingEntity entity) {
        if (entity == null || !entity.hasEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get())) {
            return 0;
        }
        return entity.getEffect((MobEffect) JujutsucraftModMobEffects.REVERSE_CURSED_TECHNIQUE.get()).getAmplifier();
    }

    public static boolean applyJjcHeal(LivingEntity entity, double amount) {
        return applyJjcHealthDelta(entity, Math.abs(amount));
    }

    public static boolean applyJjcHealthDelta(LivingEntity entity, double amount) {
        if (entity == null || amount == 0.0D || entity.level().isClientSide()) {
            return false;
        }
        if (shouldTryDistributedHeal(amount) && FirstAidMutationService.applyDistributedHeal(entity, amount)) {
            return true;
        }
        boolean executed = JjaCommandHelper.executeAsEntity(entity, buildJjcHealCommand(amount, entity.getStringUUID()));
        if (executed) {
            FirstAidMutationService.syncDirectHealthToDamageModel(entity);
        }
        return executed;
    }

    static boolean shouldTryDistributedHeal(double amount) {
        return amount > 0.0D;
    }

    static String buildJjcHealCommand(double amount, String targetUuid) {
        return "jjc_heal " + amount + " " + targetUuid;
    }

    public static void reduceEffectDuration(LivingEntity entity, MobEffect effect, double amount) {
        if (entity == null || effect == null || !entity.hasEffect(effect) || entity.level().isClientSide()) {
            return;
        }
        MobEffectInstance current = entity.getEffect(effect);
        if (current == null) {
            return;
        }
        double oldLevel = current.getAmplifier();
        double oldTick = current.getDuration();
        if (oldTick > amount) {
            oldTick -= amount;
        } else {
            oldLevel--;
        }
        entity.removeEffect(effect);
        if (Math.ceil(oldTick) > 0.0 && oldLevel >= 0.0) {
            entity.addEffect(new MobEffectInstance(effect, (int) Math.ceil(oldTick), (int) Math.round(oldLevel), false, false));
        }
    }
}
