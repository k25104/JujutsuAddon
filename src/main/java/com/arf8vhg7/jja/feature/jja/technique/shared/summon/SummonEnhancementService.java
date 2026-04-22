package com.arf8vhg7.jja.feature.jja.technique.shared.summon;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.family.megumi.MegumiSummonBranchResolver;
import com.arf8vhg7.jja.feature.jja.technique.shared.activation.TechniquePreviewCostService;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.JjaPlayerStateSync;
import com.arf8vhg7.jja.feature.player.state.model.PlayerAddonStatsState;
import com.arf8vhg7.jja.feature.jja.technique.shared.summon.SummonEnhancementCatalog.ResolvedSummon;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.slf4j.Logger;

public final class SummonEnhancementService {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String JJA_SUMMON_BASE_MAX_HEALTH = "jjaSummonEnhancementBaseMaxHealth";
    private static final String JJA_SUMMON_ACTIVATION_ID = "jjaSummonEnhancementActivationId";

    private static final Map<UUID, Preview> PREVIEWS = new ConcurrentHashMap<>();
    private static final Map<UUID, PendingActivation> PENDING_ACTIVATIONS = new ConcurrentHashMap<>();

    private SummonEnhancementService() {
    }

    public static boolean canToggleForActiveCt(ServerPlayer player) {
        return player != null && SummonEnhancementCatalog.hasToggleableSkillForActiveCt(player);
    }

    public static boolean toggle(ServerPlayer player) {
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(player);
        if (player == null || addonStats == null) {
            return false;
        }
        addonStats.setShikigamiEnhancementEnabled(!addonStats.isShikigamiEnhancementEnabled());
        refreshCurrentSelection(player);
        JjaPlayerStateSync.sync(player);
        return addonStats.isShikigamiEnhancementEnabled();
    }

    public static void onTechniqueDecide(Entity entity, double baseCost, double playerCt, double playerSelect, String currentTechniqueName) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVariables == null) {
            return;
        }
        int activeCtId = (int) Math.round(playerCt);
        int selectedTechnique = (int) Math.round(playerSelect);
        if (MegumiSummonBranchResolver.isUntamedSelection(player, activeCtId, selectedTechnique, currentTechniqueName)) {
            PREVIEWS.remove(player.getUUID());
            if (playerVariables.PlayerSelectCurseTechniqueCost != 0.0) {
                playerVariables.PlayerSelectCurseTechniqueCost = 0.0;
                playerVariables.syncPlayerVariables(player);
            }
            return;
        }
        Preview preview = createPreview(
            player,
            baseCost,
            activeCtId,
            selectedTechnique,
            currentTechniqueName
        );
        UUID playerId = player.getUUID();
        if (preview == null) {
            PREVIEWS.remove(playerId);
            return;
        }
        PREVIEWS.put(playerId, preview);
        double displayedCost = resolveDisplayedTechniqueCost(baseCost, preview.additionalCost());
        if (playerVariables.PlayerSelectCurseTechniqueCost != displayedCost) {
            playerVariables.PlayerSelectCurseTechniqueCost = displayedCost;
            playerVariables.syncPlayerVariables(player);
        }
    }

    public static void refreshCurrentSelection(ServerPlayer player) {
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVariables == null) {
            return;
        }
        int activeCtId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player);
        int selectedTechnique = (int) Math.round(playerVariables.PlayerSelectCurseTechnique);
        if (MegumiSummonBranchResolver.isUntamedSelection(
            player,
            activeCtId,
            selectedTechnique,
            playerVariables.PlayerSelectCurseTechniqueName
        )) {
            PREVIEWS.remove(player.getUUID());
            if (playerVariables.PlayerSelectCurseTechniqueCost != 0.0) {
                playerVariables.PlayerSelectCurseTechniqueCost = 0.0;
                playerVariables.syncPlayerVariables(player);
            }
            return;
        }
        double baseCost = playerVariables.PlayerSelectCurseTechniqueCostOrgin;
        double displayedBaseCost = TechniquePreviewCostService.resolveDisplayedCost(player, playerVariables, baseCost);
        Preview preview = createPreview(
            player,
            displayedBaseCost,
            activeCtId,
            selectedTechnique,
            playerVariables.PlayerSelectCurseTechniqueName
        );
        UUID playerId = player.getUUID();
        if (preview == null) {
            PREVIEWS.remove(playerId);
            if (playerVariables.PlayerSelectCurseTechniqueCost != displayedBaseCost) {
                playerVariables.PlayerSelectCurseTechniqueCost = displayedBaseCost;
                playerVariables.syncPlayerVariables(player);
            }
            return;
        }
        PREVIEWS.put(playerId, preview);
        double displayedCost = resolveDisplayedTechniqueCost(displayedBaseCost, preview.additionalCost());
        if (playerVariables.PlayerSelectCurseTechniqueCost != displayedCost) {
            playerVariables.PlayerSelectCurseTechniqueCost = displayedCost;
            playerVariables.syncPlayerVariables(player);
        }
    }

    public static void onTechniqueStarted(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVariables != null && MegumiSummonBranchResolver.isUntamedSelection(
            player,
            JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player),
            (int) Math.round(playerVariables.PlayerSelectCurseTechnique),
            playerVariables.PlayerSelectCurseTechniqueName
        )) {
            PREVIEWS.remove(player.getUUID());
            return;
        }
        Preview preview = resolvePreviewForCurrentSelection(player);
        if (preview == null) {
            return;
        }
        int startedSkillId = JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(player);
        if (startedSkillId != preview.skillId()) {
            return;
        }
        PENDING_ACTIVATIONS.put(
            player.getUUID(),
            new PendingActivation(
                UUID.randomUUID(),
                preview.skillId(),
                preview.expectedEntityTypes(),
                preview.expectedCount(),
                preview.hpMultiplier(),
                player.serverLevel().getGameTime() + preview.pendingValidityTicks()
            )
        );
    }

    public static void tryApplyPending(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity) || entity.level().isClientSide()) {
            return;
        }
        UUID ownerId = parseOwnerId(entity);
        if (ownerId == null) {
            return;
        }
        tryApplyPending(ownerId, livingEntity);
    }

    public static void tryApplyPending(Entity owner, Entity summon) {
        if (owner == null || summon == null) {
            return;
        }
        tryApplyPending(owner.getUUID(), summon);
    }

    public static void clearEnhancement(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        AttributeInstance maxHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }
        if (!hasStoredBaseMaxHealth(livingEntity)) {
            livingEntity.getPersistentData().remove(JJA_SUMMON_ACTIVATION_ID);
            return;
        }
        double originalBaseMaxHealth = livingEntity.getPersistentData().getDouble(JJA_SUMMON_BASE_MAX_HEALTH);
        if (originalBaseMaxHealth <= 0.0D) {
            livingEntity.getPersistentData().remove(JJA_SUMMON_BASE_MAX_HEALTH);
            livingEntity.getPersistentData().remove(JJA_SUMMON_ACTIVATION_ID);
            return;
        }
        maxHealth.setBaseValue(originalBaseMaxHealth);
        livingEntity.getPersistentData().remove(JJA_SUMMON_BASE_MAX_HEALTH);
        livingEntity.getPersistentData().remove(JJA_SUMMON_ACTIVATION_ID);
        livingEntity.setHealth(resolveRestoredHealth(livingEntity.getHealth(), livingEntity.getMaxHealth()));
    }

    static double resolveDisplayedTechniqueCost(double baseCost, double additionalCost) {
        return baseCost + Math.max(additionalCost, 0.0D);
    }

    public static double resolveDisplayedTechniqueCost(
        ServerPlayer player,
        JujutsucraftModVariables.PlayerVariables playerVariables,
        double baseCost,
        int activeCtId,
        int selectedTechnique,
        String currentTechniqueName
    ) {
        if (player == null || playerVariables == null) {
            return baseCost;
        }
        double displayedBaseCost = TechniquePreviewCostService.resolveDisplayedCost(player, playerVariables, baseCost);
        Preview preview = createPreview(player, displayedBaseCost, activeCtId, selectedTechnique, currentTechniqueName);
        if (preview == null) {
            return displayedBaseCost;
        }
        return resolveDisplayedTechniqueCost(displayedBaseCost, preview.additionalCost());
    }

    static boolean isPreviewBaseCostSupported(double baseCost) {
        return baseCost >= 0.0D;
    }

    static boolean shouldApplyPendingActivation(boolean expired, boolean domainEntity, boolean expectedEntityType, boolean alreadyApplied) {
        return !expired && !domainEntity && expectedEntityType && !alreadyApplied;
    }

    static boolean hasStoredBaseMaxHealth(LivingEntity livingEntity) {
        return livingEntity.getPersistentData().contains(JJA_SUMMON_BASE_MAX_HEALTH, Tag.TAG_DOUBLE);
    }

    static float resolveRestoredHealth(float currentHealth, double restoredMaxHealth) {
        return (float) Math.min(currentHealth, restoredMaxHealth);
    }

    private static void tryApplyPending(UUID ownerId, Entity summon) {
        if (!(summon instanceof LivingEntity livingEntity) || summon.level().isClientSide()) {
            return;
        }
        PendingActivation pending = PENDING_ACTIVATIONS.get(ownerId);
        if (pending == null) {
            return;
        }
        long gameTime = summon.level().getGameTime();
        boolean expired = pending.expiresAtGameTime() < gameTime;
        if (expired) {
            PENDING_ACTIVATIONS.remove(ownerId);
            return;
        }
        boolean domainEntity = summon.getPersistentData().getBoolean("domain_entity");
        boolean expectedEntityType = pending.expectedEntityTypes().contains(summon.getType());
        boolean alreadyApplied = pending.activationId().toString().equals(summon.getPersistentData().getString(JJA_SUMMON_ACTIVATION_ID));
        if (!shouldApplyPendingActivation(expired, domainEntity, expectedEntityType, alreadyApplied)) {
            return;
        }
        if (!applyHpMultiplier(livingEntity, pending.hpMultiplier(), pending.activationId())) {
            return;
        }
        if (pending.remainingCount() <= 1) {
            PENDING_ACTIVATIONS.remove(ownerId);
            return;
        }
        PENDING_ACTIVATIONS.put(
            ownerId,
            new PendingActivation(
                pending.activationId(),
                pending.skillId(),
                pending.expectedEntityTypes(),
                pending.remainingCount() - 1,
                pending.hpMultiplier(),
                pending.expiresAtGameTime()
            )
        );
    }

    public static void clearRuntimeState(Entity entity) {
        if (entity == null) {
            return;
        }
        UUID playerId = entity.getUUID();
        PREVIEWS.remove(playerId);
        PENDING_ACTIVATIONS.remove(playerId);
    }

    private static Preview resolvePreviewForCurrentSelection(ServerPlayer player) {
        Preview preview = PREVIEWS.get(player.getUUID());
        if (preview != null) {
            return preview;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
        if (playerVariables == null) {
            return null;
        }
        preview = createPreview(
            player,
            playerVariables.PlayerSelectCurseTechniqueCostOrgin,
            JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player),
            (int) Math.round(playerVariables.PlayerSelectCurseTechnique),
            playerVariables.PlayerSelectCurseTechniqueName
        );
        if (preview != null) {
            PREVIEWS.put(player.getUUID(), preview);
        }
        return preview;
    }

    private static Preview createPreview(
        ServerPlayer player,
        double baseCost,
        int activeCtId,
        int selectedTechnique,
        String currentTechniqueName
    ) {
        if (MegumiSummonBranchResolver.isUntamedSelection(player, activeCtId, selectedTechnique, currentTechniqueName)) {
            return null;
        }
        PlayerAddonStatsState addonStats = PlayerStateAccess.addonStats(player);
        if (addonStats == null || !addonStats.isShikigamiEnhancementEnabled()) {
            return null;
        }
        if (!isPreviewBaseCostSupported(baseCost) || activeCtId <= 0 || selectedTechnique <= 0) {
            return null;
        }
        ResolvedSummon resolvedSummon = SummonEnhancementCatalog.resolve(player, activeCtId, selectedTechnique, currentTechniqueName);
        if (resolvedSummon == null || resolvedSummon.activationBaseMaxHp() <= 0.0 || resolvedSummon.expectedEntityTypes().isEmpty()) {
            return null;
        }
        int strengthLevel = getDisplayedStrengthLevel(player);
        if (strengthLevel <= 0) {
            return null;
        }
        double ownerMaxHp = player.getMaxHealth();
        if (ownerMaxHp <= 0.0) {
            return null;
        }
        double rawMultiplier = ownerMaxHp * strengthLevel / (5.0 * resolvedSummon.activationBaseMaxHp());
        if (rawMultiplier <= 1.0) {
            return null;
        }
        double hpMultiplier = Math.min(6.0, rawMultiplier);
        int additionalCost = (int) Math.round(
            hpMultiplier * (resolvedSummon.activationBaseMaxHp() / strengthLevel)
        );
        return new Preview(
            resolvedSummon.skillId(),
            resolvedSummon.expectedEntityTypes(),
            resolvedSummon.expectedCount(),
            resolvedSummon.pendingValidityTicks(),
            hpMultiplier,
            additionalCost
        );
    }

    private static boolean applyHpMultiplier(LivingEntity livingEntity, double hpMultiplier, UUID activationId) {
        AttributeInstance maxHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return false;
        }
        double originalBaseMaxHealth = livingEntity.getPersistentData().contains(JJA_SUMMON_BASE_MAX_HEALTH, Tag.TAG_DOUBLE)
            ? livingEntity.getPersistentData().getDouble(JJA_SUMMON_BASE_MAX_HEALTH)
            : maxHealth.getBaseValue();
        if (originalBaseMaxHealth <= 0.0) {
            return false;
        }
        livingEntity.getPersistentData().putDouble(JJA_SUMMON_BASE_MAX_HEALTH, originalBaseMaxHealth);
        maxHealth.setBaseValue(originalBaseMaxHealth * hpMultiplier);
        livingEntity.getPersistentData().putString(JJA_SUMMON_ACTIVATION_ID, activationId.toString());
        livingEntity.setHealth((float) livingEntity.getMaxHealth());
        return true;
    }

    private static UUID parseOwnerId(Entity entity) {
        String rawOwnerId = JjaJujutsucraftDataAccess.jjaGetOwnerUuid(entity);
        if (rawOwnerId == null || rawOwnerId.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(rawOwnerId);
        } catch (IllegalArgumentException exception) {
            LOGGER.warn("Invalid summon owner UUID '{}' on entity {}.", rawOwnerId, entity, exception);
            return null;
        }
    }

    private static int getDisplayedStrengthLevel(LivingEntity livingEntity) {
        if (livingEntity == null || !livingEntity.hasEffect(MobEffects.DAMAGE_BOOST)) {
            return 0;
        }
        return livingEntity.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() + 1;
    }

    private record Preview(
        int skillId,
        Set<EntityType<?>> expectedEntityTypes,
        int expectedCount,
        long pendingValidityTicks,
        double hpMultiplier,
        int additionalCost
    ) {
    }

    private record PendingActivation(
        UUID activationId,
        int skillId,
        Set<EntityType<?>> expectedEntityTypes,
        int remainingCount,
        double hpMultiplier,
        long expiresAtGameTime
    ) {
    }
}
