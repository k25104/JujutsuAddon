package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import net.mcreator.jujutsucraft.init.JujutsucraftModEntities;
import net.mcreator.jujutsucraft.procedures.AIRideableControlProcedure;
import net.mcreator.jujutsucraft.procedures.FollowEntityProcedure;
import net.mcreator.jujutsucraft.procedures.ResetCounterProcedure;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.LevelAccessor;

public final class NueMountedControlService {
    private NueMountedControlService() {
    }

    public static boolean executeMountedRideControl(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!shouldUseMountedControl(entity)) {
            return false;
        }

        FollowEntityProcedure.execute(world, entity);
        if (entity == null || entity.isRemoved() || !entity.isAlive()) {
            return true;
        }

        applyPassiveUpkeep(entity);
        suppressAutonomousCombat(entity);
        AIRideableControlProcedure.execute(entity);
        return true;
    }

    public static boolean shouldUseDefaultTravelWhileMounted(Entity entity) {
        return shouldUseMountedControl(entity);
    }

    public static void clearRuntimeState(Entity entity) {
        // Mounted Nue control no longer keeps runtime state.
    }

    private static boolean shouldUseMountedControl(Entity entity) {
        return isNue(entity) && isTamedNue(entity) && hasMountedRider(entity);
    }

    private static void applyPassiveUpkeep(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living.level().isClientSide() || !living.isAlive()) {
            return;
        }

        int damageBoostAmplifier = (int) Math.round((entity.getPersistentData().getBoolean("Giant") ? 9.0D : 5.0D) + entity.getPersistentData().getDouble("Strength") * 0.5D);
        if (!living.hasEffect(MobEffects.DAMAGE_BOOST)) {
            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, damageBoostAmplifier, false, false));
        }

        double attackDamage = living.getAttribute(Attributes.ATTACK_DAMAGE) != null ? living.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0.0D;
        int resistanceAmplifier = (int) Math.round(Math.floor(Math.min((damageBoostAmplifier + attackDamage * 3.0D) / 4.0D, 3.0D)));
        MobEffectInstance existingResistance = living.getEffect(MobEffects.DAMAGE_RESISTANCE);
        if (existingResistance == null || existingResistance.getAmplifier() < resistanceAmplifier) {
            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, resistanceAmplifier, false, false));
        }
    }

    private static void suppressAutonomousCombat(Entity entity) {
        if (entity instanceof Mob mob) {
            mob.setTarget(null);
            mob.getNavigation().stop();
        }
        entity.setSprinting(false);
        entity.getPersistentData().putDouble("cnt_x", 0.0D);
        entity.getPersistentData().putDouble("cnt_x2", 0.0D);
        if (JjaJujutsucraftDataAccess.jjaGetCurrentSkillValue(entity) != 0.0D || entity.getPersistentData().getBoolean("attack")) {
            ResetCounterProcedure.execute(entity);
            JjaJujutsucraftDataAccess.jjaSetCurrentSkillValue(entity, 0.0D);
        }
    }

    private static boolean hasMountedRider(Entity entity) {
        return entity != null && entity.isVehicle() && entity.getFirstPassenger() instanceof LivingEntity;
    }

    private static boolean isTamedNue(Entity entity) {
        return JjaJujutsucraftDataAccess.jjaGetFriendNum(entity) != 0.0D;
    }

    private static boolean isNue(Entity entity) {
        return entity != null && entity.getType() == JujutsucraftModEntities.NUE.get();
    }
}
