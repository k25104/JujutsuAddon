package com.arf8vhg7.jja.feature.jja.technique.family.kashimo;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import java.util.Optional;
import net.mcreator.jujutsucraft.entity.EntityItemEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.EntityItemRightClickedOnEntityProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public final class KashimoNyoiStaffRecallService {
    static final double DEFAULT_ENTITY_REACH = 3.0D;

    private KashimoNyoiStaffRecallService() {
    }

    public static boolean tryHandle(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) {
            return false;
        }

        RecallResolution resolution = resolveActivation(JjaJujutsucraftDataAccess.jjaGetCurrentSkillValue(entity));
        if (!resolution.handled()) {
            return false;
        }
        if (entity.level().isClientSide()) {
            return true;
        }

        if (entity instanceof Player player) {
            Entity target = findRecallTarget(player);
            if (target != null) {
                EntityItemRightClickedOnEntityProcedure.execute(world, target, player);
            }
        }

        JjaJujutsucraftDataAccess.jjaSetCurrentSkillValue(entity, resolution.nextSkill());
        if (resolution.removeTechniqueEffect() && entity instanceof LivingEntity livingEntity) {
            livingEntity.removeEffect((MobEffect) JujutsucraftModMobEffects.CURSED_TECHNIQUE.get());
        }
        return true;
    }

    static RecallResolution resolveActivation(double currentSkill) {
        if ((int) Math.round(currentSkill) != KashimoTechniqueSelectionService.NYOI_STAFF_RECALL_SKILL) {
            return RecallResolution.none();
        }
        return new RecallResolution(true, 0.0D, true);
    }

    static boolean isOwnerAllowed(String ownerUuid, String playerUuid, boolean creative) {
        return creative || ownerUuid == null || ownerUuid.isEmpty() || ownerUuid.equals(playerUuid);
    }

    static boolean isEligiblePlacedNyoiStaff(boolean entityItem, boolean domainDecoration, boolean nyoiStaff, boolean ownerAllowed) {
        return entityItem && !domainDecoration && nyoiStaff && ownerAllowed;
    }

    static boolean shouldReplaceBestHit(double hitDistanceSqr, double blockHitDistanceSqr, double currentBestDistanceSqr) {
        return hitDistanceSqr <= blockHitDistanceSqr && hitDistanceSqr < currentBestDistanceSqr;
    }

    private static Entity findRecallTarget(Player player) {
        double reach = resolveEntityReach(player);
        Vec3 eyePosition = player.getEyePosition();
        Vec3 viewVector = player.getViewVector(1.0F);
        Vec3 endPosition = eyePosition.add(viewVector.scale(reach));
        double blockHitDistanceSqr = resolveBlockHitDistanceSqr(player, eyePosition, endPosition);
        AABB searchBox = player.getBoundingBox().expandTowards(viewVector.scale(reach)).inflate(1.0D);

        Entity bestTarget = null;
        double bestHitDistanceSqr = Double.POSITIVE_INFINITY;
        for (Entity candidate : player.level().getEntities(player, searchBox, target -> isEligiblePlacedNyoiStaff(target, player))) {
            double hitDistanceSqr = resolveHitDistanceSqr(candidate, eyePosition, endPosition);
            if (!Double.isFinite(hitDistanceSqr) || !shouldReplaceBestHit(hitDistanceSqr, blockHitDistanceSqr, bestHitDistanceSqr)) {
                continue;
            }
            bestTarget = candidate;
            bestHitDistanceSqr = hitDistanceSqr;
        }
        return bestTarget;
    }

    private static double resolveEntityReach(Player player) {
        double reach = DEFAULT_ENTITY_REACH;
        AttributeInstance reachAttribute = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (reachAttribute != null) {
            reach = reachAttribute.getValue();
        }
        return reach;
    }

    private static double resolveBlockHitDistanceSqr(Player player, Vec3 eyePosition, Vec3 endPosition) {
        HitResult blockHit = player.level().clip(new ClipContext(eyePosition, endPosition, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (blockHit.getType() == HitResult.Type.MISS) {
            return Double.POSITIVE_INFINITY;
        }
        return eyePosition.distanceToSqr(blockHit.getLocation());
    }

    private static double resolveHitDistanceSqr(Entity candidate, Vec3 eyePosition, Vec3 endPosition) {
        AABB hitBox = candidate.getBoundingBox().inflate(candidate.getPickRadius());
        if (hitBox.contains(eyePosition)) {
            return 0.0D;
        }

        Optional<Vec3> hitPosition = hitBox.clip(eyePosition, endPosition);
        return hitPosition.map(vec3 -> eyePosition.distanceToSqr(vec3)).orElse(Double.POSITIVE_INFINITY);
    }

    private static boolean isEligiblePlacedNyoiStaff(Entity candidate, Player player) {
        boolean entityItem = candidate instanceof EntityItemEntity;
        boolean domainDecoration = entityItem && Boolean.TRUE.equals(((EntityItemEntity) candidate).getEntityData().get(EntityItemEntity.DATA_domain_decoration));
        ItemStack headStack = candidate instanceof LivingEntity livingEntity ? livingEntity.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY;
        boolean nyoiStaff = headStack.getItem() == JujutsucraftModItems.NYOI_STAFF.get();
        boolean ownerAllowed = isOwnerAllowed(
            JjaJujutsucraftDataAccess.jjaGetOwnerUuid(candidate),
            player.getStringUUID(),
            player.getAbilities().instabuild
        );
        return isEligiblePlacedNyoiStaff(entityItem, domainDecoration, nyoiStaff, ownerAllowed);
    }

    record RecallResolution(boolean handled, double nextSkill, boolean removeTechniqueEffect) {
        static RecallResolution none() {
            return new RecallResolution(false, 0.0D, false);
        }
    }
}
