package com.arf8vhg7.jja.feature.jja.technique.family.gojo;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.rct.RctAdvancementHelper;
import com.arf8vhg7.jja.feature.jja.technique.shared.effect.JjaMobEffects;
import com.arf8vhg7.jja.feature.player.mobility.fly.FlyEffectGrantRules;
import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeAdvancementHelper;
import com.arf8vhg7.jja.feature.player.progression.grade.SorcererGradeTier;
import com.arf8vhg7.jja.util.JjaAdvancementHelper;
import java.util.Objects;
import java.util.function.DoublePredicate;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.mcreator.jujutsucraft.procedures.LogicStartPassiveProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class GojoProgressionService {
    public static final ResourceLocation LEVITATION_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath("jja", "skill_gojo_levitation");
    public static final ResourceLocation TELEPORT_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath("jja", "skill_gojo_teleport");

    private static final ResourceLocation SIX_EYES_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath("jujutsucraft", "advancement_six_eyes");
    private static final double GOJO_TECHNIQUE_ID = 2.0D;
    private static final int GOJO_TELEPORT_SKILL_ID = GojoTechniqueSelectionService.TELEPORT_SKILL;
    private static final int MAX_TELEPORT_CHARGE_TICKS = 20;
    private static final int TELEPORT_COOLDOWN_TICKS = 40;
    private static final double TELEPORT_DISTANCE_PER_TICK = 20.0D;
    private static final double TELEPORT_BACK_OFFSET = 2.0D;
    private static final double TELEPORT_BACKTRACK_STEP = 0.25D;
    private static final double TELEPORT_LOADED_SCAN_STEP = 1.0D;
    private static final double MIN_VECTOR_LENGTH_SQR = 1.0E-6D;
    private static final String KEY_TELEPORT_CHARGE_TICKS = "jjaGojoTeleportChargeTicks";

    private GojoProgressionService() {
    }

    public static void tick(@Nullable Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        JujutsucraftModVariables.PlayerVariables playerVariables = getPlayerVariables(player);
        if (playerVariables == null) {
            return;
        }

        awardUnlocks(player, playerVariables);
        applyLevitation(player, playerVariables);
        tickTeleportCharge(player, playerVariables);
    }

    public static boolean tryHandleTeleportTechnique(LevelAccessor world, double x, double y, double z, @Nullable Entity entity) {
        if (entity == null || JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(entity) != GOJO_TELEPORT_SKILL_ID) {
            return false;
        }
        if (entity.level().isClientSide()) {
            return true;
        }
        if (!(entity instanceof ServerPlayer player)) {
            clearTeleportTechnique(entity);
            return true;
        }

        JujutsucraftModVariables.PlayerVariables playerVariables = getPlayerVariables(player);
        if (!canUseTeleport(player, playerVariables)) {
            clearTeleportTechnique(player);
        }
        return true;
    }

    public static void onTeleportKeyReleased(@Nullable Entity entity) {
        if (JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(entity) != GOJO_TELEPORT_SKILL_ID) {
            return;
        }
        if (!(entity instanceof ServerPlayer player)) {
            clearTeleportTechnique(entity);
            return;
        }

        JujutsucraftModVariables.PlayerVariables playerVariables = getPlayerVariables(player);
        if (playerVariables == null) {
            clearTeleportTechnique(player);
            return;
        }

        awardUnlocks(player, playerVariables);
        if (!shouldPerformTeleportOnRelease(
            JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(player),
            getTeleportChargeTicks(player),
            canUseTeleport(player, playerVariables)
        )) {
            clearTeleportTechnique(player);
            return;
        }

        performTeleport(player, getTeleportChargeTicks(player));
        clearTeleportTechnique(player);
    }

    static boolean isGojoTechnique(double playerCt1, double playerCt2) {
        return isTechnique(playerCt1, playerCt2, GOJO_TECHNIQUE_ID);
    }

    static boolean shouldAwardLevitation(boolean gojoTechnique, boolean hasSixEyes, boolean hasGradeOneOrHigher) {
        return gojoTechnique && hasSixEyes && hasGradeOneOrHigher;
    }

    static boolean shouldAwardTeleport(boolean gojoTechnique, boolean hasSixEyes, boolean hasSpecialGradeOrHigher, boolean hasRct1) {
        return gojoTechnique && hasSixEyes && hasSpecialGradeOrHigher && hasRct1;
    }

    static boolean shouldApplyLevitation(boolean gojoTechnique, boolean unlocked, boolean passiveActive) {
        return gojoTechnique && unlocked && passiveActive;
    }

    static boolean shouldAllowTeleport(boolean gojoTechnique, boolean unlocked, boolean passiveActive, boolean cooldownActive) {
        return gojoTechnique && unlocked && passiveActive && !cooldownActive;
    }

    static boolean shouldChargeTeleport(boolean gojoTechnique, boolean unlocked, boolean passiveActive, boolean pressZ) {
        return gojoTechnique && unlocked && passiveActive && pressZ;
    }

    static boolean shouldKeepTeleportCharge(int currentSkillId, boolean canUseTeleport) {
        return currentSkillId == GOJO_TELEPORT_SKILL_ID && canUseTeleport;
    }

    static boolean shouldPerformTeleportOnRelease(int currentSkillId, int chargeTicks, boolean canUseTeleport) {
        return currentSkillId == GOJO_TELEPORT_SKILL_ID && chargeTicks > 0 && canUseTeleport;
    }

    static int clampTeleportChargeTicks(int chargeTicks) {
        return Math.max(0, Math.min(chargeTicks, MAX_TELEPORT_CHARGE_TICKS));
    }

    static double resolveTeleportScanDistance(int chargeTicks) {
        return clampTeleportChargeTicks(chargeTicks) * TELEPORT_DISTANCE_PER_TICK;
    }

    static double resolveTeleportBackoffDistance(double hitDistance) {
        return hitDistance - TELEPORT_BACK_OFFSET;
    }

    static double resolveSafeTeleportDistance(double startDistance, double minimumDistance, DoublePredicate isSafeDistance) {
        for (double distance = startDistance; distance >= minimumDistance; distance -= TELEPORT_BACKTRACK_STEP) {
            if (isSafeDistance.test(distance)) {
                return distance;
            }
        }
        return Double.NaN;
    }

    static boolean hasSixEyes(ServerPlayer player) {
        if (player == null) {
            return false;
        }

        return JujutsucraftModMobEffects.SIX_EYES.get() != null && player.hasEffect(JujutsucraftModMobEffects.SIX_EYES.get())
            || JjaAdvancementHelper.has(player, SIX_EYES_ADVANCEMENT_ID);
    }

    static boolean hasGradeOneOrHigher(@Nullable ServerPlayer player) {
        return hasAtLeast(player, SorcererGradeTier.GRADE_1);
    }

    static boolean hasSpecialGradeOrHigher(@Nullable ServerPlayer player) {
        return hasAtLeast(player, SorcererGradeTier.SPECIAL);
    }

    static boolean hasRct1(@Nullable ServerPlayer player) {
        return player != null && JjaAdvancementHelper.has(player, RctAdvancementHelper.RCT_1_ID);
    }

    static boolean hasLevitationUnlock(@Nullable ServerPlayer player) {
        return player != null && JjaAdvancementHelper.has(player, LEVITATION_ADVANCEMENT_ID);
    }

    static boolean hasTeleportUnlock(@Nullable ServerPlayer player) {
        return player != null && JjaAdvancementHelper.has(player, TELEPORT_ADVANCEMENT_ID);
    }

    static boolean hasTeleportUnlock(@Nullable Entity entity) {
        return entity instanceof ServerPlayer player && hasTeleportUnlock(player);
    }

    static boolean isGojoTechnique(@Nullable JujutsucraftModVariables.PlayerVariables playerVariables) {
        return playerVariables != null && isGojoTechnique(playerVariables.PlayerCurseTechnique, playerVariables.PlayerCurseTechnique2);
    }

    static boolean shouldAwardLevitation(@Nullable ServerPlayer player, @Nullable JujutsucraftModVariables.PlayerVariables playerVariables) {
        boolean gojoTechnique = isGojoTechnique(playerVariables);
        return player != null && shouldAwardLevitation(gojoTechnique, hasSixEyes(player), hasGradeOneOrHigher(player));
    }

    static boolean shouldAwardTeleport(@Nullable ServerPlayer player, @Nullable JujutsucraftModVariables.PlayerVariables playerVariables) {
        boolean gojoTechnique = isGojoTechnique(playerVariables);
        return player != null && shouldAwardTeleport(gojoTechnique, hasSixEyes(player), hasSpecialGradeOrHigher(player), hasRct1(player));
    }

    static boolean canUseLevitation(@Nullable ServerPlayer player, @Nullable JujutsucraftModVariables.PlayerVariables playerVariables) {
        return player != null
            && isGojoTechnique(playerVariables)
            && hasLevitationUnlock(player)
            && LogicStartPassiveProcedure.execute(player);
    }

    static boolean canUseTeleport(@Nullable ServerPlayer player, @Nullable JujutsucraftModVariables.PlayerVariables playerVariables) {
        return player != null && shouldAllowTeleport(
            isGojoTechnique(playerVariables),
            hasTeleportUnlock(player),
            LogicStartPassiveProcedure.execute(player),
            hasTeleportCooldown(player)
        );
    }

    static int getTeleportChargeTicks(@Nullable Entity entity) {
        return entity == null ? 0 : entity.getPersistentData().getInt(KEY_TELEPORT_CHARGE_TICKS);
    }

    static void setTeleportChargeTicks(@Nullable Entity entity, int ticks) {
        if (entity != null) {
            entity.getPersistentData().putInt(KEY_TELEPORT_CHARGE_TICKS, Math.max(0, ticks));
        }
    }

    private static void awardUnlocks(ServerPlayer player, JujutsucraftModVariables.PlayerVariables playerVariables) {
        if (shouldAwardLevitation(player, playerVariables)) {
            JjaAdvancementHelper.award(player, LEVITATION_ADVANCEMENT_ID);
        }
        if (shouldAwardTeleport(player, playerVariables)) {
            JjaAdvancementHelper.award(player, TELEPORT_ADVANCEMENT_ID);
        }
    }

    private static void applyLevitation(ServerPlayer player, JujutsucraftModVariables.PlayerVariables playerVariables) {
        if (!canUseLevitation(player, playerVariables)) {
            return;
        }
        FlyEffectGrantRules.applyGroundedFlyEffect(player, FlyEffectGrantRules.PASSIVE_FLY_INCREMENT);
    }

    private static void tickTeleportCharge(ServerPlayer player, JujutsucraftModVariables.PlayerVariables playerVariables) {
        boolean pressZ = player.getPersistentData().getBoolean("PRESS_Z");
        boolean canUseTeleport = canUseTeleport(player, playerVariables);
        int currentSkillId = JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(player);
        if (!shouldKeepTeleportCharge(currentSkillId, canUseTeleport)) {
            clearTeleportCharge(player);
            return;
        }
        if (shouldChargeTeleport(true, true, true, pressZ)) {
            setTeleportChargeTicks(player, getTeleportChargeTicks(player) + 1);
            return;
        }
        if (!pressZ && getTeleportChargeTicks(player) > 0) {
            clearTeleportCharge(player);
        }
    }

    private static boolean performTeleport(ServerPlayer player, int chargeTicks) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }

        Vec3 direction = Objects.requireNonNull(player.getViewVector(1.0F));
        if (direction.lengthSqr() <= MIN_VECTOR_LENGTH_SQR) {
            return false;
        }

        Vec3 normalizedDirection = direction.normalize();
        Vec3 eyeOrigin = player.getEyePosition(1.0F);
        double maxDistance = resolveTeleportScanDistance(chargeTicks);
        if (maxDistance <= 0.0D) {
            return false;
        }

        Vec3 teleportTarget = resolveTeleportTarget(level, player, eyeOrigin, normalizedDirection, maxDistance);
        if (teleportTarget == null) {
            return false;
        }

        player.setDeltaMovement(Objects.requireNonNull(Vec3.ZERO));
        player.fallDistance = 0.0F;
        player.hasImpulse = true;
        player.teleportTo(level, teleportTarget.x, teleportTarget.y, teleportTarget.z, player.getYRot(), player.getXRot());
        applyTeleportCooldown(player);
        return true;
    }

    private static void clearTeleportTechnique(@Nullable Entity entity) {
        clearTeleportCharge(entity);
        if (JjaJujutsucraftDataAccess.jjaGetCurrentSkillId(entity) != GOJO_TELEPORT_SKILL_ID) {
            return;
        }
        JjaJujutsucraftDataAccess.jjaSetCurrentSkillValue(entity, 0.0D);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.removeEffect((MobEffect) JujutsucraftModMobEffects.CURSED_TECHNIQUE.get());
        }
    }

    @Nullable
    static Vec3 resolveTeleportTarget(ServerLevel level, ServerPlayer player, Vec3 eyeOrigin, Vec3 direction, double maxDistance) {
        double loadedDistance = resolveLoadedTeleportDistance(level, eyeOrigin, direction, maxDistance);
        if (loadedDistance <= 0.0D) {
            return null;
        }
        Vec3 clipEnd = new Vec3(
            eyeOrigin.x + direction.x * loadedDistance,
            eyeOrigin.y + direction.y * loadedDistance,
            eyeOrigin.z + direction.z * loadedDistance
        );
        Vec3 feetOrigin = player.position();
        BlockHitResult hitResult = level.clip(
            new ClipContext(
                eyeOrigin,
                clipEnd,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
            )
        );
        Vec3 hitLocation = hitResult.getLocation();
        double hitDistance = hitResult.getType() == HitResult.Type.BLOCK
            ? Math.sqrt(
                Math.pow(hitLocation.x - eyeOrigin.x, 2.0D)
                    + Math.pow(hitLocation.y - eyeOrigin.y, 2.0D)
                    + Math.pow(hitLocation.z - eyeOrigin.z, 2.0D)
            )
            : loadedDistance;
        double startDistance = resolveTeleportBackoffDistance(hitDistance);
        double minimumDistance = hitResult.getType() == HitResult.Type.BLOCK ? Math.min(0.0D, startDistance - 4.0D) : 0.0D;
        double safeDistance = resolveSafeTeleportDistance(
            startDistance,
            minimumDistance,
            candidateDistance -> isSafeTeleportPosition(
                level,
                player,
                new Vec3(
                    feetOrigin.x + direction.x * candidateDistance,
                    feetOrigin.y + direction.y * candidateDistance,
                    feetOrigin.z + direction.z * candidateDistance
                )
            )
        );
        if (Double.isNaN(safeDistance)) {
            return null;
        }
        return new Vec3(
            feetOrigin.x + direction.x * safeDistance,
            feetOrigin.y + direction.y * safeDistance,
            feetOrigin.z + direction.z * safeDistance
        );
    }

    static boolean isSafeTeleportPosition(ServerLevel level, ServerPlayer player, Vec3 targetFeetPosition) {
        if (!isChunkLoaded(level, targetFeetPosition)) {
            return false;
        }
        AABB boundingBox = player.getBoundingBox();
        Vec3 playerPosition = player.position();
        Vec3 movement = new Vec3(
            targetFeetPosition.x - playerPosition.x,
            targetFeetPosition.y - playerPosition.y,
            targetFeetPosition.z - playerPosition.z
        );
        AABB movedBoundingBox = boundingBox.move(movement);
        return level.noCollision(player, Objects.requireNonNull(movedBoundingBox));
    }

    static double resolveLoadedTeleportDistance(ServerLevel level, Vec3 eyeOrigin, Vec3 direction, double maxDistance) {
        return resolveLoadedTeleportDistance(
            maxDistance,
            distance -> isChunkLoaded(level, new Vec3(
                eyeOrigin.x + direction.x * distance,
                eyeOrigin.y + direction.y * distance,
                eyeOrigin.z + direction.z * distance
            ))
        );
    }

    static double resolveLoadedTeleportDistance(double maxDistance, DoublePredicate isLoadedDistance) {
        if (maxDistance <= 0.0D) {
            return 0.0D;
        }
        double lastLoadedDistance = 0.0D;
        for (double distance = TELEPORT_LOADED_SCAN_STEP; distance < maxDistance; distance += TELEPORT_LOADED_SCAN_STEP) {
            if (!isLoadedDistance.test(distance)) {
                return lastLoadedDistance;
            }
            lastLoadedDistance = distance;
        }
        return isLoadedDistance.test(maxDistance) ? maxDistance : lastLoadedDistance;
    }

    private static boolean hasAtLeast(@Nullable ServerPlayer player, SorcererGradeTier minimumTier) {
        if (player == null || minimumTier == null) {
            return false;
        }
        SorcererGradeTier currentTier = SorcererGradeAdvancementHelper.findHighestTier(player);
        return currentTier != null && currentTier.rank() >= minimumTier.rank();
    }

    private static void clearTeleportCharge(@Nullable Entity entity) {
        setTeleportChargeTicks(entity, 0);
    }

    private static boolean hasTeleportCooldown(@Nullable Entity entity) {
        return entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(JjaMobEffects.COOLDOWN_TIME_TELEPORT.get());
    }

    private static boolean isChunkLoaded(ServerLevel level, Vec3 position) {
        BlockPos blockPos = BlockPos.containing(position.x, level.getMinBuildHeight(), position.z);
        int chunkX = blockPos.getX() >> 4;
        int chunkZ = blockPos.getZ() >> 4;
        return level.getChunkSource().hasChunk(chunkX, chunkZ);
    }

    private static void applyTeleportCooldown(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(JjaMobEffects.COOLDOWN_TIME_TELEPORT.get(), TELEPORT_COOLDOWN_TICKS, 0, false, true, true));
    }

    @Nullable
    private static JujutsucraftModVariables.PlayerVariables getPlayerVariables(ServerPlayer player) {
        return JjaJujutsucraftCompat.jjaGetPlayerVariables(player);
    }

    private static boolean isTechnique(double playerCt1, double playerCt2, double techniqueId) {
        return Math.round(playerCt1) == Math.round(techniqueId) || Math.round(playerCt2) == Math.round(techniqueId);
    }
}
