package com.arf8vhg7.jja.compat.jujutsucraft;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.GetEntityFromUUIDProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public final class JjaJujutsucraftDataAccess {
    private static final int MAX_OWNER_DEPTH = 5;
    private static final String SKILL_KEY = "skill";
    private static final String OWNER_UUID_KEY = "OWNER_UUID";
    private static final String TARGET_UUID_KEY = "TARGET_UUID";
    private static final String CNT_TARGET_KEY = "cnt_target";
    private static final String FRIEND_NUM_KEY = "friend_num";
    private static final String FRIEND_NUM_WORKER_KEY = "friend_num_worker";
    private static final String DOMAIN_FLAG_KEY = "flag_domain";
    private static final String DOMAIN_LEARN_COUNT_KEY = "cnt_learn_domain";
    private static final String DOMAIN_X_KEY = "x_pos_doma";
    private static final String DOMAIN_Y_KEY = "y_pos_doma";
    private static final String DOMAIN_Z_KEY = "z_pos_doma";
    private static final String DOMAIN_PATTERN_X_KEY = "x_pos_doma2";
    private static final String DOMAIN_PATTERN_Y_KEY = "y_pos_doma2";
    private static final String DOMAIN_PATTERN_Z_KEY = "z_pos_doma2";
    private static final String JJA_MANUAL_TECHNIQUE_ATTACK_KEY = "jja_manual_ct_attack";

    private JjaJujutsucraftDataAccess() {
    }

    public static double jjaGetCurrentSkillValue(@Nullable Entity entity) {
        return entity == null ? 0.0D : entity.getPersistentData().getDouble(SKILL_KEY);
    }

    public static int jjaGetCurrentSkillId(@Nullable Entity entity) {
        return (int) Math.round(jjaGetCurrentSkillValue(entity));
    }

    public static void jjaSetCurrentSkillValue(@Nullable Entity entity, double skillValue) {
        if (entity != null) {
            entity.getPersistentData().putDouble(SKILL_KEY, skillValue);
        }
    }

    public static int jjaGetCopiedSkillId(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTag()) {
            return 0;
        }
        return (int) Math.round(stack.getOrCreateTag().getDouble(SKILL_KEY));
    }

    public static String jjaGetOwnerUuid(@Nullable Entity entity) {
        return entity == null ? "" : entity.getPersistentData().getString(OWNER_UUID_KEY);
    }

    @Nullable
    public static Entity jjaResolveOwnerByUuid(@Nullable LevelAccessor world, @Nullable String ownerUuid) {
        if (world == null || ownerUuid == null || ownerUuid.isBlank()) {
            return null;
        }
        return GetEntityFromUUIDProcedure.execute(world, ownerUuid);
    }

    @Nullable
    public static Entity jjaResolveDirectOwner(@Nullable LevelAccessor world, @Nullable Entity entity) {
        return jjaResolveOwnerByUuid(world, jjaGetOwnerUuid(entity));
    }

    @Nullable
    public static Entity jjaResolveRootOwner(@Nullable LevelAccessor world, @Nullable Entity entity) {
        return jjaResolveRootOwner(entity, false, ownerUuid -> jjaResolveOwnerByUuid(world, ownerUuid));
    }

    @Nullable
    public static Entity jjaResolveRootLivingOwner(@Nullable LevelAccessor world, @Nullable Entity entity) {
        return jjaResolveRootOwner(entity, true, ownerUuid -> jjaResolveOwnerByUuid(world, ownerUuid));
    }

    public static boolean jjaIsManualTechniqueAttack(@Nullable Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(JJA_MANUAL_TECHNIQUE_ATTACK_KEY);
    }

    public static void jjaSetManualTechniqueAttack(@Nullable Entity entity, boolean value) {
        if (entity != null) {
            entity.getPersistentData().putBoolean(JJA_MANUAL_TECHNIQUE_ATTACK_KEY, value);
        }
    }

    public static String jjaGetTargetUuid(@Nullable Entity entity) {
        return entity == null ? "" : entity.getPersistentData().getString(TARGET_UUID_KEY);
    }

    public static void jjaClearTargetUuid(@Nullable Entity entity) {
        if (entity != null) {
            entity.getPersistentData().putString(TARGET_UUID_KEY, "");
        }
    }

    public static void jjaClearMobTarget(@Nullable Mob mob) {
        if (mob == null) {
            return;
        }
        jjaClearTargetState(mob.getPersistentData());
        mob.setTarget(null);
    }

    public static double jjaGetFriendNum(@Nullable Entity entity) {
        return entity == null ? 0.0D : entity.getPersistentData().getDouble(FRIEND_NUM_KEY);
    }

    public static double jjaGetFriendNumWorker(@Nullable Entity entity) {
        return entity == null ? 0.0D : entity.getPersistentData().getDouble(FRIEND_NUM_WORKER_KEY);
    }

    public static boolean jjaHasDomainFlag(@Nullable Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(DOMAIN_FLAG_KEY);
    }

    public static double jjaGetDomainLearnCount(@Nullable Entity entity) {
        return entity == null ? 0.0D : entity.getPersistentData().getDouble(DOMAIN_LEARN_COUNT_KEY);
    }

    public static boolean jjaCanOwnDomain(@Nullable Entity entity) {
        return jjaHasDomainFlag(entity) || jjaGetDomainLearnCount(entity) > 0.0D;
    }

    public static boolean jjaHasDomainCenter(@Nullable Entity entity) {
        return entity != null
            && entity.getPersistentData().contains(DOMAIN_X_KEY)
            && entity.getPersistentData().contains(DOMAIN_Y_KEY)
            && entity.getPersistentData().contains(DOMAIN_Z_KEY);
    }

    @Nullable
    public static Vec3 jjaGetDomainCenter(@Nullable Entity entity) {
        if (!jjaHasDomainCenter(entity)) {
            return null;
        }
        Entity nonNullEntity = java.util.Objects.requireNonNull(entity);
        return new Vec3(
            nonNullEntity.getPersistentData().getDouble(DOMAIN_X_KEY),
            nonNullEntity.getPersistentData().getDouble(DOMAIN_Y_KEY),
            nonNullEntity.getPersistentData().getDouble(DOMAIN_Z_KEY)
        );
    }

    public static void jjaSetDomainCenter(@Nullable Entity entity, @Nullable Vec3 center) {
        if (entity == null || center == null) {
            return;
        }
        entity.getPersistentData().putDouble(DOMAIN_X_KEY, center.x);
        entity.getPersistentData().putDouble(DOMAIN_Y_KEY, center.y);
        entity.getPersistentData().putDouble(DOMAIN_Z_KEY, center.z);
    }

    public static void jjaClearDomainCenter(@Nullable Entity entity) {
        if (entity == null) {
            return;
        }

        entity.getPersistentData().remove(DOMAIN_X_KEY);
        entity.getPersistentData().remove(DOMAIN_Y_KEY);
        entity.getPersistentData().remove(DOMAIN_Z_KEY);
    }

    public static boolean jjaHasDomainPatternOrigin(@Nullable Entity entity) {
        return entity != null
            && entity.getPersistentData().contains(DOMAIN_PATTERN_X_KEY)
            && entity.getPersistentData().contains(DOMAIN_PATTERN_Y_KEY)
            && entity.getPersistentData().contains(DOMAIN_PATTERN_Z_KEY);
    }

    @Nullable
    public static Vec3 jjaGetDomainPatternOrigin(@Nullable Entity entity) {
        if (!jjaHasDomainPatternOrigin(entity)) {
            return null;
        }
        Entity nonNullEntity = java.util.Objects.requireNonNull(entity);
        return new Vec3(
            nonNullEntity.getPersistentData().getDouble(DOMAIN_PATTERN_X_KEY),
            nonNullEntity.getPersistentData().getDouble(DOMAIN_PATTERN_Y_KEY),
            nonNullEntity.getPersistentData().getDouble(DOMAIN_PATTERN_Z_KEY)
        );
    }

    public static void jjaSetDomainPatternOrigin(@Nullable Entity entity, @Nullable Vec3 patternOrigin) {
        if (entity == null || patternOrigin == null) {
            return;
        }
        entity.getPersistentData().putDouble(DOMAIN_PATTERN_X_KEY, patternOrigin.x);
        entity.getPersistentData().putDouble(DOMAIN_PATTERN_Y_KEY, patternOrigin.y);
        entity.getPersistentData().putDouble(DOMAIN_PATTERN_Z_KEY, patternOrigin.z);
    }

    public static void jjaClearDomainPatternOrigin(@Nullable Entity entity) {
        if (entity == null) {
            return;
        }

        entity.getPersistentData().remove(DOMAIN_PATTERN_X_KEY);
        entity.getPersistentData().remove(DOMAIN_PATTERN_Y_KEY);
        entity.getPersistentData().remove(DOMAIN_PATTERN_Z_KEY);
    }

    static void jjaClearTargetState(CompoundTag persistentData) {
        if (persistentData == null) {
            return;
        }
        persistentData.putString(TARGET_UUID_KEY, "");
        persistentData.putDouble(CNT_TARGET_KEY, 0.0D);
    }

    static Entity jjaResolveRootOwner(@Nullable Entity entity, boolean requireLivingOwners, Function<String, Entity> ownerResolver) {
        return jjaResolveRootOwner(
            entity,
            JjaJujutsucraftDataAccess::jjaGetOwnerUuid,
            ownerResolver,
            requireLivingOwners ? LivingEntity.class::isInstance : ignored -> true
        );
    }

    @Nullable
    static <T> T jjaResolveRootOwner(
        @Nullable T entity,
        Function<T, String> ownerUuidGetter,
        Function<String, T> ownerResolver,
        Predicate<T> acceptedOwner
    ) {
        if (entity == null) {
            return null;
        }

        T owner = entity;
        String ownerUuid = normalizeOwnerUuid(ownerUuidGetter.apply(entity));
        int safeCount = 0;
        while (!ownerUuid.isEmpty() && safeCount < MAX_OWNER_DEPTH) {
            T found = ownerResolver.apply(ownerUuid);
            if (found == null || !acceptedOwner.test(found)) {
                break;
            }
            owner = found;
            ownerUuid = normalizeOwnerUuid(ownerUuidGetter.apply(found));
            safeCount++;
        }
        return owner;
    }

    private static String normalizeOwnerUuid(@Nullable String ownerUuid) {
        return ownerUuid == null ? "" : ownerUuid;
    }
}
