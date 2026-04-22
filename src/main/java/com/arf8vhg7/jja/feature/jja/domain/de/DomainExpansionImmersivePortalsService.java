package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.JujutsuAddon;
import com.arf8vhg7.jja.compat.immersiveportals.JjaImmersivePortalsCompat;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.entity.DomainExpansionEntityEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.DomainExpansionBattleProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public final class DomainExpansionImmersivePortalsService {
    static final ResourceLocation DOMAIN_DIMENSION_ID = ResourceLocation.fromNamespaceAndPath(JujutsuAddon.MODID, "domain");
    static final ResourceKey<Level> DOMAIN_DIMENSION = ResourceKey.create(Registries.DIMENSION, DOMAIN_DIMENSION_ID);

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String KEY_DOMAIN_MIRROR = "jja_domain_ip_mirror";
    private static final String KEY_DOMAIN_MIRROR_OWNER = "jja_domain_ip_mirror_owner";
    private static final int DOMAIN_SLOT_SPACING = 1024;
    private static final int SESSION_CHUNK_LOADER_RADIUS = 4;
    private static final int KEEPALIVE_CHUNK_LOADER_RADIUS = 0;
    private static final double TRANSFER_READY_COUNTER = 2.0D;

    private static final Map<UUID, Session> SESSIONS = new HashMap<>();

    @Nullable
    private static Object keepaliveChunkLoader;

    private DomainExpansionImmersivePortalsService() {
    }

    public static void onServerStarted(MinecraftServer server) {
        if (!JjaImmersivePortalsCompat.isAvailable()) {
            return;
        }

        ServerLevel domainLevel = server.getLevel(DOMAIN_DIMENSION);
        if (domainLevel == null) {
            LOGGER.warn("Immersive Portals domain support is enabled but {} is missing", DOMAIN_DIMENSION_ID);
            return;
        }

        if (keepaliveChunkLoader == null) {
            keepaliveChunkLoader = createChunkLoader(DOMAIN_DIMENSION, Vec3.ZERO, KEEPALIVE_CHUNK_LOADER_RADIUS);
            JjaImmersivePortalsCompat.addGlobalChunkLoader(keepaliveChunkLoader);
        }
    }

    public static void onServerStopping(MinecraftServer server) {
        for (Session session : new ArrayList<>(SESSIONS.values())) {
            discardSession(server, session, true, true);
        }

        SESSIONS.clear();

        if (keepaliveChunkLoader != null) {
            JjaImmersivePortalsCompat.removeGlobalChunkLoader(keepaliveChunkLoader);
            keepaliveChunkLoader = null;
        }
    }

    public static void tick(@Nullable MinecraftServer server) {
        if (server == null || !JjaImmersivePortalsCompat.isAvailable()) {
            return;
        }

        if (server.getLevel(DOMAIN_DIMENSION) == null) {
            return;
        }

        discoverPreparingSessions(server);

        List<Session> sessions = new ArrayList<>(SESSIONS.values());
        for (Session session : sessions) {
            if (session.transferred) {
                tickTransferredSession(server, session);
            } else {
                tickPreparingSession(server, session);
            }
        }
    }

    public static void onPlayerLoggedOut(Entity player) {
        if (!(player instanceof LivingEntity livingEntity) || !JjaImmersivePortalsCompat.isAvailable()) {
            return;
        }

        Session session = findContainingPocketSession(livingEntity);
        if (session == null) {
            return;
        }

        ServerLevel originLevel = livingEntity.getServer() == null ? null : livingEntity.getServer().getLevel(session.originDimension);
        if (originLevel == null) {
            return;
        }

        teleportRelativeToCenter(livingEntity, originLevel, session.pocketCenter, session.originCenter);
        if (Objects.equals(livingEntity.getUUID(), session.ownerId)) {
            Entity originOwner = findEntity(originLevel, livingEntity.getUUID());
            if (originOwner != null) {
                applyDomainCenters(originOwner, session.originCenter);
            }
        }
    }

    public static Vec3 resolveTransferredPocketBuildCenter(Entity entity, double x, double y, double z) {
        Vec3 originalCenter = new Vec3(x, y, z);
        return resolveTransferredPocketBattleBuildState(entity, x, y, z).center();
    }

    public static BattleBuildState resolveTransferredPocketBattleBuildState(Entity entity, double x, double y, double z) {
        Vec3 originalCenter = new Vec3(x, y, z);
        if (entity == null || entity.level().dimension() != DOMAIN_DIMENSION) {
            return new BattleBuildState(originalCenter, false);
        }

        Session session = SESSIONS.get(entity.getUUID());
        boolean managedTransferredSession = session != null && session.transferred;
        Vec3 center = resolveTransferredPocketBuildCenter(
            managedTransferredSession,
            originalCenter,
            session == null ? null : session.pocketCenter
        );
        return new BattleBuildState(center, managedTransferredSession);
    }

    public static void applyTransferredPocketBattleBuildState(Entity entity, BattleBuildState state) {
        if (entity != null && state.managedTransferredPocket()) {
            applyDomainCenters(entity, state.center());
        }
    }

    public static void ensureSynchronizedBarrierSession(LevelAccessor world, Entity entity) {
        ensureSessionForBuild(world, entity);
    }

    public static void runSynchronizedSecondaryBarrierBuild(LevelAccessor world, Entity entity) {
        if (!(world instanceof ServerLevel sourceLevel) || !(entity instanceof LivingEntity source) || isDomainMirror(entity)) {
            return;
        }

        Session session = ensureSessionForBuild(world, source);
        if (session == null) {
            return;
        }

        MinecraftServer server = sourceLevel.getServer();
        if (sourceLevel.dimension() == DOMAIN_DIMENSION) {
            runOriginBarrierBuild(server, session, source);
            return;
        }

        if (sourceLevel.dimension() == session.originDimension) {
            runPocketBarrierBuild(server, session, source);
        }
    }

    public static void onRadiusChanged(Entity owner, double oldRadius, double newRadius) {
        if (owner == null || !JjaImmersivePortalsCompat.isAvailable()) {
            return;
        }

        MinecraftServer server = owner.getServer();
        Session session = SESSIONS.get(owner.getUUID());
        if (server == null || session == null) {
            return;
        }

        ServerLevel originLevel = server.getLevel(session.originDimension);
        ServerLevel domainLevel = server.getLevel(DOMAIN_DIMENSION);
        if (originLevel != null) {
            DomainBarrierCleanup.rebuildChangedBand(originLevel, owner, session.originCenter, oldRadius, newRadius);
        }
        if (domainLevel != null) {
            DomainBarrierCleanup.rebuildChangedBand(domainLevel, owner, session.pocketCenter, oldRadius, newRadius);
        }
    }

    static Vec3 resolveTransferredPocketBuildCenter(boolean managedTransferredSession, Vec3 originalCenter, @Nullable Vec3 pocketCenter) {
        return DomainExpansionPocketBuildCenterRules.resolveTransferredPocketBuildCenter(
            managedTransferredSession,
            originalCenter,
            pocketCenter
        );
    }

    @Nullable
    private static Session ensureSessionForBuild(LevelAccessor world, Entity entity) {
        if (!(world instanceof ServerLevel sourceLevel) || !(entity instanceof LivingEntity livingEntity)) {
            return null;
        }

        if (!JjaImmersivePortalsCompat.isAvailable() || sourceLevel.getServer().getLevel(DOMAIN_DIMENSION) == null) {
            return null;
        }

        if (sourceLevel.dimension() == DOMAIN_DIMENSION) {
            return SESSIONS.get(entity.getUUID());
        }

        return ensurePreparingSession(sourceLevel.getServer(), sourceLevel, livingEntity);
    }

    @Nullable
    private static Session ensurePreparingSession(MinecraftServer server, ServerLevel originLevel, LivingEntity owner) {
        if (originLevel.dimension() == DOMAIN_DIMENSION || isDomainMirror(owner)) {
            return null;
        }

        Session existing = SESSIONS.get(owner.getUUID());
        if (existing != null) {
            return existing;
        }

        if (!isClosedBarrierBuildCandidate(owner) && !DomainExpansionHookSupport.isClosedDomainActive(owner)) {
            return null;
        }

        ServerLevel domainLevel = server.getLevel(DOMAIN_DIMENSION);
        if (domainLevel == null) {
            return null;
        }

        Vec3 pocketCenter = findNextPocketCenter();
        Object chunkLoader = createChunkLoader(DOMAIN_DIMENSION, pocketCenter, SESSION_CHUNK_LOADER_RADIUS);
        if (chunkLoader == null) {
            return null;
        }

        JjaImmersivePortalsCompat.addGlobalChunkLoader(chunkLoader);

        Session session = new Session(
            owner.getUUID(),
            originLevel.dimension(),
            resolveDomainCenter(owner, owner.position()),
            pocketCenter,
            resolveDomainRadius(owner),
            chunkLoader
        );
        SESSIONS.put(owner.getUUID(), session);
        return session;
    }

    private static void runPocketBarrierBuild(MinecraftServer server, Session session, LivingEntity owner) {
        if (session.transferred) {
            return;
        }

        ServerLevel domainLevel = server.getLevel(DOMAIN_DIMENSION);
        if (domainLevel == null) {
            return;
        }

        session.radius = resolveDomainRadius(owner);
        session.originCenter = resolveDomainCenter(owner, session.originCenter);

        ArmorStand buildMirror = ensureMirror(domainLevel, session.buildMirrorId, session.ownerId, session.pocketCenter);
        session.buildMirrorId = buildMirror.getUUID();
        syncMirror(buildMirror, owner, session.pocketCenter);
        DomainExpansionBattleProcedure.execute(domainLevel, session.pocketCenter.x, session.pocketCenter.y, session.pocketCenter.z, buildMirror);
    }

    private static void runOriginBarrierBuild(MinecraftServer server, Session session, LivingEntity pocketOwner) {
        if (!session.transferred) {
            return;
        }

        ServerLevel originLevel = server.getLevel(session.originDimension);
        if (originLevel == null) {
            return;
        }

        session.radius = resolveDomainRadius(pocketOwner);

        ArmorStand originMirror = ensureMirror(originLevel, session.originMirrorId, session.ownerId, session.originCenter);
        session.originMirrorId = originMirror.getUUID();
        syncMirror(originMirror, pocketOwner, session.originCenter);
        DomainExpansionBattleProcedure.execute(originLevel, session.originCenter.x, session.originCenter.y, session.originCenter.z, originMirror);
    }

    private static void discoverPreparingSessions(MinecraftServer server) {
        Set<UUID> activeOwnerIds = new HashSet<>(SESSIONS.keySet());
        if (server.getLevel(DOMAIN_DIMENSION) == null) {
            return;
        }

        for (ServerLevel level : server.getAllLevels()) {
            if (level.dimension() == DOMAIN_DIMENSION) {
                continue;
            }

            for (Entity entity : level.getAllEntities()) {
                if (!(entity instanceof LivingEntity livingEntity) || activeOwnerIds.contains(entity.getUUID()) || isDomainMirror(entity)) {
                    continue;
                }

                if (!isClosedBarrierBuildCandidate(livingEntity)) {
                    continue;
                }

                if (ensurePreparingSession(server, level, livingEntity) != null) {
                    activeOwnerIds.add(livingEntity.getUUID());
                }
            }
        }
    }

    private static void tickPreparingSession(MinecraftServer server, Session session) {
        ServerLevel originLevel = server.getLevel(session.originDimension);
        ServerLevel domainLevel = server.getLevel(DOMAIN_DIMENSION);
        if (originLevel == null || domainLevel == null) {
            discardSession(server, session, true, false);
            return;
        }

        LivingEntity owner = findLivingEntity(originLevel, session.ownerId);
        if (owner == null) {
            discardSession(server, session, true, false);
            return;
        }

        boolean closedDomainActive = DomainExpansionHookSupport.isClosedDomainActive(owner);
        if (!closedDomainActive && !isClosedBarrierBuildCandidate(owner)) {
            discardSession(server, session, true, false);
            return;
        }

        session.radius = resolveDomainRadius(owner);
        session.originCenter = resolveDomainCenter(owner, session.originCenter);

        ArmorStand buildMirror = ensureMirror(domainLevel, session.buildMirrorId, session.ownerId, session.pocketCenter);
        session.buildMirrorId = buildMirror.getUUID();
        syncMirror(buildMirror, owner, session.pocketCenter);

        if (!closedDomainActive) {
            return;
        }

        if (owner.getPersistentData().getDouble("skill_domain") <= 0.0D) {
            return;
        }

        if (!JjaImmersivePortalsCompat.isChunkLoaderReady(session.chunkLoader)) {
            return;
        }

        if (!isTransferReady(originLevel, owner, session.originCenter, session.radius)) {
            return;
        }

        transferEntitiesIntoPocket(originLevel, domainLevel, session);

        LivingEntity transferredOwner = findLivingEntity(domainLevel, session.ownerId);
        if (transferredOwner == null) {
            discardSession(server, session, true, false);
            return;
        }

        applyDomainCenters(transferredOwner, session.pocketCenter);
        removeMirror(domainLevel, session.buildMirrorId);
        session.buildMirrorId = null;

        ArmorStand originMirror = ensureMirror(originLevel, session.originMirrorId, session.ownerId, session.originCenter);
        session.originMirrorId = originMirror.getUUID();
        syncMirror(originMirror, transferredOwner, session.originCenter);

        session.transferred = true;
    }

    private static void tickTransferredSession(MinecraftServer server, Session session) {
        ServerLevel originLevel = server.getLevel(session.originDimension);
        ServerLevel domainLevel = server.getLevel(DOMAIN_DIMENSION);
        if (originLevel == null || domainLevel == null) {
            discardSession(server, session, true, true);
            return;
        }

        LivingEntity pocketOwner = findLivingEntity(domainLevel, session.ownerId);
        if (pocketOwner == null || !DomainExpansionHookSupport.isClosedDomainActive(pocketOwner)) {
            restoreAndDiscardTransferredSession(server, session, true);
            return;
        }

        session.radius = resolveDomainRadius(pocketOwner);
        applyDomainCenters(pocketOwner, session.pocketCenter);

        ArmorStand originMirror = ensureMirror(originLevel, session.originMirrorId, session.ownerId, session.originCenter);
        session.originMirrorId = originMirror.getUUID();
        syncMirror(originMirror, pocketOwner, session.originCenter);

        transferNearbyEntities(originLevel, domainLevel, session.originCenter, session.pocketCenter, session.radius, session.ownerId);
    }

    private static boolean isTransferReady(ServerLevel level, LivingEntity owner, Vec3 originCenter, double radius) {
        if (DomainExpansionHookSupport.getCounter(owner) < TRANSFER_READY_COUNTER) {
            return false;
        }

        if (owner.getPersistentData().getBoolean("Failed") || owner.getPersistentData().getBoolean("Cover")) {
            return false;
        }

        return !hasBarrierDomainClash(level, owner, originCenter, radius);
    }

    private static void transferEntitiesIntoPocket(ServerLevel originLevel, ServerLevel domainLevel, Session session) {
        transferNearbyEntities(originLevel, domainLevel, session.originCenter, session.pocketCenter, session.radius, session.ownerId);
    }

    private static void transferNearbyEntities(
        ServerLevel sourceLevel,
        ServerLevel targetLevel,
        Vec3 sourceCenter,
        Vec3 targetCenter,
        double radius,
        UUID ownerId
    ) {
        List<Entity> entities = collectTransferableEntities(sourceLevel, sourceCenter, radius);
        Entity owner = null;
        for (Entity entity : entities) {
            if (Objects.equals(entity.getUUID(), ownerId)) {
                owner = entity;
                continue;
            }

            teleportRelativeToCenter(entity, targetLevel, sourceCenter, targetCenter);
        }

        if (owner != null) {
            teleportRelativeToCenter(owner, targetLevel, sourceCenter, targetCenter);
        }
    }

    private static void restoreAndDiscardTransferredSession(MinecraftServer server, Session session, boolean breakPocketBarrier) {
        ServerLevel originLevel = server.getLevel(session.originDimension);
        ServerLevel domainLevel = server.getLevel(DOMAIN_DIMENSION);
        if (originLevel != null && domainLevel != null) {
            List<Entity> entities = collectTransferableEntities(domainLevel, session.pocketCenter, session.radius);
            for (Entity entity : entities) {
                teleportRelativeToCenter(entity, originLevel, session.pocketCenter, session.originCenter);
            }

            Entity restoredOwner = findEntity(originLevel, session.ownerId);
            if (restoredOwner != null) {
                applyDomainCenters(restoredOwner, session.originCenter);
            }

            markBarrierForBreak(originLevel, session.originCenter);
            if (breakPocketBarrier) {
                markBarrierForBreak(domainLevel, session.pocketCenter);
            }
        }

        discardSession(server, session, false, false);
    }

    private static void discardSession(@Nullable MinecraftServer server, Session session, boolean breakPocketBarrier, boolean breakOriginBarrier) {
        if (server != null) {
            ServerLevel originLevel = server.getLevel(session.originDimension);
            ServerLevel domainLevel = server.getLevel(DOMAIN_DIMENSION);
            if (breakOriginBarrier && originLevel != null) {
                markBarrierForBreak(originLevel, session.originCenter);
            }
            if (breakPocketBarrier && domainLevel != null) {
                markBarrierForBreak(domainLevel, session.pocketCenter);
            }
            if (originLevel != null) {
                removeMirror(originLevel, session.originMirrorId);
            }
            if (domainLevel != null) {
                removeMirror(domainLevel, session.buildMirrorId);
            }
        }

        if (session.chunkLoader != null) {
            JjaImmersivePortalsCompat.removeGlobalChunkLoader(session.chunkLoader);
            session.chunkLoader = null;
        }

        SESSIONS.remove(session.ownerId);
    }

    private static boolean hasBarrierDomainClash(ServerLevel level, Entity owner, Vec3 center, double radius) {
        double clashDistanceSqr = square(Math.max(radius * 2.0D, 1.0D));
        for (Entity candidate : level.getAllEntities()) {
            if (candidate == owner || !isBarrierDomainClashCandidate(candidate)) {
                continue;
            }

            Vec3 candidateCenter = resolveDomainCenter(candidate, candidate.position());
            if (candidateCenter.distanceToSqr(center) < clashDistanceSqr) {
                return true;
            }
        }

        return false;
    }

    private static boolean isBarrierDomainClashCandidate(Entity entity) {
        return entity instanceof LivingEntity livingEntity && (DomainExpansionHookSupport.isClosedDomainActive(livingEntity) || isClosedBarrierBuildCandidate(livingEntity));
    }

    private static boolean isClosedBarrierBuildCandidate(LivingEntity livingEntity) {
        if (isDomainMirror(livingEntity)) {
            return false;
        }

        if (livingEntity.level().dimension() == DOMAIN_DIMENSION) {
            return false;
        }

        if (DomainExpansionHookSupport.isClosedDomainActive(livingEntity)) {
            return false;
        }

        return livingEntity.getPersistentData().getDouble("select") != 0.0D
            && livingEntity.getPersistentData().getDouble("skill_domain") == 0.0D
            && livingEntity.getPersistentData().getDouble("cnt7") > 0.0D
            && livingEntity.getPersistentData().getDouble("cnt1") > 0.0D
            && livingEntity.getPersistentData().getDouble("cnt2") == 0.0D;
    }

    private static double resolveDomainRadius(Entity entity) {
        return Math.max(
            DomainExpansionHookSupport.resolveCurrentRadius(entity, DomainExpansionConfiguredRadiusSync.getConfiguredRadius()),
            1.0D
        );
    }

    private static Vec3 resolveDomainCenter(Entity entity, Vec3 fallback) {
        Vec3 domainCenter = JjaJujutsucraftDataAccess.jjaGetDomainCenter(entity);
        return domainCenter != null ? domainCenter : fallback;
    }

    private static List<Entity> collectTransferableEntities(ServerLevel level, Vec3 center, double radius) {
        double radiusSqr = square(radius + 0.5D);
        List<Entity> result = new ArrayList<>();
        for (Entity entity : level.getAllEntities()) {
            if (!isTransferableEntity(entity)) {
                continue;
            }

            if (entity.position().distanceToSqr(center) > radiusSqr) {
                continue;
            }

            result.add(entity);
        }
        return result;
    }

    private static boolean isTransferableEntity(Entity entity) {
        if (entity.isRemoved() || isDomainMirror(entity) || entity instanceof DomainExpansionEntityEntity) {
            return false;
        }

        if (!entity.canChangeDimensions()) {
            return false;
        }

        return entity.level().dimension() != DOMAIN_DIMENSION || !entity.getPersistentData().getBoolean(KEY_DOMAIN_MIRROR);
    }

    private static void teleportRelativeToCenter(Entity entity, ServerLevel targetLevel, Vec3 sourceCenter, Vec3 targetCenter) {
        Vec3 relativeOffset = entity.position().subtract(sourceCenter);
        Vec3 targetPos = targetCenter.add(relativeOffset);
        JjaImmersivePortalsCompat.teleportEntity(entity, targetLevel, targetPos);
    }

    private static ArmorStand ensureMirror(ServerLevel level, @Nullable UUID mirrorId, UUID ownerId, Vec3 center) {
        Entity existing = mirrorId == null ? null : findEntity(level, mirrorId);
        if (existing instanceof ArmorStand armorStand && isDomainMirror(armorStand)) {
            armorStand.moveTo(center.x, center.y, center.z);
            return armorStand;
        }

        ArmorStand mirror = new ArmorStand(level, center.x, center.y, center.z);
        mirror.setInvisible(true);
        mirror.setInvulnerable(true);
        mirror.setNoGravity(true);
        mirror.setSilent(true);
        mirror.getPersistentData().putBoolean(KEY_DOMAIN_MIRROR, true);
        mirror.getPersistentData().putString(KEY_DOMAIN_MIRROR_OWNER, ownerId.toString());
        level.addFreshEntity(mirror);
        return mirror;
    }

    private static void syncMirror(ArmorStand mirror, LivingEntity source, Vec3 mappedCenter) {
        copyPersistentData(source, mirror);
        mirror.getPersistentData().putBoolean(KEY_DOMAIN_MIRROR, true);
        mirror.getPersistentData().putString(KEY_DOMAIN_MIRROR_OWNER, source.getUUID().toString());
        applyDomainCenters(mirror, mappedCenter);

        mirror.moveTo(mappedCenter.x, mappedCenter.y, mappedCenter.z, source.getYRot(), source.getXRot());
        mirror.setYBodyRot(source.getYRot());
        mirror.setYHeadRot(source.getYHeadRot());
        mirror.setXRot(source.getXRot());
        syncBarrierBuildEffects(mirror, source);

        AttributeInstance maxHealthAttribute = mirror.getAttribute(Attributes.MAX_HEALTH);
        double maxHealth = Math.max(source.getMaxHealth(), 1.0D);
        if (maxHealthAttribute != null) {
            maxHealthAttribute.setBaseValue(maxHealth);
        }
        mirror.setHealth((float) Math.min(Math.max(source.getHealth(), 1.0F), maxHealth));
    }

    private static void syncBarrierBuildEffects(ArmorStand mirror, LivingEntity source) {
        syncEffect(mirror, source, JujutsucraftModMobEffects.DOMAIN_EXPANSION.get());
        syncEffect(mirror, source, JujutsucraftModMobEffects.ZONE.get());
        syncEffect(mirror, source, MobEffects.DAMAGE_BOOST);
    }

    private static void syncEffect(LivingEntity target, LivingEntity source, @Nullable MobEffect effect) {
        if (effect == null) {
            return;
        }

        MobEffectInstance sourceEffect = source.getEffect(effect);
        if (sourceEffect == null) {
            target.removeEffect(effect);
            return;
        }

        target.addEffect(new MobEffectInstance(sourceEffect));
    }

    private static void copyPersistentData(Entity source, Entity target) {
        Set<String> existingKeys = Set.copyOf(target.getPersistentData().getAllKeys());
        for (String key : existingKeys) {
            target.getPersistentData().remove(key);
        }
        target.getPersistentData().merge(source.getPersistentData().copy());
    }

    private static void applyDomainCenters(Entity entity, Vec3 center) {
        JjaJujutsucraftDataAccess.jjaSetDomainCenter(entity, center);
        JjaJujutsucraftDataAccess.jjaSetDomainPatternOrigin(entity, center);
    }

    private static void markBarrierForBreak(ServerLevel level, Vec3 center) {
        List<DomainExpansionEntityEntity> barriers = level.getEntitiesOfClass(
            DomainExpansionEntityEntity.class,
            new AABB(
                center.x - 0.5D,
                center.y - 0.5D,
                center.z - 0.5D,
                center.x + 0.5D,
                center.y + 0.5D,
                center.z + 0.5D
            )
        );
        for (DomainExpansionEntityEntity barrier : barriers) {
            barrier.getPersistentData().putBoolean("Break", true);
        }
    }

    private static void removeMirror(ServerLevel level, @Nullable UUID mirrorId) {
        Entity entity = mirrorId == null ? null : findEntity(level, mirrorId);
        if (entity != null) {
            entity.discard();
        }
    }

    private static boolean isDomainMirror(Entity entity) {
        return entity.getPersistentData().getBoolean(KEY_DOMAIN_MIRROR);
    }

    @Nullable
    private static Session findContainingPocketSession(LivingEntity entity) {
        for (Session session : SESSIONS.values()) {
            if (!session.transferred || entity.level().dimension() != DOMAIN_DIMENSION) {
                continue;
            }

            if (entity.position().distanceToSqr(session.pocketCenter) <= square(session.radius + 0.5D)) {
                return session;
            }
        }

        return null;
    }

    @Nullable
    private static Entity findEntity(ServerLevel level, UUID uuid) {
        for (Entity entity : level.getAllEntities()) {
            if (Objects.equals(entity.getUUID(), uuid)) {
                return entity;
            }
        }
        return null;
    }

    @Nullable
    private static LivingEntity findLivingEntity(ServerLevel level, UUID uuid) {
        Entity entity = findEntity(level, uuid);
        return entity instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Nullable
    private static Object createChunkLoader(ResourceKey<Level> dimension, Vec3 center, int radius) {
        BlockPos blockPos = BlockPos.containing(center);
        int chunkX = SectionPos.blockToSectionCoord(blockPos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());
        return JjaImmersivePortalsCompat.createChunkLoader(dimension, chunkX, chunkZ, radius);
    }

    private static Vec3 findNextPocketCenter() {
        Set<Long> occupiedSlots = new HashSet<>();
        for (Session session : SESSIONS.values()) {
            int gridX = Mth.floor(session.pocketCenter.x / DOMAIN_SLOT_SPACING);
            int gridZ = Mth.floor(session.pocketCenter.z / DOMAIN_SLOT_SPACING);
            occupiedSlots.add(packGrid(gridX, gridZ));
        }

        for (int radius = 0; radius < 256; radius++) {
            for (int gridX = -radius; gridX <= radius; gridX++) {
                for (int gridZ = -radius; gridZ <= radius; gridZ++) {
                    if (Math.max(Math.abs(gridX), Math.abs(gridZ)) != radius) {
                        continue;
                    }

                    long packed = packGrid(gridX, gridZ);
                    if (occupiedSlots.contains(packed)) {
                        continue;
                    }

                    return new Vec3(gridX * (double) DOMAIN_SLOT_SPACING, 0.0D, gridZ * (double) DOMAIN_SLOT_SPACING);
                }
            }
        }

        return new Vec3((SESSIONS.size() + 1) * (double) DOMAIN_SLOT_SPACING, 0.0D, 0.0D);
    }

    private static long packGrid(int gridX, int gridZ) {
        return ((long) gridX << 32) ^ (gridZ & 0xFFFFFFFFL);
    }

    private static double square(double value) {
        return value * value;
    }

    public record BattleBuildState(Vec3 center, boolean managedTransferredPocket) {
    }

    private static final class Session {
        private final UUID ownerId;
        private final ResourceKey<Level> originDimension;
        private Vec3 originCenter;
        private final Vec3 pocketCenter;
        private double radius;
        @Nullable
        private Object chunkLoader;
        @Nullable
        private UUID buildMirrorId;
        @Nullable
        private UUID originMirrorId;
        private boolean transferred;

        private Session(
            UUID ownerId,
            ResourceKey<Level> originDimension,
            Vec3 originCenter,
            Vec3 pocketCenter,
            double radius,
            @Nullable Object chunkLoader
        ) {
            this.ownerId = ownerId;
            this.originDimension = originDimension;
            this.originCenter = originCenter;
            this.pocketCenter = pocketCenter;
            this.radius = radius;
            this.chunkLoader = chunkLoader;
        }
    }
}
