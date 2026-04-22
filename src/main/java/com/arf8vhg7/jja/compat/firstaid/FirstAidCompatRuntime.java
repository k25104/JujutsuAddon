package com.arf8vhg7.jja.compat.firstaid;

import com.arf8vhg7.jja.compat.JjaOptionalModHelper;
import com.arf8vhg7.jja.compat.JjaReflectiveCompatSupport;
import com.mojang.logging.LogUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public final class FirstAidCompatRuntime {
    private static final String SNAPSHOT_DAMAGE_MODEL_KEY = "damageModel";
    private static final String SNAPSHOT_RATIO_PREFIX = "ratio_";
    private static final String SNAPSHOT_MISSING_PREFIX = "missing_";
    private static final String SNAPSHOT_PART_HEALTH_PREFIX = "part_health_";
    private static final float FULL_HEAL_EPSILON = 0.05F;
    private static final float DAMAGE_EPSILON = 1.0E-4F;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FIRST_AID_MODID = "firstaid";
    private static final String COMMON_UTILS_CLASS_NAME = "ichttt.mods.firstaid.common.util.CommonUtils";
    private static final String FIRST_AID_CLASS_NAME = "ichttt.mods.firstaid.FirstAid";
    private static final String ABSTRACT_PLAYER_DAMAGE_MODEL_CLASS_NAME = "ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel";
    private static final String PLAYER_DAMAGE_MODEL_CLASS_NAME = "ichttt.mods.firstaid.common.damagesystem.PlayerDamageModel";
    private static final String DAMAGEABLE_PART_CLASS_NAME = "ichttt.mods.firstaid.common.damagesystem.DamageablePart";
    private static final String ABSTRACT_DAMAGEABLE_PART_CLASS_NAME = "ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart";
    private static final String ABSTRACT_DEBUFF_CLASS_NAME = "ichttt.mods.firstaid.common.damagesystem.debuff.AbstractDebuff";
    private static final String HEALTH_DISTRIBUTION_CLASS_NAME = "ichttt.mods.firstaid.common.damagesystem.distribution.HealthDistribution";
    private static final String MESSAGE_SYNC_DAMAGE_MODEL_CLASS_NAME = "ichttt.mods.firstaid.common.network.MessageSyncDamageModel";
    private static final String SYNCHED_ENTITY_DATA_WRAPPER_CLASS_NAME = "ichttt.mods.firstaid.common.SynchedEntityDataWrapper";
    private static final String ON_HIT_DEBUFF_CLASS_NAME = "ichttt.mods.firstaid.common.damagesystem.debuff.OnHitDebuff";
    private static final String PR_COMPAT_MANAGER_CLASS_NAME = "ichttt.mods.firstaid.common.compat.playerrevive.PRCompatManager";
    private static final String PR_COMPAT_HANDLER_CLASS_NAME = "ichttt.mods.firstaid.common.compat.playerrevive.IPRCompatHandler";
    private static final String ENTITY_DATA_FIELD_NAME = "f_19804_";
    private static final String DATA_HEALTH_ID_FIELD_NAME = "f_20961_";
    private static final Set<ResourceLocation> SUPPRESSED_ON_HIT_EFFECTS = Set.of(
        ResourceLocation.withDefaultNamespace("blindness"),
        ResourceLocation.withDefaultNamespace("nausea")
    );
    private static final String[] PART_FIELD_NAMES = {
        "HEAD",
        "LEFT_ARM",
        "LEFT_LEG",
        "LEFT_FOOT",
        "BODY",
        "RIGHT_ARM",
        "RIGHT_LEG",
        "RIGHT_FOOT"
    };

    private static final JjaReflectiveCompatSupport.InitState INIT_STATE = new JjaReflectiveCompatSupport.InitState();
    private static Method getDamageModelMethod;
    private static Method serializeNbtMethod;
    private static Method deserializeNbtMethod;
    private static Method runScaleLogicMethod;
    private static Method scheduleResyncMethod;
    private static Method calculateNewCurrentHealthMethod;
    private static Method distributeHealthMethod;
    private static Method getMaxHealthMethod;
    private static Method toggleTrackingMethod;
    private static Method toggleBeingRevivedMethod;
    private static Method setImplMethod;
    private static Method revivePlayerMethod;
    private static Field entityDataField;
    private static Field dataHealthIdField;
    private static Field currentHealthField;
    private static Field debuffsField;
    private static Field debuffEffectField;
    private static Field prCompatHandlerField;
    private static Field[] partFields;
    private static Class<?> onHitDebuffClass;
    private static Class<?> synchedEntityDataWrapperClass;
    private static Class<?> prCompatHandlerClass;
    private static Constructor<?> syncDamageModelConstructor;
    private static SimpleChannel networkingChannel;

    private FirstAidCompatRuntime() {
    }

    public interface FallbackKnockoutBridge {
        boolean tryKnockOut(Player player, @Nullable Object source);

        boolean isWaiting(Player player);
    }

    public static final class DamageModelInspection {
        private final float vanillaHealth;

        private DamageModelInspection(float vanillaHealth) {
            this.vanillaHealth = vanillaHealth;
        }

        public float vanillaHealth() {
            return vanillaHealth;
        }
    }

    public static boolean isFirstAidLoaded() {
        return JjaOptionalModHelper.isLoaded(FIRST_AID_MODID);
    }

    @Nullable
    public static Object getDamageModel(@Nullable Player player) {
        if (player == null || !ensureInitialized()) {
            return null;
        }
        try {
            return getDamageModelMethod.invoke(null, player);
        } catch (ReflectiveOperationException exception) {
            logCompatError("get damage model", exception);
            return null;
        }
    }

    @Nullable
    public static CompoundTag snapshotDamageModel(@Nullable Player player) {
        return queryDamageModel(player, "snapshot damage model", FirstAidCompatRuntime::serializeDamageModel, null);
    }

    @Nullable
    public static CompoundTag snapshotDamageModelWithRatios(@Nullable Player player) {
        return queryDamageModel(player, "snapshot damage model ratios", damageModel -> {
            CompoundTag snapshot = new CompoundTag();
            snapshot.put(SNAPSHOT_DAMAGE_MODEL_KEY, serializeDamageModel(damageModel));
            writePartSnapshot(snapshot, damageModel, SNAPSHOT_RATIO_PREFIX, (index, part) -> {
                int maxHealth = maxHealth(part);
                return maxHealth <= 0 ? 0.0F : currentHealth(part) / (float) maxHealth;
            });
            return snapshot;
        }, null);
    }

    public static void restoreDamageModel(@Nullable Player player, @Nullable CompoundTag snapshot) {
        if (player == null || snapshot == null) {
            return;
        }
        applyDamageModel(player, "restore damage model", damageModel -> {
            deserializeNbtMethod.invoke(damageModel, snapshot.copy());
            runScaleLogicMethod.invoke(damageModel, player);
            scheduleResyncMethod.invoke(damageModel);
        });
    }

    public static void restoreDamageModelPreservingRatios(@Nullable Player player, @Nullable CompoundTag snapshot) {
        if (player == null || snapshot == null) {
            return;
        }
        CompoundTag damageModelSnapshot = snapshot.contains(SNAPSHOT_DAMAGE_MODEL_KEY)
            ? snapshot.getCompound(SNAPSHOT_DAMAGE_MODEL_KEY).copy()
            : snapshot.copy();
        applyDamageModel(player, "restore damage model ratios", damageModel -> {
            deserializeNbtMethod.invoke(damageModel, damageModelSnapshot);
            runScaleLogicMethod.invoke(damageModel, player);
            restorePartSnapshot(damageModel, snapshot, SNAPSHOT_RATIO_PREFIX, (index, part, ratio) -> {
                int maxHealth = maxHealth(part);
                float clampedRatio = Math.max(0.0F, ratio);
                float restoredHealth = clampedRatio * maxHealth;
                if (restoredHealth >= maxHealth - 1.0E-4F) {
                    restoredHealth = maxHealth;
                }
                currentHealthField.setFloat(part, Math.min(restoredHealth, maxHealth));
            });
            scheduleResyncMethod.invoke(damageModel);
        });
    }

    public static void runScaleLogic(@Nullable Player player) {
        if (player == null) {
            return;
        }
        applyDamageModel(player, "run scale logic", damageModel -> runScaleLogicMethod.invoke(damageModel, player));
    }

    public static boolean distributeHeal(@Nullable Player player, float amount) {
        if (player == null || amount <= 0.0F || !ensureInitialized()) {
            return false;
        }
        try {
            distributeHealthMethod.invoke(null, amount, player, false);
            return true;
        } catch (ReflectiveOperationException exception) {
            logCompatError("distribute heal", exception);
            return false;
        }
    }

    public static void setUniformHealthRatio(@Nullable Player player, float ratio) {
        if (player == null || !ensureInitialized()) {
            return;
        }
        float clampedRatio = Math.max(0.0F, Math.min(1.0F, ratio));
        applyDamageModel(player, "set uniform health ratio", damageModel -> {
            forEachDamagePart(damageModel, (index, part) -> {
                if (part == null) {
                    return;
                }
                int maxHealth = maxHealth(part);
                float restoredHealth = clampedRatio * maxHealth;
                if (restoredHealth >= maxHealth - DAMAGE_EPSILON) {
                    restoredHealth = maxHealth;
                }
                currentHealthField.setFloat(part, Math.min(restoredHealth, maxHealth));
            });
            scheduleResyncMethod.invoke(damageModel);
        });
    }

    public static boolean areAllPartHealthRatiosAtLeast(@Nullable Player player, float minimumRatio) {
        if (player == null || !ensureInitialized()) {
            return false;
        }
        float clampedRatio = Math.max(0.0F, minimumRatio);
        return queryDamageModel(player, "check part health ratios", damageModel ->
            allDamageParts(damageModel, (index, part) -> {
                if (part == null) {
                    return false;
                }
                return isPartHealthRatioAtLeast(currentHealth(part), maxHealth(part), clampedRatio);
            }),
            false
        );
    }

    @Nullable
    public static CompoundTag snapshotMissingHealth(@Nullable Player player) {
        return queryDamageModel(
            player,
            "snapshot missing health",
            damageModel -> capturePartSnapshot(damageModel, SNAPSHOT_MISSING_PREFIX, (index, part) -> Math.max(0.0F, maxHealth(part) - currentHealth(part))),
            null
        );
    }

    public static void restoreMissingHealthAfterScale(@Nullable Player player, @Nullable CompoundTag snapshot) {
        if (player == null || snapshot == null) {
            return;
        }
        applyDamageModel(player, "restore missing health after scale", damageModel -> {
            runScaleLogicMethod.invoke(damageModel, player);
            restoreMissingHealthSnapshot(damageModel, snapshot);
        });
    }

    public static void restoreMissingHealth(@Nullable Player player, @Nullable CompoundTag snapshot) {
        if (player == null || snapshot == null) {
            return;
        }
        applyDamageModel(player, "restore missing health", damageModel -> restoreMissingHealthSnapshot(damageModel, snapshot));
    }

    public static boolean trySyncDamageModelNow(@Nullable Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !ensureInitialized()) {
            return false;
        }
        Object damageModel = getDamageModel(player);
        if (damageModel == null || networkingChannel == null || syncDamageModelConstructor == null) {
            return false;
        }
        try {
            Object message = syncDamageModelConstructor.newInstance(damageModel, true);
            networkingChannel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
            return true;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            LOGGER.warn("Failed to sync damage model immediately via FirstAid compat reflection.", exception);
            return false;
        }
    }

    @Nullable
    public static CompoundTag snapshotPartHealth(@Nullable Player player) {
        return queryDamageModel(
            player,
            "snapshot part health",
            damageModel -> capturePartSnapshot(damageModel, SNAPSHOT_PART_HEALTH_PREFIX, (index, part) -> currentHealth(part)),
            null
        );
    }

    public static boolean hasDamageApplied(@Nullable Player player, @Nullable CompoundTag snapshot) {
        if (player == null || snapshot == null || !ensureInitialized()) {
            return false;
        }
        return queryDamageModel(player, "check applied damage", damageModel ->
            anyDamagePart(damageModel, (index, part) -> {
                String key = SNAPSHOT_PART_HEALTH_PREFIX + index;
                return snapshot.contains(key)
                    && part != null
                    && currentHealth(part) < snapshot.getFloat(key) - DAMAGE_EPSILON;
            }),
            false
        );
    }

    public static float calculateCurrentVanillaHealth(@Nullable Player player) {
        if (player == null || !ensureInitialized()) {
            return player == null ? 0.0F : player.getHealth();
        }
        return queryDamageModel(player, "calculate current vanilla health", damageModel -> calculateVanillaHealth(player, damageModel), player.getHealth());
    }

    public static float getEffectiveHealth(@Nullable LivingEntity entity) {
        if (!(entity instanceof Player player) || !ensureInitialized()) {
            return entity == null ? 0.0F : entity.getHealth();
        }
        return calculateCurrentVanillaHealth(player);
    }

    public static float getEffectiveHealthRatio(@Nullable LivingEntity entity) {
        if (entity == null) {
            return 0.0F;
        }
        float maxHealth = entity.getMaxHealth();
        if (!(maxHealth > 0.0F)) {
            return 1.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, getEffectiveHealth(entity) / maxHealth));
    }

    public static void syncVanillaHealth(@Nullable Player player) {
        if (player == null || !ensureInitialized()) {
            return;
        }
        float currentHealth = queryDamageModel(
            player,
            "calculate current vanilla health",
            damageModel -> calculateVanillaHealth(player, damageModel),
            player.getHealth()
        );
        if (!Float.isFinite(currentHealth)) {
            return;
        }
        try {
            toggleTracking(player, false);
            setDataHealth(player, currentHealth);
        } finally {
            toggleTracking(player, true);
        }
    }

    public static void toggleTracking(@Nullable Player player, boolean tracking) {
        applyEntityData(player, "toggle tracking", entityData -> toggleTrackingMethod.invoke(entityData, tracking));
    }

    public static void setBeingRevived(@Nullable Player player, boolean beingRevived) {
        applyEntityData(player, "toggle being revived", entityData -> toggleBeingRevivedMethod.invoke(entityData, beingRevived));
    }

    public static void reviveDamageModel(@Nullable Player player) {
        if (player == null) {
            return;
        }
        applyDamageModel(player, "revive damage model", damageModel -> revivePlayerMethod.invoke(damageModel, player));
    }

    public static void setTrackedHealthDirect(@Nullable Player player, float health) {
        if (player == null || !Float.isFinite(health)) {
            return;
        }
        setDataHealth(player, health);
    }

    public static boolean installReviveCompat(@Nullable FallbackKnockoutBridge bridge) {
        if (bridge == null || !ensureInitialized()) {
            return false;
        }
        try {
            Object currentHandler = prCompatHandlerField.get(null);
            if (currentHandler != null && Proxy.isProxyClass(currentHandler.getClass())) {
                InvocationHandler invocationHandler = Proxy.getInvocationHandler(currentHandler);
                if (invocationHandler instanceof ReviveCompatInvocationHandler) {
                    return true;
                }
            }

            Object proxy = Proxy.newProxyInstance(
                prCompatHandlerClass.getClassLoader(),
                new Class<?>[] {prCompatHandlerClass},
                new ReviveCompatInvocationHandler(currentHandler, bridge)
            );
            prCompatHandlerField.set(null, proxy);
            return true;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            LOGGER.warn("Failed to install FirstAid revive compat bridge.", exception);
            return false;
        }
    }

    public static void scheduleResync(@Nullable Player player) {
        if (player == null) {
            return;
        }
        applyDamageModel(player, "schedule resync", damageModel -> scheduleResyncMethod.invoke(damageModel));
    }

    public static void stripOnHitDebuffs(@Nullable Player player) {
        applyDamageModel(player, "strip on-hit debuffs", damageModel ->
            forEachDamagePart(damageModel, (index, part) -> stripPartDebuffs(part))
        );
    }

    public static boolean isFullyHealed(@Nullable LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return entity != null && getEffectiveHealth(entity) >= entity.getMaxHealth() - FULL_HEAL_EPSILON;
        }
        if (!ensureInitialized()) {
            return getEffectiveHealth(player) >= player.getMaxHealth() - FULL_HEAL_EPSILON;
        }
        return queryDamageModel(player, "check full heal", damageModel -> {
            boolean allPartsFull = allDamageParts(damageModel, (index, part) ->
                part != null && maxHealth(part) - currentHealth(part) <= FULL_HEAL_EPSILON
            );
            return allPartsFull || getEffectiveHealth(player) >= player.getMaxHealth() - FULL_HEAL_EPSILON;
        }, getEffectiveHealth(player) >= player.getMaxHealth() - FULL_HEAL_EPSILON);
    }

    public static boolean isEffectivelyAtFullHealth(@Nullable LivingEntity entity) {
        return entity != null && (getEffectiveHealth(entity) >= entity.getMaxHealth() - FULL_HEAL_EPSILON || isFullyHealed(entity));
    }

    @Nullable
    public static DamageModelInspection inspectDamageModel(@Nullable Player player, boolean stripOnHitDebuffs) {
        if (player == null || !ensureInitialized()) {
            return player == null ? null : new DamageModelInspection(player.getHealth());
        }
        return queryDamageModel(player, "inspect damage model", damageModel -> {
            forEachDamagePart(damageModel, (index, part) -> {
                if (stripOnHitDebuffs) {
                    stripPartDebuffs(part);
                }
            });
            return new DamageModelInspection(calculateVanillaHealth(player, damageModel));
        }, new DamageModelInspection(player.getHealth()));
    }

    private static void stripPartDebuffs(@Nullable Object part) throws ReflectiveOperationException {
        if (part == null) {
            return;
        }
        Object debuffArray = debuffsField.get(part);
        if (!(debuffArray instanceof Object[] debuffs) || debuffs.length == 0) {
            return;
        }

        int kept = 0;
        for (Object debuff : debuffs) {
            if (!shouldSuppressOnHitDebuff(debuff)) {
                kept++;
            }
        }
        if (kept == debuffs.length) {
            return;
        }

        Object filtered = Array.newInstance(debuffs.getClass().getComponentType(), kept);
        int writeIndex = 0;
        for (Object debuff : debuffs) {
            if (!shouldSuppressOnHitDebuff(debuff)) {
                Array.set(filtered, writeIndex++, debuff);
            }
        }
        debuffsField.set(part, filtered);
    }

    private static boolean shouldSuppressOnHitDebuff(@Nullable Object debuff) throws ReflectiveOperationException {
        if (debuff == null || !onHitDebuffClass.isInstance(debuff)) {
            return false;
        }
        Object effect = debuffEffectField.get(debuff);
        if (!(effect instanceof MobEffect mobEffect)) {
            return false;
        }
        ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
        return effectId != null && SUPPRESSED_ON_HIT_EFFECTS.contains(effectId);
    }

    private static CompoundTag serializeDamageModel(Object damageModel) throws ReflectiveOperationException {
        return ((CompoundTag) serializeNbtMethod.invoke(damageModel)).copy();
    }

    private static float calculateVanillaHealth(Player player, Object damageModel) throws ReflectiveOperationException {
        Object result = calculateNewCurrentHealthMethod.invoke(damageModel, player);
        if (result instanceof Float value && Float.isFinite(value)) {
            return value;
        }
        return player.getHealth();
    }

    @Nullable
    private static CompoundTag capturePartSnapshot(
        Object damageModel,
        String keyPrefix,
        DamagePartFloatFunction function
    ) throws ReflectiveOperationException {
        CompoundTag snapshot = new CompoundTag();
        writePartSnapshot(snapshot, damageModel, keyPrefix, function);
        return snapshot.isEmpty() ? null : snapshot;
    }

    private static void writePartSnapshot(
        CompoundTag snapshot,
        Object damageModel,
        String keyPrefix,
        DamagePartFloatFunction function
    ) throws ReflectiveOperationException {
        forEachDamagePart(damageModel, (index, part) -> {
            if (part == null) {
                return;
            }
            snapshot.putFloat(keyPrefix + index, function.apply(index, part));
        });
    }

    private static void restorePartSnapshot(
        Object damageModel,
        CompoundTag snapshot,
        String keyPrefix,
        DamagePartFloatConsumer consumer
    ) throws ReflectiveOperationException {
        forEachDamagePart(damageModel, (index, part) -> {
            String key = keyPrefix + index;
            if (!snapshot.contains(key) || part == null) {
                return;
            }
            consumer.accept(index, part, snapshot.getFloat(key));
        });
    }

    private static void restoreMissingHealthSnapshot(Object damageModel, CompoundTag snapshot) throws ReflectiveOperationException {
        restorePartSnapshot(damageModel, snapshot, SNAPSHOT_MISSING_PREFIX, (index, part, missingHealth) -> {
            int maxHealth = maxHealth(part);
            float clampedMissingHealth = Math.max(0.0F, missingHealth);
            float restoredHealth = Math.max(0.0F, maxHealth - clampedMissingHealth);
            if (maxHealth - restoredHealth <= DAMAGE_EPSILON) {
                restoredHealth = maxHealth;
            }
            currentHealthField.setFloat(part, Math.min(restoredHealth, maxHealth));
        });
    }

    private static void forEachDamagePart(Object damageModel, DamagePartConsumer consumer) throws ReflectiveOperationException {
        for (int index = 0; index < partFields.length; index++) {
            consumer.accept(index, partFields[index].get(damageModel));
        }
    }

    private static boolean anyDamagePart(Object damageModel, DamagePartPredicate predicate) throws ReflectiveOperationException {
        for (int index = 0; index < partFields.length; index++) {
            if (predicate.test(index, partFields[index].get(damageModel))) {
                return true;
            }
        }
        return false;
    }

    private static boolean allDamageParts(Object damageModel, DamagePartPredicate predicate) throws ReflectiveOperationException {
        for (int index = 0; index < partFields.length; index++) {
            if (!predicate.test(index, partFields[index].get(damageModel))) {
                return false;
            }
        }
        return true;
    }

    private static int maxHealth(Object part) throws ReflectiveOperationException {
        return (Integer) getMaxHealthMethod.invoke(part);
    }

    private static float currentHealth(Object part) throws ReflectiveOperationException {
        return currentHealthField.getFloat(part);
    }

    static boolean isPartHealthRatioAtLeast(float currentHealth, int maxHealth, float minimumRatio) {
        if (maxHealth <= 0) {
            return true;
        }
        float clampedRatio = Math.max(0.0F, minimumRatio);
        return currentHealth / maxHealth + DAMAGE_EPSILON >= clampedRatio;
    }

    @FunctionalInterface
    private interface DamagePartConsumer {
        void accept(int index, @Nullable Object part) throws ReflectiveOperationException;
    }

    @FunctionalInterface
    private interface DamagePartPredicate {
        boolean test(int index, @Nullable Object part) throws ReflectiveOperationException;
    }

    @FunctionalInterface
    private interface DamagePartFloatFunction {
        float apply(int index, Object part) throws ReflectiveOperationException;
    }

    @FunctionalInterface
    private interface DamagePartFloatConsumer {
        void accept(int index, Object part, float value) throws ReflectiveOperationException;
    }

    @FunctionalInterface
    private interface DamageModelFunction<T> {
        T apply(Object damageModel) throws ReflectiveOperationException;
    }

    @FunctionalInterface
    private interface DamageModelConsumer {
        void accept(Object damageModel) throws ReflectiveOperationException;
    }

    @FunctionalInterface
    private interface EntityDataConsumer {
        void accept(Object entityData) throws ReflectiveOperationException;
    }

    private static <T> T queryDamageModel(
        @Nullable Player player,
        String action,
        DamageModelFunction<T> function,
        T fallback
    ) {
        Object damageModel = getDamageModel(player);
        if (damageModel == null) {
            return fallback;
        }
        try {
            return function.apply(damageModel);
        } catch (ReflectiveOperationException exception) {
            logCompatError(action, exception);
            return fallback;
        }
    }

    private static void applyDamageModel(
        @Nullable Player player,
        String action,
        DamageModelConsumer consumer
    ) {
        Object damageModel = getDamageModel(player);
        if (damageModel == null) {
            return;
        }
        try {
            consumer.accept(damageModel);
        } catch (ReflectiveOperationException exception) {
            logCompatError(action, exception);
        }
    }

    private static boolean ensureInitialized() {
        return JjaReflectiveCompatSupport.ensureInitialized(
            INIT_STATE,
            FirstAidCompatRuntime.class,
            FirstAidCompatRuntime::isFirstAidLoaded,
            () -> {
                Class<?> commonUtilsClass = Class.forName(COMMON_UTILS_CLASS_NAME);
                Class<?> firstAidClass = Class.forName(FIRST_AID_CLASS_NAME);
                Class<?> abstractPlayerDamageModelClass = Class.forName(ABSTRACT_PLAYER_DAMAGE_MODEL_CLASS_NAME);
                Class<?> playerDamageModelClass = Class.forName(PLAYER_DAMAGE_MODEL_CLASS_NAME);
                Class<?> damageablePartClass = Class.forName(DAMAGEABLE_PART_CLASS_NAME);
                Class<?> abstractDamageablePartClass = Class.forName(ABSTRACT_DAMAGEABLE_PART_CLASS_NAME);
                Class<?> abstractDebuffClass = Class.forName(ABSTRACT_DEBUFF_CLASS_NAME);
                Class<?> healthDistributionClass = Class.forName(HEALTH_DISTRIBUTION_CLASS_NAME);
                Class<?> messageSyncDamageModelClass = Class.forName(MESSAGE_SYNC_DAMAGE_MODEL_CLASS_NAME);
                synchedEntityDataWrapperClass = Class.forName(SYNCHED_ENTITY_DATA_WRAPPER_CLASS_NAME);
                Class<?> prCompatManagerClass = Class.forName(PR_COMPAT_MANAGER_CLASS_NAME);
                prCompatHandlerClass = Class.forName(PR_COMPAT_HANDLER_CLASS_NAME);

                getDamageModelMethod = commonUtilsClass.getMethod("getDamageModel", Player.class);
                serializeNbtMethod = playerDamageModelClass.getMethod("serializeNBT");
                deserializeNbtMethod = playerDamageModelClass.getMethod("deserializeNBT", CompoundTag.class);
                runScaleLogicMethod = playerDamageModelClass.getMethod("runScaleLogic", Player.class);
                scheduleResyncMethod = playerDamageModelClass.getMethod("scheduleResync");
                revivePlayerMethod = playerDamageModelClass.getMethod("revivePlayer", Player.class);
                calculateNewCurrentHealthMethod = playerDamageModelClass.getDeclaredMethod("calculateNewCurrentHealth", Player.class);
                calculateNewCurrentHealthMethod.setAccessible(true);
                distributeHealthMethod = healthDistributionClass.getMethod("distributeHealth", float.class, Player.class, boolean.class);
                getMaxHealthMethod = abstractDamageablePartClass.getMethod("getMaxHealth");
                toggleTrackingMethod = synchedEntityDataWrapperClass.getMethod("toggleTracking", boolean.class);
                toggleBeingRevivedMethod = synchedEntityDataWrapperClass.getMethod("toggleBeingRevived", boolean.class);
                setImplMethod = synchedEntityDataWrapperClass.getMethod("set_impl", EntityDataAccessor.class, Object.class);
                syncDamageModelConstructor = messageSyncDamageModelClass.getConstructor(abstractPlayerDamageModelClass, boolean.class);
                networkingChannel = (SimpleChannel) firstAidClass.getField("NETWORKING").get(null);
                entityDataField = ObfuscationReflectionHelper.findField(Entity.class, ENTITY_DATA_FIELD_NAME);
                dataHealthIdField = ObfuscationReflectionHelper.findField(LivingEntity.class, DATA_HEALTH_ID_FIELD_NAME);
                currentHealthField = abstractDamageablePartClass.getField("currentHealth");
                debuffsField = damageablePartClass.getDeclaredField("debuffs");
                debuffsField.setAccessible(true);
                debuffEffectField = abstractDebuffClass.getField("effect");
                prCompatHandlerField = prCompatManagerClass.getDeclaredField("handler");
                prCompatHandlerField.setAccessible(true);

                partFields = new Field[PART_FIELD_NAMES.length];
                for (int i = 0; i < PART_FIELD_NAMES.length; i++) {
                    partFields[i] = playerDamageModelClass.getField(PART_FIELD_NAMES[i]);
                }
                onHitDebuffClass = Class.forName(ON_HIT_DEBUFF_CLASS_NAME);
            },
            exception -> {
                LOGGER.warn("Failed to initialize FirstAid compat reflection. Disabling FirstAid integration.", exception);
            }
        );
    }

    private static void logCompatError(String action, ReflectiveOperationException exception) {
        JjaReflectiveCompatSupport.logCompatError(LOGGER, "FirstAid", action, exception);
    }

    private static void setDataHealth(Player player, float health) {
        EntityDataAccessor<?> dataHealthId = getDataHealthId();
        if (dataHealthId == null) {
            return;
        }
        applyEntityData(player, "set tracked health", entityData ->
            setImplMethod.invoke(entityData, dataHealthId, Float.valueOf(health))
        );
    }

    @Nullable
    private static Object getEntityData(@Nullable Player player) {
        if (player == null || !ensureInitialized()) {
            return null;
        }
        try {
            return entityDataField.get(player);
        } catch (ReflectiveOperationException exception) {
            logCompatError("get entity data", exception);
            return null;
        }
    }

    @Nullable
    private static EntityDataAccessor<?> getDataHealthId() {
        if (!ensureInitialized()) {
            return null;
        }
        try {
            Object value = dataHealthIdField.get(null);
            return value instanceof EntityDataAccessor<?> accessor ? accessor : null;
        } catch (ReflectiveOperationException exception) {
            logCompatError("get data health id", exception);
            return null;
        }
    }

    private static void applyEntityData(
        @Nullable Player player,
        String action,
        EntityDataConsumer consumer
    ) {
        Object entityData = getEntityData(player);
        if (entityData == null || !ensureInitialized() || !synchedEntityDataWrapperClass.isInstance(entityData)) {
            return;
        }
        try {
            consumer.accept(entityData);
        } catch (ReflectiveOperationException exception) {
            logCompatError(action, exception);
        }
    }

    private static final class ReviveCompatInvocationHandler implements InvocationHandler {
        private final Object existingHandler;
        private final FallbackKnockoutBridge bridge;

        private ReviveCompatInvocationHandler(@Nullable Object existingHandler, FallbackKnockoutBridge bridge) {
            this.existingHandler = existingHandler;
            this.bridge = bridge;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                    case "toString" -> "JjaFirstAidReviveCompatProxy[" + existingHandler + "]";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == (args == null || args.length == 0 ? null : args[0]);
                    default -> method.invoke(this, args);
                };
            }
            String methodName = method.getName();
            if ("tryKnockOutPlayer".equals(methodName) && args != null && args.length == 2 && args[0] instanceof Player player) {
                if (invokeExistingBoolean(method, args)) {
                    return true;
                }
                try {
                    setBeingRevived(player, true);
                    if (bridge.tryKnockOut(player, args[1])) {
                        return true;
                    }
                } finally {
                    if (!bridge.isWaiting(player)) {
                        setBeingRevived(player, false);
                    }
                }
                return false;
            }
            if ("isBleeding".equals(methodName) && args != null && args.length == 1 && args[0] instanceof Player player) {
                return invokeExistingBoolean(method, args) || bridge.isWaiting(player);
            }
            if (existingHandler == null) {
                return null;
            }
            return method.invoke(existingHandler, args);
        }

        private boolean invokeExistingBoolean(Method method, Object[] args) throws ReflectiveOperationException {
            if (existingHandler == null) {
                return false;
            }
            Object result = method.invoke(existingHandler, args);
            return result instanceof Boolean value && value;
        }
    }
}
