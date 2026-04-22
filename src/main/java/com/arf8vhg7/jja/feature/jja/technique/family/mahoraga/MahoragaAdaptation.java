package com.arf8vhg7.jja.feature.jja.technique.family.mahoraga;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidHealthAccess;
import com.arf8vhg7.jja.feature.equipment.curios.CuriosEquipmentReadService;
import com.arf8vhg7.jja.feature.player.health.firstaid.FirstAidMutationService;
import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.mcreator.jujutsucraft.entity.CursedSpiritGrade010Entity;
import net.mcreator.jujutsucraft.entity.EightHandledSwordDivergentSilaDivineGeneralMahoragaEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

public final class MahoragaAdaptation {
    public static final String KEY_ACTIVE = "jja_adapt_active";
    public static final String KEY_DAMAGE_POOL = "jja_adapt_damage_pool";

    private static final String KEY_DAMAGE_POOL_LEGACY = "__legacy__";
    private static final String KEY_ADAPTATION_ADVANCED_MESSAGE = "message.jja.mahoraga.adaptation_advanced";
    private static final String KEY_START_MESSAGE = "jja_adapt_start_message";
    private static final String KEY_PENDING_DAMAGE_ENTRIES = "jja_adapt_pending_damage_entries";
    private static final String KEY_PENDING_DAMAGE_KEY = "jja_adapt_pending_damage_key";
    private static final String KEY_PENDING_DAMAGE_COUNT = "jja_adapt_pending_damage_count";
    private static final String KEY_PENDING_DAMAGE_SEQUENCE = "jja_adapt_pending_damage_sequence";
    private static final String KEY_PENDING_DAMAGE_TICK = "jja_adapt_pending_damage_tick";
    private static final String KEY_PENDING_DAMAGE_SOURCE = "jja_adapt_pending_damage_source";
    private static final ResourceLocation GACON_SOUND_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "gacon");
    private static final ResourceLocation CUT_THE_WORLD_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath(
        "jujutsucraft",
        "skill_dismantle_cut_the_world"
    );

    private MahoragaAdaptation() {
    }

    public static double resolveRegistrationProgress(Entity target, String key, double originalValue, Entity damageSource, boolean trackDamage) {
        if (target == null || key == null || key.isEmpty()) {
            return originalValue;
        }
        if (ReviveFlowService.isWaiting(target)) {
            return originalValue;
        }

        ItemStack helmet = getMahoragaHelmet(target);
        if (!isMahoragaHelmet(helmet)) {
            return originalValue;
        }

        setStartMessageFlag(target, false);
        CompoundTag tag = helmet.getOrCreateTag();
        initializeDamagePoolEntry(tag, key);
        if (trackDamage) {
            queuePendingDamage(target, damageSource, key);
        }

        String activeKey = tag.getString(KEY_ACTIVE);
        if (activeKey.isEmpty() || activeKey.equals(key)) {
            tag.putString(KEY_ACTIVE, key);
            setStartMessageFlag(target, true);
            return originalValue;
        }

        return 0.0;
    }

    public static boolean consumeStartMessageFlag(Entity entity) {
        if (entity == null) {
            return false;
        }

        CompoundTag data = entity.getPersistentData();
        boolean show = data.getBoolean(KEY_START_MESSAGE);
        data.remove(KEY_START_MESSAGE);
        return show;
    }

    public static void recordPendingDamage(LivingEntity entity, DamageSource damageSource, double amount) {
        if (entity == null || damageSource == null || amount <= 0.0) {
            return;
        }
        if (ReviveFlowService.isWaiting(entity)) {
            clearPendingDamage(entity.getPersistentData());
            return;
        }

        CompoundTag data = entity.getPersistentData();
        if (!data.contains(KEY_PENDING_DAMAGE_ENTRIES, Tag.TAG_COMPOUND)) {
            return;
        }

        long currentTick = entity.level().getGameTime();
        if (data.getLong(KEY_PENDING_DAMAGE_TICK) != currentTick) {
            clearPendingDamage(data);
            return;
        }

        String pendingEntryId = findPendingDamageEntryId(data, damageSource);
        if (pendingEntryId.isEmpty()) {
            return;
        }

        ItemStack helmet = getMahoragaHelmet(entity);
        if (!isMahoragaHelmet(helmet)) {
            removePendingDamageEntry(data, pendingEntryId);
            return;
        }

        CompoundTag tag = helmet.getOrCreateTag();
        if (!hasIncompleteAdaptation(tag)) {
            removePendingDamageEntry(data, pendingEntryId);
            return;
        }

        CompoundTag pendingEntry = data.getCompound(KEY_PENDING_DAMAGE_ENTRIES).getCompound(pendingEntryId);
        String key = pendingEntry.getString(KEY_PENDING_DAMAGE_KEY);
        if (!key.isEmpty()) {
            addDamageToPool(tag, key, amount);
        }
        removePendingDamageEntry(data, pendingEntryId);
    }

    public static void processEntityTick(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        if (ReviveFlowService.isWaiting(entity)) {
            return;
        }

        ItemStack helmet = getMahoragaHelmet(entity);
        if (!isMahoragaHelmet(helmet)) {
            return;
        }

        CompoundTag tag = helmet.getOrCreateTag();
        tag.putDouble("cnt_adaptation", tag.getDouble("cnt_adaptation") + 1.0);

        boolean mahoraga = isMahoragaUser(entity);
        boolean adaptationStartTick = false;
        boolean stageAdvanced = false;

        if (tag.getDouble("cnt_adaptation") >= 6.0) {
            tag.putDouble("cnt_adaptation", 0.0);
            adaptationStartTick = true;
        }

        String activeKey = tag.getString(KEY_ACTIVE);
        double activeProgress = activeKey.isEmpty() ? 0.0 : tag.getDouble(activeKey);
        boolean periodicHealEvaluationTick = false;
        StageAdvanceMessageCounts stageMessageCounts = new StageAdvanceMessageCounts();
        if (!activeKey.isEmpty() && activeProgress > 0.0 && activeProgress < 1000.0) {
            boolean slowAdaptation = isSlowAdaptation(activeKey);
            double oldProgress = activeProgress;
            double step = !slowAdaptation && !activeKey.contains("domain") ? 20.0 : 4.0;
            if (activeProgress >= 100.0) {
                step = Math.round(step * 0.5);
            }

            double updatedProgress = Math.round(activeProgress + step);
            tag.putDouble(activeKey, updatedProgress);
            if (oldProgress < 100.0 && updatedProgress >= 100.0) {
                if (adaptationStartTick) {
                    stageAdvanced = true;
                    stageMessageCounts.progressCount++;
                } else {
                    tag.putDouble(activeKey, 99.0);
                }
            } else if (updatedProgress >= 1000.0) {
                if (adaptationStartTick) {
                    tag.putDouble(activeKey, 1000.0);
                    stageAdvanced = true;
                    stageMessageCounts.completionCount++;
                } else {
                    tag.putDouble(activeKey, 999.0);
                }
            } else if (Math.floor(updatedProgress / 100.0) > Math.floor(oldProgress / 100.0)) {
                if (adaptationStartTick) {
                    stageAdvanced = true;
                    stageMessageCounts.progressCount++;
                } else {
                    tag.putDouble(activeKey, oldProgress);
                }
            }

            periodicHealEvaluationTick = stageAdvanced
                || (slowAdaptation && shouldEvaluateSlowPeriodicHeal(tag.getDouble(activeKey), adaptationStartTick));
        }

        boolean periodicHealTriggered = false;
        double healAmount = 0.0;
        if (!periodicHealEvaluationTick && !hasIncompleteAdaptation(tag) && adaptationStartTick) {
            periodicHealEvaluationTick = true;
        }
        if (mahoraga && periodicHealEvaluationTick) {
            if (!FirstAidHealthAccess.isEffectivelyAtFullHealth(livingEntity)) {
                tag.putDouble("cnt_heal", tag.getDouble("cnt_heal") + 1.0);
                if (tag.getDouble("cnt_heal") >= 2.0) {
                    tag.putDouble("cnt_heal", 0.0);
                    healAmount += livingEntity.getMaxHealth() * (entity instanceof Player ? 0.2 : 0.1);
                    periodicHealTriggered = true;
                }
            } else {
                tag.putDouble("cnt_heal", 0.0);
            }
        }

        if (stageAdvanced) {
            if ("toLiving".equals(activeKey)) {
                MahoragaPehkuiScaleEvents.applyStageScale(entity, tag);
            }
            stageMessageCounts.add(advanceMatchingStageGroup(tag, activeKey));
            for (int i = 0; i < stageMessageCounts.completionCount; i++) {
                displayStageProgressMessage(entity, true);
            }
            for (int i = 0; i < stageMessageCounts.progressCount; i++) {
                displayStageProgressMessage(entity, false);
            }
            for (int i = 0; i < stageMessageCounts.startCount; i++) {
                displayAdaptationStartMessage(entity);
            }
            healAmount += getTotalDamagePoolAmount(tag);
            clearDamagePool(tag);
        }
        if (healAmount > 0.0) {
            if (!FirstAidMutationService.applyDistributedHeal(livingEntity, healAmount)) {
                livingEntity.setHealth((float) Math.min(livingEntity.getHealth() + healAmount, livingEntity.getMaxHealth()));
            }
            if (periodicHealTriggered) {
                livingEntity.removeEffect(MobEffects.REGENERATION);
            }
        }
        if (stageAdvanced || periodicHealTriggered) {
            playAdaptationSound(world, x, y, z, entity);
        }
        if (stageAdvanced) {
            awardCutTheWorldAdvancement(entity, tag);
        }

        if (stageAdvanced || activeKey.isEmpty() || activeProgress <= 0.0 || activeProgress >= 1000.0) {
            promoteNextActive(entity, tag);
        }
    }

    public static void clearHelmet(ItemStack stack) {
        if (!isResettableWheelHelmet(stack)) {
            return;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return;
        }

        List<String> keysToRemove = new ArrayList<>();
        for (String key : tag.getAllKeys()) {
            if (isAdaptationTagKey(key)) {
                keysToRemove.add(key);
            }
        }
        for (String key : keysToRemove) {
            tag.remove(key);
        }
    }

    public static void clearJjaWaitingTransientState(Entity entity) {
        if (entity == null) {
            return;
        }
        CompoundTag data = entity.getPersistentData();
        data.remove(KEY_START_MESSAGE);
        clearPendingDamage(data);
    }

    public static boolean isResettableWheelHelmet(ItemStack stack) {
        return isMahoragaHelmet(stack);
    }

    public static boolean isMahoragaHelmet(ItemStack stack) {
        return !stack.isEmpty()
            && (stack.getItem() == JujutsucraftModItems.MAHORAGA_WHEEL_HELMET.get()
                || stack.getItem() == JujutsucraftModItems.MAHORAGA_BODY_HELMET.get());
    }

    private static ItemStack getMahoragaHelmet(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return ItemStack.EMPTY;
        }
        return CuriosEquipmentReadService.resolveEquipmentRead(
            livingEntity,
            EquipmentSlot.HEAD,
            livingEntity.getItemBySlot(EquipmentSlot.HEAD)
        );
    }

    private static void setStartMessageFlag(Entity entity, boolean value) {
        CompoundTag data = entity.getPersistentData();
        if (value) {
            data.putBoolean(KEY_START_MESSAGE, true);
        } else {
            data.remove(KEY_START_MESSAGE);
        }
    }

    private static void queuePendingDamage(Entity entity, Entity source, String key) {
        CompoundTag data = entity.getPersistentData();
        long currentTick = entity.level().getGameTime();
        if (data.getLong(KEY_PENDING_DAMAGE_TICK) != currentTick) {
            clearPendingDamage(data);
        }

        CompoundTag pendingEntries = data.contains(KEY_PENDING_DAMAGE_ENTRIES, Tag.TAG_COMPOUND)
            ? data.getCompound(KEY_PENDING_DAMAGE_ENTRIES)
            : new CompoundTag();
        long sequence = data.getLong(KEY_PENDING_DAMAGE_SEQUENCE);
        CompoundTag pendingEntry = new CompoundTag();
        pendingEntry.putString(KEY_PENDING_DAMAGE_KEY, key);
        if (source != null) {
            pendingEntry.putString(KEY_PENDING_DAMAGE_SOURCE, source.getStringUUID());
        }

        data.putLong(KEY_PENDING_DAMAGE_TICK, currentTick);
        data.putLong(KEY_PENDING_DAMAGE_SEQUENCE, sequence + 1L);
        pendingEntries.put(String.valueOf(sequence), pendingEntry);
        data.put(KEY_PENDING_DAMAGE_ENTRIES, pendingEntries);
    }

    private static void clearPendingDamage(CompoundTag data) {
        data.remove(KEY_PENDING_DAMAGE_ENTRIES);
        data.remove(KEY_PENDING_DAMAGE_COUNT);
        data.remove(KEY_PENDING_DAMAGE_SEQUENCE);
        data.remove(KEY_PENDING_DAMAGE_TICK);
        data.remove(KEY_PENDING_DAMAGE_SOURCE);
    }

    private static String findPendingDamageEntryId(CompoundTag data, DamageSource damageSource) {
        CompoundTag pendingEntries = data.getCompound(KEY_PENDING_DAMAGE_ENTRIES);
        String matchedId = "";
        long matchedSequence = Long.MAX_VALUE;
        String fallbackId = "";
        long fallbackSequence = Long.MAX_VALUE;
        for (String entryId : pendingEntries.getAllKeys()) {
            CompoundTag pendingEntry = pendingEntries.getCompound(entryId);
            String expectedUuid = pendingEntry.getString(KEY_PENDING_DAMAGE_SOURCE);
            long sequence = parsePendingDamageSequence(entryId);
            if (expectedUuid.isEmpty()) {
                if (sequence < fallbackSequence) {
                    fallbackId = entryId;
                    fallbackSequence = sequence;
                }
                continue;
            }
            if (!matchesDamageSource(damageSource, expectedUuid)) {
                continue;
            }

            if (sequence < matchedSequence) {
                matchedId = entryId;
                matchedSequence = sequence;
            }
        }
        return matchedId.isEmpty() ? fallbackId : matchedId;
    }

    private static long parsePendingDamageSequence(String entryId) {
        try {
            return Long.parseLong(entryId);
        } catch (NumberFormatException exception) {
            return Long.MAX_VALUE;
        }
    }

    private static void removePendingDamageEntry(CompoundTag data, String entryId) {
        if (entryId.isEmpty() || !data.contains(KEY_PENDING_DAMAGE_ENTRIES, Tag.TAG_COMPOUND)) {
            return;
        }

        CompoundTag pendingEntries = data.getCompound(KEY_PENDING_DAMAGE_ENTRIES);
        pendingEntries.remove(entryId);
        if (pendingEntries.getAllKeys().isEmpty()) {
            clearPendingDamage(data);
            return;
        }

        data.put(KEY_PENDING_DAMAGE_ENTRIES, pendingEntries);
    }

    private static boolean matchesDamageSource(DamageSource damageSource, String expectedUuid) {
        Entity directEntity = damageSource.getDirectEntity();
        if (directEntity != null && expectedUuid.equals(directEntity.getStringUUID())) {
            return true;
        }

        Entity sourceEntity = damageSource.getEntity();
        return sourceEntity != null && expectedUuid.equals(sourceEntity.getStringUUID());
    }

    private static boolean hasIncompleteAdaptation(CompoundTag tag) {
        for (String key : getStoredAdaptationKeys(tag)) {
            if (tag.getDouble(key) < 1000.0) {
                return true;
            }
        }
        return false;
    }

    private static CompoundTag getDamagePoolTag(CompoundTag tag) {
        if (tag.contains(KEY_DAMAGE_POOL, Tag.TAG_COMPOUND)) {
            return tag.getCompound(KEY_DAMAGE_POOL);
        }

        CompoundTag damagePool = new CompoundTag();
        if (tag.contains(KEY_DAMAGE_POOL, Tag.TAG_DOUBLE)) {
            double legacyValue = tag.getDouble(KEY_DAMAGE_POOL);
            if (legacyValue > 0.0) {
                damagePool.putDouble(KEY_DAMAGE_POOL_LEGACY, legacyValue);
            }
        }

        tag.remove(KEY_DAMAGE_POOL);
        tag.put(KEY_DAMAGE_POOL, damagePool);
        return damagePool;
    }

    private static void initializeDamagePoolEntry(CompoundTag tag, String key) {
        if (key == null || key.isEmpty()) {
            return;
        }

        CompoundTag damagePool = getDamagePoolTag(tag);
        if (!damagePool.contains(key, Tag.TAG_DOUBLE)) {
            damagePool.putDouble(key, 0.0);
            tag.put(KEY_DAMAGE_POOL, damagePool);
        }
    }

    private static void addDamageToPool(CompoundTag tag, String key, double amount) {
        if (key == null || key.isEmpty() || amount <= 0.0) {
            return;
        }

        CompoundTag damagePool = getDamagePoolTag(tag);
        damagePool.putDouble(key, damagePool.getDouble(key) + amount);
        tag.put(KEY_DAMAGE_POOL, damagePool);
    }

    private static double getTotalDamagePoolAmount(CompoundTag tag) {
        double total = 0.0;
        CompoundTag damagePool = getDamagePoolTag(tag);
        for (String key : damagePool.getAllKeys()) {
            total += damagePool.getDouble(key);
        }
        return total;
    }

    private static void clearDamagePool(CompoundTag tag) {
        tag.remove(KEY_DAMAGE_POOL);
    }

    static StageAdvanceMessageCounts advanceMatchingStageGroup(CompoundTag tag, String activeKey) {
        String groupKey = getAdaptationGroupKey(activeKey);
        if (groupKey.isEmpty()) {
            return new StageAdvanceMessageCounts();
        }

        StageAdvanceMessageCounts counts = new StageAdvanceMessageCounts();
        for (String key : getStoredAdaptationKeys(tag)) {
            if (activeKey.equals(key) || !groupKey.equals(getAdaptationGroupKey(key))) {
                continue;
            }
            counts.add(advanceStage(tag, key));
        }
        return counts;
    }

    private static StageAdvanceMessageCounts advanceStage(CompoundTag tag, String key) {
        StageAdvanceMessageCounts counts = new StageAdvanceMessageCounts();
        if (key == null || key.isEmpty()) {
            return counts;
        }

        double progress = tag.getDouble(key);
        if (progress >= 1000.0) {
            return counts;
        }
        if (progress <= 0.0) {
            tag.putDouble(key, 1.0);
            counts.startCount++;
            return counts;
        }

        double nextStage = Math.min((Math.floor(Math.max(progress, 0.0) / 100.0) + 1.0) * 100.0, 1000.0);
        if (nextStage > progress) {
            tag.putDouble(key, nextStage);
            if (nextStage >= 1000.0) {
                counts.completionCount++;
            } else {
                counts.progressCount++;
            }
        }
        return counts;
    }

    private static String getAdaptationGroupKey(String key) {
        if (key == null || key.isEmpty() || "toLiving".equals(key)) {
            return "";
        }
        if (hasNumericSuffix(key, "skill")) {
            return getSkillGroupKey(key.substring("skill".length()));
        }
        if (hasNumericSuffix(key, "domain")) {
            return getDomainGroupKey(key.substring("domain".length()));
        }
        return "";
    }

    private static String getSkillGroupKey(String numericSuffix) {
        try {
            String padded = String.format(Locale.ROOT, "%04d", Integer.parseInt(numericSuffix));
            return padded.substring(0, 2);
        } catch (NumberFormatException exception) {
            return "";
        }
    }

    private static String getDomainGroupKey(String numericSuffix) {
        try {
            return String.format(Locale.ROOT, "%02d", Integer.parseInt(numericSuffix));
        } catch (NumberFormatException exception) {
            return "";
        }
    }

    public static boolean isMahoragaUser(Entity entity) {
        if (entity instanceof Player player) {
            JujutsucraftModVariables.PlayerVariables playerVars = JjaJujutsucraftCompat.jjaGetPlayerVariablesOrDefault(player);
            return playerVars.PlayerCurseTechnique == 16.0 || playerVars.PlayerCurseTechnique2 == 16.0;
        }

        return entity instanceof EightHandledSwordDivergentSilaDivineGeneralMahoragaEntity || entity instanceof CursedSpiritGrade010Entity;
    }

    private static boolean isSlowAdaptation(String key) {
        if (!key.startsWith("skill")) {
            return false;
        }

        try {
            int value = Integer.parseInt(key.substring("skill".length()));
            return value >= 205 && value <= 220;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private static boolean shouldEvaluateSlowPeriodicHeal(double progress, boolean adaptationStartTick) {
        if (!adaptationStartTick) {
            return false;
        }

        int roundedProgress = (int) Math.round(progress);
        if (roundedProgress <= 0 || roundedProgress >= 1000) {
            return false;
        }
        if (roundedProgress < 100) {
            return true;
        }

        int stageFloor = (roundedProgress / 100) * 100;
        int anchor = (roundedProgress & 1) == 0 ? stageFloor + 24 : (stageFloor == 100 ? 103 : stageFloor + 1);
        return roundedProgress >= anchor && (roundedProgress - anchor) % 24 == 0;
    }

    private static void playAdaptationSound(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity instanceof CursedSpiritGrade010Entity || !(world instanceof Level level)) {
            return;
        }

        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(GACON_SOUND_ID);
        if (soundEvent == null) {
            return;
        }

        if (!level.isClientSide()) {
            level.playSound(null, BlockPos.containing(x, y, z), soundEvent, SoundSource.NEUTRAL, 0.5F, 1.0F);
            return;
        }

        level.playLocalSound(x, y, z, soundEvent, SoundSource.NEUTRAL, 0.5F, 1.0F, false);
    }

    private static void awardCutTheWorldAdvancement(Entity entity, CompoundTag tag) {
        if (!(entity instanceof ServerPlayer player) || tag.getDouble("skill205") < 1000.0 || player.server == null) {
            return;
        }

        Advancement advancement = player.server.getAdvancements().getAdvancement(CUT_THE_WORLD_ADVANCEMENT_ID);
        if (advancement == null) {
            return;
        }

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) {
            return;
        }

        List<String> remainingCriteria = new ArrayList<>();
        for (String criterion : progress.getRemainingCriteria()) {
            remainingCriteria.add(criterion);
        }
        for (String criterion : remainingCriteria) {
            player.getAdvancements().award(advancement, criterion);
        }
    }

    private static void displayStageProgressMessage(Entity entity, boolean adaptationComplete) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(buildStageProgressMessage(adaptationComplete), false);
        }
    }

    private static void displayAdaptationStartMessage(Entity entity) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(buildAdaptationStartMessage(), false);
        }
    }

    private static void promoteNextActive(Entity entity, CompoundTag tag) {
        String nextKey = selectNextActiveKey(tag);
        if (nextKey.isEmpty()) {
            tag.remove(KEY_ACTIVE);
            return;
        }

        tag.putString(KEY_ACTIVE, nextKey);
        if (tag.getDouble(nextKey) <= 0.0) {
            tag.putDouble(nextKey, 1.0);
            if (entity instanceof Player player && !player.level().isClientSide()) {
                player.displayClientMessage(buildAdaptationStartMessage(), false);
            }
        }
    }

    static MutableComponent buildStageProgressMessage(boolean adaptationComplete) {
        return Component.translatable(adaptationComplete ? "jujutsu.message.adaptation_complete" : KEY_ADAPTATION_ADVANCED_MESSAGE);
    }

    static MutableComponent buildAdaptationStartMessage() {
        return Component.translatable("jujutsu.message.adaptation_start");
    }

    private static String selectNextActiveKey(CompoundTag tag) {
        List<String> stageZeroCandidates = new ArrayList<>();
        List<String> incompleteCandidates = new ArrayList<>();
        for (String key : getStoredAdaptationKeys(tag)) {
            double progress = tag.getDouble(key);
            if (progress >= 1000.0) {
                continue;
            }

            incompleteCandidates.add(key);
            if ((int) Math.floor(progress / 100.0) == 0) {
                stageZeroCandidates.add(key);
            }
        }

        if (!stageZeroCandidates.isEmpty()) {
            return selectHighestDamageCandidate(tag, stageZeroCandidates);
        }

        return selectHighestDamageCandidate(tag, incompleteCandidates);
    }

    private static String selectHighestDamageCandidate(CompoundTag tag, List<String> candidates) {
        if (candidates.isEmpty()) {
            return "";
        }

        CompoundTag damagePool = getDamagePoolTag(tag);
        double maxDamage = Double.NEGATIVE_INFINITY;
        List<String> maxDamageCandidates = new ArrayList<>();
        for (String key : candidates) {
            double damage = damagePool.getDouble(key);
            if (damage > maxDamage) {
                maxDamage = damage;
                maxDamageCandidates.clear();
                maxDamageCandidates.add(key);
            } else if (Double.compare(damage, maxDamage) == 0) {
                maxDamageCandidates.add(key);
            }
        }

        List<String> selectionPool = maxDamage > 0.0 ? maxDamageCandidates : candidates;
        return selectionPool.get(ThreadLocalRandom.current().nextInt(selectionPool.size()));
    }

    private static List<String> getStoredAdaptationKeys(CompoundTag tag) {
        Set<String> orderedKeys = new LinkedHashSet<>();
        for (int i = 1; i <= 800; i++) {
            String key = tag.getString("DATA" + i);
            if (!key.isEmpty()) {
                orderedKeys.add(key);
            }
        }
        return new ArrayList<>(orderedKeys);
    }

    private static boolean isAdaptationTagKey(String key) {
        return isDataKey(key)
            || "cnt_adaptation".equals(key)
            || "cnt_heal".equals(key)
            || KEY_ACTIVE.equals(key)
            || KEY_DAMAGE_POOL.equals(key)
            || "toLiving".equals(key)
            || hasNumericSuffix(key, "skill")
            || hasNumericSuffix(key, "domain");
    }

    private static boolean isDataKey(String key) {
        return hasNumericSuffix(key, "DATA");
    }

    private static boolean hasNumericSuffix(String key, String prefix) {
        if (key == null || prefix == null || !key.startsWith(prefix) || key.length() == prefix.length()) {
            return false;
        }
        for (int i = prefix.length(); i < key.length(); i++) {
            if (!Character.isDigit(key.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    static final class StageAdvanceMessageCounts {
        private int startCount;
        private int progressCount;
        private int completionCount;

        private void add(StageAdvanceMessageCounts other) {
            if (other == null) {
                return;
            }
            startCount += other.startCount;
            progressCount += other.progressCount;
            completionCount += other.completionCount;
        }

        int startCount() {
            return startCount;
        }

        int progressCount() {
            return progressCount;
        }

        int completionCount() {
            return completionCount;
        }
    }
}
