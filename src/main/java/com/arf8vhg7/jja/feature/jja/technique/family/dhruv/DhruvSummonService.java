package com.arf8vhg7.jja.feature.jja.technique.family.dhruv;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.entity.ShikigamiHeterocephalusGlaberEntity;
import net.mcreator.jujutsucraft.entity.ShikigamiPterosaurEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModEntities;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class DhruvSummonService {
    static final String KEY_SUMMONED_GAME_TIME = "jjaDhruvSummonedGameTime";
    private static final String KEY_FRIEND_NUM = "friend_num";
    private static final String KEY_FRIEND_NUM_WORKER = "friend_num_worker";
    private static final String KEY_OWNER_UUID = "OWNER_UUID";
    private static final String KEY_SHIKIGAMI = "Shikigami";
    private static final String KEY_PLAYER = "Player";
    private static final String KEY_JUJUTSU_SORCERER = "JujutsuSorcerer";
    private static final String KEY_CURSE_USER = "CurseUser";
    private static final String KEY_BASE_CURSE_POWER = "BaseCursePower";
    private static final String KEY_DOMAIN_ENTITY = "domain_entity";
    private static final String KEY_SKILL = "skill";
    private static final String KEY_OLD_X = "old_x";
    private static final String KEY_OLD_Y = "old_y";
    private static final String KEY_OLD_Z = "old_z";
    private static final String KEY_MODE = "mode";
    private static final String KEY_CNT1 = "cnt1";
    private static final String KEY_SHIKIGAMI_DHRUV1 = "SHIKIGAMI_DHRUV1";
    private static final String KEY_SHIKIGAMI_DHRUV2_1 = "SHIKIGAMI_DHRUV2_1";

    private DhruvSummonService() {
    }

    public static void handleMouseSummon(LevelAccessor world, Entity owner) {
        handleSummon(world, owner, SummonKind.HETEROCEPHALUS_GLABER);
    }

    public static void handlePterosaurSummon(LevelAccessor world, Entity owner) {
        handleSummon(world, owner, SummonKind.PTEROSAUR);
    }

    static int countActiveSummons(ServerLevel level, Entity owner, SummonKind kind) {
        return collectActiveSummons(level, owner, kind).size();
    }

    static int resolveMaxSummonsForOwner(Entity owner, SummonKind kind) {
        return resolveMaxSummons(kind, getStrengthAmplifier(owner));
    }

    static int resolveMaxSummons(SummonKind kind, int strengthAmplifier) {
        return DhruvSummonRules.resolveMaxSummons(kind.baseCap, strengthAmplifier);
    }

    @Nullable
    static SummonOrder selectOldestOrder(List<SummonOrder> orders) {
        DhruvSummonRules.SummonOrder selected = DhruvSummonRules.selectOldestOrder(
            orders.stream().map(order -> new DhruvSummonRules.SummonOrder(order.summonedGameTime(), order.entityId())).toList()
        );
        return selected == null ? null : new SummonOrder(selected.summonedGameTime(), selected.entityId());
    }

    private static void handleSummon(LevelAccessor world, Entity owner, SummonKind kind) {
        if (owner == null) {
            return;
        }

        if (owner instanceof LivingEntity livingOwner) {
            livingOwner.swing(InteractionHand.MAIN_HAND, true);
        }

        if (!(world instanceof ServerLevel serverLevel)) {
            owner.getPersistentData().putDouble(KEY_SKILL, 0.0D);
            return;
        }

        ensureFriendNum(owner);
        killOldestIfAtCapacity(serverLevel, owner, kind);

        BlockPos summonPos = resolveSummonBlockPos(owner, kind.rayDistance);
        LivingEntity summon = kind.spawn(serverLevel, summonPos);
        if (summon == null) {
            owner.getPersistentData().putDouble(KEY_SKILL, 0.0D);
            return;
        }

        initializeSpawnPose(owner, summon);
        initializeSummonMetadata(owner, summon);
        applyKindSpecificState(owner, summon, kind);
        applySummonMaxHealth(owner, summon, kind.healthMultiplier);
        applyOwnerResistance(owner, summon);
        applyBaseCursePower(owner, summon, kind.baseCursePowerMultiplier);
        serverLevel.addFreshEntity(summon);
        updateLatestSummonSlot(owner, summon, kind);
        owner.getPersistentData().putDouble(KEY_SKILL, 0.0D);
    }

    private static void killOldestIfAtCapacity(ServerLevel level, Entity owner, SummonKind kind) {
        List<ActiveSummonCandidate> activeSummons = collectActiveSummons(level, owner, kind);
        if (activeSummons.size() < resolveMaxSummons(kind, getStrengthAmplifier(owner))) {
            return;
        }

        ActiveSummonCandidate oldest = activeSummons.stream()
            .min(Comparator.comparingLong(ActiveSummonCandidate::summonedGameTime).thenComparingInt(ActiveSummonCandidate::entityId))
            .orElse(null);
        if (oldest != null) {
            oldest.entity().kill();
        }
    }

    private static List<ActiveSummonCandidate> collectActiveSummons(ServerLevel level, Entity owner, SummonKind kind) {
        String ownerUuid = owner.getStringUUID();
        double friendNum = owner.getPersistentData().getDouble(KEY_FRIEND_NUM);
        List<ActiveSummonCandidate> activeSummons = new ArrayList<>();
        for (ServerLevel serverLevel : level.getServer().getAllLevels()) {
            for (Entity candidate : serverLevel.getAllEntities()) {
                if (!kind.matches(candidate)
                    || !candidate.isAlive()
                    || candidate.isRemoved()
                    || candidate.getPersistentData().getBoolean(KEY_DOMAIN_ENTITY)
                    || !ownerUuid.equals(candidate.getPersistentData().getString(KEY_OWNER_UUID))
                    || Double.compare(friendNum, candidate.getPersistentData().getDouble(KEY_FRIEND_NUM_WORKER)) != 0) {
                    continue;
                }
                activeSummons.add(new ActiveSummonCandidate(
                    candidate,
                    candidate.getPersistentData().getLong(KEY_SUMMONED_GAME_TIME),
                    candidate.getId()
                ));
            }
        }
        return activeSummons;
    }

    private static void ensureFriendNum(Entity owner) {
        if (owner.getPersistentData().getDouble(KEY_FRIEND_NUM) == 0.0D) {
            owner.getPersistentData().putDouble(KEY_FRIEND_NUM, Math.random());
        }
    }

    private static BlockPos resolveSummonBlockPos(Entity owner, double rayDistance) {
        Vec3 eyePosition = owner.getEyePosition(1.0F);
        Vec3 endPosition = eyePosition.add(owner.getViewVector(1.0F).scale(rayDistance));
        BlockHitResult blockHit = owner.level().clip(new ClipContext(
            eyePosition,
            endPosition,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            owner
        ));
        return blockHit.getBlockPos();
    }

    private static void initializeSpawnPose(Entity owner, Entity summon) {
        summon.moveTo(summon.getX(), summon.getY(), summon.getZ(), owner.getYRot(), owner.getXRot());
        if (summon instanceof LivingEntity livingSummon) {
            livingSummon.setYBodyRot(owner.getYRot());
            livingSummon.setYHeadRot(owner.getYRot());
        }
    }

    private static void initializeSummonMetadata(Entity owner, LivingEntity summon) {
        summon.getPersistentData().putString(KEY_OWNER_UUID, owner.getStringUUID());
        summon.getPersistentData().putDouble(KEY_FRIEND_NUM, owner.getPersistentData().getDouble(KEY_FRIEND_NUM));
        summon.getPersistentData().putDouble(KEY_FRIEND_NUM_WORKER, owner.getPersistentData().getDouble(KEY_FRIEND_NUM));
        summon.getPersistentData().putBoolean(KEY_SHIKIGAMI, true);
        summon.getPersistentData().putBoolean(KEY_PLAYER, owner instanceof Player || owner.getPersistentData().getBoolean(KEY_PLAYER));
        summon.getPersistentData().putBoolean(KEY_JUJUTSU_SORCERER, owner.getPersistentData().getBoolean(KEY_JUJUTSU_SORCERER));
        summon.getPersistentData().putBoolean(KEY_CURSE_USER, owner.getPersistentData().getBoolean(KEY_CURSE_USER));
        summon.getPersistentData().putLong(KEY_SUMMONED_GAME_TIME, summon.level().getGameTime());
    }

    private static void applyKindSpecificState(Entity owner, LivingEntity summon, SummonKind kind) {
        if (kind != SummonKind.PTEROSAUR) {
            return;
        }
        summon.getPersistentData().putDouble(KEY_OLD_X, owner.getX());
        summon.getPersistentData().putDouble(KEY_OLD_Y, owner.getY());
        summon.getPersistentData().putDouble(KEY_OLD_Z, owner.getZ());
        summon.getPersistentData().putDouble(KEY_MODE, 1.0D);
        summon.getPersistentData().putDouble(KEY_SKILL, 1.0D);
        summon.getPersistentData().putDouble(KEY_CNT1, 50.0D);
    }

    private static void applySummonMaxHealth(Entity owner, LivingEntity summon, double multiplier) {
        AttributeInstance summonMaxHealth = summon.getAttribute(Attributes.MAX_HEALTH);
        if (summonMaxHealth == null) {
            return;
        }
        double ownerMaxHealth = 0.0D;
        AttributeInstance ownerMaxHealthAttribute = owner instanceof LivingEntity livingOwner ? livingOwner.getAttribute(Attributes.MAX_HEALTH) : null;
        if (ownerMaxHealthAttribute != null) {
            ownerMaxHealth = ownerMaxHealthAttribute.getValue();
        }
        double boostedMaxHealth = summonMaxHealth.getValue() + (ownerMaxHealth + getStrengthAmplifier(owner) * 3.0D) * multiplier;
        summonMaxHealth.setBaseValue(boostedMaxHealth);
        summon.setHealth((float) summon.getMaxHealth());
    }

    private static void applyOwnerResistance(Entity owner, LivingEntity summon) {
        int resistanceAmplifier = owner instanceof LivingEntity livingOwner && livingOwner.hasEffect(MobEffects.DAMAGE_RESISTANCE)
            ? Math.max(livingOwner.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier(), 0)
            : 0;
        summon.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, resistanceAmplifier, false, false));
    }

    private static void applyBaseCursePower(Entity owner, LivingEntity summon, double multiplier) {
        if (!(owner instanceof Player)) {
            return;
        }
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariablesOrDefault(owner);
        summon.getPersistentData().putDouble(KEY_BASE_CURSE_POWER, playerVariables.PlayerSelectCurseTechniqueCost * multiplier);
    }

    private static void updateLatestSummonSlot(Entity owner, Entity summon, SummonKind kind) {
        if (kind == SummonKind.HETEROCEPHALUS_GLABER) {
            owner.getPersistentData().putString(KEY_SHIKIGAMI_DHRUV1, summon.getStringUUID());
            return;
        }
        owner.getPersistentData().putString(KEY_SHIKIGAMI_DHRUV2_1, summon.getStringUUID());
    }

    private static int getStrengthAmplifier(Entity owner) {
        if (!(owner instanceof LivingEntity livingOwner) || !livingOwner.hasEffect(MobEffects.DAMAGE_BOOST)) {
            return 0;
        }
        return livingOwner.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier();
    }

    enum SummonKind {
        HETEROCEPHALUS_GLABER(10, 2.0D, 1.0D, 1.0D),
        PTEROSAUR(3, 3.0D, 0.75D, 0.25D);

        private final int baseCap;
        private final double rayDistance;
        private final double healthMultiplier;
        private final double baseCursePowerMultiplier;

        SummonKind(int baseCap, double rayDistance, double healthMultiplier, double baseCursePowerMultiplier) {
            this.baseCap = baseCap;
            this.rayDistance = rayDistance;
            this.healthMultiplier = healthMultiplier;
            this.baseCursePowerMultiplier = baseCursePowerMultiplier;
        }

        @Nullable
        private LivingEntity spawn(ServerLevel level, BlockPos summonPos) {
            return switch (this) {
                case HETEROCEPHALUS_GLABER -> JujutsucraftModEntities.SHIKIGAMI_HETEROCEPHALUS_GLABER.get()
                    .create(level, null, null, summonPos, MobSpawnType.MOB_SUMMONED, false, false);
                case PTEROSAUR -> JujutsucraftModEntities.SHIKIGAMI_PTEROSAUR.get()
                    .create(level, null, null, summonPos, MobSpawnType.MOB_SUMMONED, false, false);
            };
        }

        private boolean matches(Entity entity) {
            return switch (this) {
                case HETEROCEPHALUS_GLABER -> entity instanceof ShikigamiHeterocephalusGlaberEntity;
                case PTEROSAUR -> entity instanceof ShikigamiPterosaurEntity;
            };
        }
    }

    record SummonOrder(long summonedGameTime, int entityId) {
    }

    private record ActiveSummonCandidate(Entity entity, long summonedGameTime, int entityId) {
    }
}
