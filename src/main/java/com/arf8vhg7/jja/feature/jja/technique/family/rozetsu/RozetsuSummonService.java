package com.arf8vhg7.jja.feature.jja.technique.family.rozetsu;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.entity.RozetsuShikigamiEntity;
import net.mcreator.jujutsucraft.entity.RozetsuShikigamiVessel2Entity;
import net.mcreator.jujutsucraft.entity.RozetsuShikigamiVesselEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModEntities;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.KeyChangeTechniqueOnKeyPressedProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class RozetsuSummonService {
    static final String KEY_SUMMONED_GAME_TIME = "jjaRozetsuSummonedGameTime";
    private static final String KEY_FRIEND_NUM = "friend_num";
    private static final String KEY_FRIEND_NUM_WORKER = "friend_num_worker";
    private static final String KEY_OWNER_UUID = "OWNER_UUID";
    private static final String KEY_SHIKIGAMI = "Shikigami";
    private static final String KEY_PLAYER = "Player";
    private static final String KEY_JUJUTSU_SORCERER = "JujutsuSorcerer";
    private static final String KEY_CURSE_USER = "CurseUser";
    private static final String KEY_CURSED_SPIRIT = "CursedSpirit";
    private static final String KEY_BASE_CURSE_POWER = "BaseCursePower";
    private static final String KEY_DOMAIN_ENTITY = "domain_entity";
    private static final String KEY_SKILL = "skill";
    private static final String KEY_CNT1 = "cnt1";
    private static final String KEY_SHIKIGAMI_ROZETSU_UUID = "SHIKIGAMI_ROZETSU_UUID";

    private static final int NORMAL_REQUESTED_COUNT = 3;
    private static final double HEALTH_MULTIPLIER = 0.5D;

    private RozetsuSummonService() {
    }

    public static void handleMixedSummon(LevelAccessor world, double x, double y, double z, Entity owner) {
        if (owner == null) {
            return;
        }
        RozetsuSummonRules.SummonKind kind = resolveMixedKind(owner);
        int requestedCount = kind == RozetsuSummonRules.SummonKind.NORMAL ? NORMAL_REQUESTED_COUNT : 1;
        handleSummon(world, x, y, z, owner, kind, requestedCount, BaseCursePowerMode.NONE, false);
    }

    public static void handleNormalSummon(LevelAccessor world, double x, double y, double z, Entity owner) {
        handleSummon(world, x, y, z, owner, RozetsuSummonRules.SummonKind.NORMAL, NORMAL_REQUESTED_COUNT, BaseCursePowerMode.NORMAL_DIRECT, false);
    }

    public static void handleVesselSummon(LevelAccessor world, double x, double y, double z, Entity owner) {
        handleSummon(world, x, y, z, owner, RozetsuSummonRules.SummonKind.VESSEL, 1, BaseCursePowerMode.FULL, false);
    }

    public static void handleVessel2Summon(LevelAccessor world, double x, double y, double z, Entity owner) {
        handleSummon(world, x, y, z, owner, RozetsuSummonRules.SummonKind.VESSEL_2, 1, BaseCursePowerMode.FULL, true);
    }

    public static int countActivePoints(ServerLevel level, Entity owner) {
        return collectActiveSummons(level, owner).stream().mapToInt(candidate -> candidate.kind().cost()).sum();
    }

    public static int resolveMaxPointsForOwner(Entity owner) {
        return RozetsuSummonRules.resolveMaxPoints(getStrengthAmplifier(owner));
    }

    private static RozetsuSummonRules.SummonKind resolveMixedKind(Entity owner) {
        if (owner instanceof Player) {
            return owner.isShiftKeyDown() ? RozetsuSummonRules.SummonKind.VESSEL : RozetsuSummonRules.SummonKind.NORMAL;
        }
        if (Math.random() < 0.5D) {
            return RozetsuSummonRules.SummonKind.NORMAL;
        }
        return Math.random() < 0.5D ? RozetsuSummonRules.SummonKind.VESSEL : RozetsuSummonRules.SummonKind.VESSEL_2;
    }

    private static void handleSummon(
        LevelAccessor world,
        double x,
        double y,
        double z,
        @Nullable Entity owner,
        RozetsuSummonRules.SummonKind kind,
        int requestedCount,
        BaseCursePowerMode baseCursePowerMode,
        boolean changeTechniqueAfterSummon
    ) {
        if (owner == null) {
            return;
        }

        owner.getPersistentData().putDouble(KEY_CNT1, owner.getPersistentData().getDouble(KEY_CNT1) + 1.0D);
        if (owner instanceof LivingEntity livingOwner) {
            livingOwner.swing(InteractionHand.MAIN_HAND, true);
        }
        ensureFriendNum(owner);

        if (!(world instanceof ServerLevel serverLevel)) {
            owner.getPersistentData().putDouble(KEY_SKILL, 0.0D);
            return;
        }

        float originalYaw = owner.getYRot();
        float originalPitch = owner.getXRot();
        List<ActiveSummonCandidate> activeSummons = collectActiveSummons(serverLevel, owner);
        int currentPoints = activeSummons.stream().mapToInt(candidate -> candidate.kind().cost()).sum();
        int maxPoints = resolveMaxPointsForOwner(owner);
        int incomingUnitCost = kind.cost();
        for (RozetsuSummonRules.SummonOrder removal : RozetsuSummonRules.selectOldestRemovalsForCapacity(
            activeSummons.stream().map(ActiveSummonCandidate::order).toList(),
            currentPoints,
            maxPoints,
            incomingUnitCost
        )) {
            ActiveSummonCandidate candidate = findCandidate(activeSummons, removal);
            if (candidate != null) {
                candidate.entity().kill();
                currentPoints -= candidate.kind().cost();
            }
        }

        int spawnCount = kind == RozetsuSummonRules.SummonKind.NORMAL
            ? RozetsuSummonRules.resolveNormalSpawnCount(currentPoints, maxPoints, requestedCount)
            : (RozetsuSummonRules.canSpawnVessel(currentPoints, maxPoints) ? 1 : 0);

        for (int index = 0; index < spawnCount; index++) {
            Vec3 spawnPosition = resolveSpawnPosition(owner, originalYaw, originalPitch, kind == RozetsuSummonRules.SummonKind.NORMAL);
            LivingEntity summon = spawn(serverLevel, spawnPosition, kind);
            if (summon == null) {
                continue;
            }
            initializeSpawnPose(owner, summon);
            initializeSummonMetadata(owner, summon, kind);
            applySummonMaxHealth(owner, summon);
            applyOwnerResistance(owner, summon);
            applyBaseCursePower(owner, summon, baseCursePowerMode);
            serverLevel.addFreshEntity(summon);
            if (kind == RozetsuSummonRules.SummonKind.VESSEL_2) {
                owner.getPersistentData().putString(KEY_SHIKIGAMI_ROZETSU_UUID, summon.getStringUUID());
            }
            spawnParticles(serverLevel, spawnPosition);
        }

        playSummonSound(world, x, y, z);
        restoreOwnerPose(owner, originalYaw, originalPitch);
        owner.getPersistentData().putDouble(KEY_SKILL, 0.0D);
        if (changeTechniqueAfterSummon) {
            markNoChangeTechnique(owner);
            KeyChangeTechniqueOnKeyPressedProcedure.execute(world, x, y, z, owner);
        }
    }

    @Nullable
    private static ActiveSummonCandidate findCandidate(List<ActiveSummonCandidate> candidates, RozetsuSummonRules.SummonOrder order) {
        for (ActiveSummonCandidate candidate : candidates) {
            if (candidate.summonedGameTime() == order.summonedGameTime() && candidate.entity().getId() == order.entityId()) {
                return candidate;
            }
        }
        return null;
    }

    private static List<ActiveSummonCandidate> collectActiveSummons(ServerLevel level, Entity owner) {
        String ownerUuid = owner.getStringUUID();
        double friendNum = owner.getPersistentData().getDouble(KEY_FRIEND_NUM);
        List<ActiveSummonCandidate> activeSummons = new ArrayList<>();
        for (ServerLevel serverLevel : level.getServer().getAllLevels()) {
            for (Entity candidate : serverLevel.getAllEntities()) {
                RozetsuSummonRules.SummonKind kind = resolveKind(candidate);
                if (kind == null
                    || !candidate.isAlive()
                    || candidate.isRemoved()
                    || candidate.getPersistentData().getBoolean(KEY_DOMAIN_ENTITY)
                    || !ownerUuid.equals(candidate.getPersistentData().getString(KEY_OWNER_UUID))
                    || Double.compare(friendNum, candidate.getPersistentData().getDouble(KEY_FRIEND_NUM_WORKER)) != 0) {
                    continue;
                }
                activeSummons.add(new ActiveSummonCandidate(candidate, kind, candidate.getPersistentData().getLong(KEY_SUMMONED_GAME_TIME)));
            }
        }
        return activeSummons;
    }

    @Nullable
    private static RozetsuSummonRules.SummonKind resolveKind(Entity entity) {
        if (entity instanceof RozetsuShikigamiEntity) {
            return RozetsuSummonRules.SummonKind.NORMAL;
        }
        if (entity instanceof RozetsuShikigamiVesselEntity) {
            return RozetsuSummonRules.SummonKind.VESSEL;
        }
        if (entity instanceof RozetsuShikigamiVessel2Entity) {
            return RozetsuSummonRules.SummonKind.VESSEL_2;
        }
        return null;
    }

    private static void ensureFriendNum(Entity owner) {
        if (owner.getPersistentData().getDouble(KEY_FRIEND_NUM) == 0.0D) {
            owner.getPersistentData().putDouble(KEY_FRIEND_NUM, Math.random());
        }
    }

    private static Vec3 resolveSpawnPosition(Entity owner, float originalYaw, float originalPitch, boolean randomizeYaw) {
        if (randomizeYaw) {
            owner.setYRot((float) (originalYaw + (Math.random() - 0.5D) * 40.0D));
            owner.setXRot(0.0F);
            owner.setYBodyRot(owner.getYRot());
            owner.setYHeadRot(owner.getYRot());
        }

        double distance = 1.0D + owner.getBbWidth();
        double x = owner.getX() + Math.cos(Math.toRadians(owner.getYRot() + 90.0F)) * Math.cos(Math.toRadians(owner.getXRot())) * distance;
        double y = owner.getY() + owner.getBbHeight() * 0.75D + Math.sin(originalPitch) * -1.0D * distance;
        double z = owner.getZ() + Math.sin(Math.toRadians(owner.getYRot() + 90.0F)) * Math.cos(Math.toRadians(owner.getXRot())) * distance;
        return new Vec3(x, y, z);
    }

    @Nullable
    private static LivingEntity spawn(ServerLevel level, Vec3 position, RozetsuSummonRules.SummonKind kind) {
        BlockPos blockPos = BlockPos.containing(position);
        return switch (kind) {
            case NORMAL -> JujutsucraftModEntities.ROZETSU_SHIKIGAMI.get()
                .create(level, null, null, blockPos, MobSpawnType.MOB_SUMMONED, false, false);
            case VESSEL -> JujutsucraftModEntities.ROZETSU_SHIKIGAMI_VESSEL.get()
                .create(level, null, null, blockPos, MobSpawnType.MOB_SUMMONED, false, false);
            case VESSEL_2 -> JujutsucraftModEntities.ROZETSU_SHIKIGAMI_VESSEL_2.get()
                .create(level, null, null, blockPos, MobSpawnType.MOB_SUMMONED, false, false);
        };
    }

    private static void initializeSpawnPose(Entity owner, LivingEntity summon) {
        summon.moveTo(summon.getX(), summon.getY(), summon.getZ(), owner.getYRot(), owner.getXRot());
        summon.setYBodyRot(owner.getYRot());
        summon.setYHeadRot(owner.getYRot());
    }

    private static void initializeSummonMetadata(Entity owner, LivingEntity summon, RozetsuSummonRules.SummonKind kind) {
        summon.getPersistentData().putString(KEY_OWNER_UUID, owner.getStringUUID());
        summon.getPersistentData().putDouble(KEY_FRIEND_NUM, owner.getPersistentData().getDouble(KEY_FRIEND_NUM));
        summon.getPersistentData().putDouble(KEY_FRIEND_NUM_WORKER, owner.getPersistentData().getDouble(KEY_FRIEND_NUM));
        summon.getPersistentData().putBoolean(KEY_SHIKIGAMI, true);
        summon.getPersistentData().putBoolean(KEY_PLAYER, owner instanceof Player || owner.getPersistentData().getBoolean(KEY_PLAYER));
        summon.getPersistentData().putBoolean(KEY_JUJUTSU_SORCERER, owner.getPersistentData().getBoolean(KEY_JUJUTSU_SORCERER));
        summon.getPersistentData().putBoolean(KEY_CURSE_USER, owner.getPersistentData().getBoolean(KEY_CURSE_USER));
        summon.getPersistentData().putBoolean(KEY_CURSED_SPIRIT, true);
        summon.getPersistentData().putLong(KEY_SUMMONED_GAME_TIME, summon.level().getGameTime());
    }

    private static void applySummonMaxHealth(Entity owner, LivingEntity summon) {
        AttributeInstance summonMaxHealth = summon.getAttribute(Attributes.MAX_HEALTH);
        if (summonMaxHealth == null) {
            return;
        }
        double ownerMaxHealth = 0.0D;
        AttributeInstance ownerMaxHealthAttribute = owner instanceof LivingEntity livingOwner ? livingOwner.getAttribute(Attributes.MAX_HEALTH) : null;
        if (ownerMaxHealthAttribute != null) {
            ownerMaxHealth = ownerMaxHealthAttribute.getBaseValue();
        }
        double boostedMaxHealth = summonMaxHealth.getBaseValue() + (ownerMaxHealth + getStrengthAmplifier(owner) * 3.0D) * HEALTH_MULTIPLIER;
        summonMaxHealth.setBaseValue(boostedMaxHealth);
        summon.setHealth((float) summon.getMaxHealth());
    }

    private static void applyOwnerResistance(Entity owner, LivingEntity summon) {
        int resistanceAmplifier = owner instanceof LivingEntity livingOwner && livingOwner.hasEffect(MobEffects.DAMAGE_RESISTANCE)
            ? Math.max(Objects.requireNonNull(livingOwner.getEffect(MobEffects.DAMAGE_RESISTANCE)).getAmplifier(), 0)
            : 0;
        summon.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, resistanceAmplifier, false, false));
    }

    private static void applyBaseCursePower(Entity owner, LivingEntity summon, BaseCursePowerMode mode) {
        if (mode == BaseCursePowerMode.NONE || !(owner instanceof Player)) {
            return;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariablesOrDefault(owner);
        double multiplier = mode == BaseCursePowerMode.NORMAL_DIRECT ? 0.33D : 1.0D;
        double value = playerVariables.PlayerSelectCurseTechniqueCost * multiplier;
        summon.getPersistentData().putDouble(KEY_BASE_CURSE_POWER, mode == BaseCursePowerMode.NORMAL_DIRECT ? Math.floor(value) : value);
    }

    private static void spawnParticles(ServerLevel level, Vec3 position) {
        level.sendParticles(ParticleTypes.POOF, position.x, position.y, position.z, 15, 0.2D, 0.2D, 0.2D, 0.0D);
        level.sendParticles(ParticleTypes.POOF, position.x, position.y, position.z, 15, 0.2D, 0.2D, 0.2D, 0.25D);
    }

    private static void playSummonSound(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof Level level)) {
            return;
        }
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "entity.ender_dragon.flap"));
        if (level.isClientSide()) {
            level.playLocalSound(x, y, z, soundEvent, SoundSource.NEUTRAL, 1.0F, 0.5F, false);
            return;
        }
        level.playSound(null, BlockPos.containing(x, y, z), soundEvent, SoundSource.NEUTRAL, 1.0F, 0.5F);
    }

    private static void restoreOwnerPose(Entity owner, float yaw, float pitch) {
        owner.setYRot(yaw);
        owner.setXRot(pitch);
        owner.setYBodyRot(owner.getYRot());
        owner.setYHeadRot(owner.getYRot());
    }

    private static void markNoChangeTechnique(Entity owner) {
        owner.getCapability(JujutsucraftModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
            capability.noChangeTechnique = true;
            capability.syncPlayerVariables(owner);
        });
    }

    private static int getStrengthAmplifier(Entity owner) {
        if (!(owner instanceof LivingEntity livingOwner) || !livingOwner.hasEffect(MobEffects.DAMAGE_BOOST)) {
            return 0;
        }
        return Objects.requireNonNull(livingOwner.getEffect(MobEffects.DAMAGE_BOOST)).getAmplifier();
    }

    private enum BaseCursePowerMode {
        NONE,
        NORMAL_DIRECT,
        FULL
    }

    private record ActiveSummonCandidate(Entity entity, RozetsuSummonRules.SummonKind kind, long summonedGameTime) {
        private RozetsuSummonRules.SummonOrder order() {
            return new RozetsuSummonRules.SummonOrder(this.summonedGameTime, this.entity.getId(), this.kind.cost());
        }
    }
}
