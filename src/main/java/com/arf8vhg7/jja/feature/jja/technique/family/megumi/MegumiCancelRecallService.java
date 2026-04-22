package com.arf8vhg7.jja.feature.jja.technique.family.megumi;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.entity.FushiguroMegumiEntity;
import net.mcreator.jujutsucraft.entity.MaxElephantEntity;
import net.mcreator.jujutsucraft.entity.RabbitEscapeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class MegumiCancelRecallService {
    private static final double SEARCH_RADIUS = 128.0D;
    private static final String KEY_AMBUSH = "Ambush";
    private static final String KEY_DESPAWN = "Despawn";
    private static final String KEY_DOMAIN_ENTITY = "domain_entity";
    private static final String KEY_FLAG_DESPAWN = "flag_despawn";
    private static final String KEY_NUM_TEN_SHADOWS_TECHNIQUE = "NUM_TenShadowsTechnique";

    private MegumiCancelRecallService() {
    }

    public static boolean tryHandleManualCancel(@Nullable Entity owner) {
        if (owner == null || !owner.isAlive()) {
            return false;
        }
        Level level = owner.level();
        if (level.isClientSide()) {
            return false;
        }

        List<RecallCandidate> candidates = collectCandidates(owner);
        RecallCandidate selectedCandidate = selectCandidate(candidates, owner.isShiftKeyDown());
        if (selectedCandidate == null || selectedCandidate.entity() == null) {
            return false;
        }

        List<RecallCandidate> recalledCandidates = selectRecallGroup(candidates, selectedCandidate);
        for (RecallCandidate recalledCandidate : recalledCandidates) {
            if (recalledCandidate.entity() != null) {
                recalledCandidate.entity().getPersistentData().putBoolean(KEY_FLAG_DESPAWN, true);
            }
        }
        JjaJujutsucraftDataAccess.jjaSetCurrentSkillValue(owner, 0.0D);
        owner.getPersistentData().putDouble(KEY_NUM_TEN_SHADOWS_TECHNIQUE, computeStoredLoad(candidates, recalledCandidates));
        return true;
    }

    private static List<RecallCandidate> collectCandidates(Entity owner) {
        double friendNum = JjaJujutsucraftDataAccess.jjaGetFriendNum(owner);
        if (friendNum == 0.0D) {
            return List.of();
        }
        String ownerUuid = owner.getStringUUID();
        boolean megumiOwner = owner instanceof FushiguroMegumiEntity;
        Vec3 center = owner.position();
        AABB searchBox = new AABB(center, center).inflate(SEARCH_RADIUS);
        return owner.level().getEntitiesOfClass(Entity.class, searchBox, candidate -> isRecallCandidate(owner, candidate, ownerUuid, friendNum)).stream()
            .map(candidate -> new RecallCandidate(candidate, candidate.tickCount, candidate.getId(), resolveLoadType(megumiOwner, candidate)))
            .toList();
    }

    private static boolean isRecallCandidate(Entity owner, Entity candidate, String ownerUuid, double friendNum) {
        if (candidate == owner || !candidate.isAlive()) {
            return false;
        }
        if (!candidate.getType().is(tenShadowsTechniqueTag())) {
            return false;
        }
        if (!candidate.getPersistentData().getBoolean(KEY_AMBUSH) || candidate.getPersistentData().getBoolean(KEY_DOMAIN_ENTITY)) {
            return false;
        }
        if (candidate.getPersistentData().getBoolean(KEY_FLAG_DESPAWN) || candidate.getPersistentData().getBoolean(KEY_DESPAWN)) {
            return false;
        }
        return ownerUuid.equals(JjaJujutsucraftDataAccess.jjaGetOwnerUuid(candidate))
            && JjaJujutsucraftDataAccess.jjaGetFriendNum(candidate) == friendNum;
    }

    private static TagKey<EntityType<?>> tenShadowsTechniqueTag() {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("jujutsucraft", "ten_shadows_technique"));
    }

    private static RecallLoadType resolveLoadType(boolean megumiOwner, Entity candidate) {
        if (candidate instanceof RabbitEscapeEntity) {
            return RecallLoadType.RABBIT_ESCAPE;
        }
        if (megumiOwner && candidate instanceof MaxElephantEntity) {
            return RecallLoadType.MAX_ELEPHANT;
        }
        return RecallLoadType.STANDARD;
    }

    static double computeStoredLoad(List<RecallCandidate> candidates, @Nullable RecallCandidate excludedCandidate) {
        return computeStoredLoad(candidates, excludedCandidate == null ? List.of() : List.of(excludedCandidate));
    }

    static double computeStoredLoad(List<RecallCandidate> candidates, List<RecallCandidate> excludedCandidates) {
        double totalLoad = 0.0D;
        for (RecallCandidate candidate : candidates) {
            if (excludedCandidates.contains(candidate)) {
                continue;
            }
            totalLoad += candidate.loadType().load();
        }
        return totalLoad > 2.0D ? 0.0D : totalLoad;
    }

    @Nullable
    static RecallCandidate selectCandidate(List<RecallCandidate> candidates, boolean crouching) {
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.stream().sorted(crouching ? oldestFirstOrder() : newestFirstOrder()).findFirst().orElse(null);
    }

    static List<RecallCandidate> selectRecallGroup(List<RecallCandidate> candidates, RecallCandidate selectedCandidate) {
        if (selectedCandidate.loadType() != RecallLoadType.RABBIT_ESCAPE) {
            return List.of(selectedCandidate);
        }
        return candidates.stream()
            .filter(candidate -> candidate.loadType() == RecallLoadType.RABBIT_ESCAPE)
            .toList();
    }

    private static Comparator<RecallCandidate> newestFirstOrder() {
        return Comparator.comparingInt(RecallCandidate::tickCount)
            .thenComparing(Comparator.comparingInt(RecallCandidate::entityId).reversed());
    }

    private static Comparator<RecallCandidate> oldestFirstOrder() {
        return Comparator.comparingInt(RecallCandidate::tickCount)
            .reversed()
            .thenComparingInt(RecallCandidate::entityId);
    }

    static record RecallCandidate(@Nullable Entity entity, int tickCount, int entityId, RecallLoadType loadType) {
    }

    enum RecallLoadType {
        STANDARD(1.0D),
        RABBIT_ESCAPE(0.025D),
        MAX_ELEPHANT(2.0D);

        private final double load;

        RecallLoadType(double load) {
            this.load = load;
        }

        double load() {
            return this.load;
        }
    }
}
