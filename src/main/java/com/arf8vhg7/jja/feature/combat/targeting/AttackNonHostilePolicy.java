package com.arf8vhg7.jja.feature.combat.targeting;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.config.JjaCommonConfig;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import java.util.List;
import net.mcreator.jujutsucraft.entity.CrowEntity;
import net.mcreator.jujutsucraft.entity.UraumeEntity;
import net.mcreator.jujutsucraft.init.JujutsucraftModGameRules;
import net.mcreator.jujutsucraft.init.JujutsucraftModItems;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.procedures.LogicAttackDomainProcedure;
import net.mcreator.jujutsucraft.procedures.ReturnInsideItemProcedure;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public final class AttackNonHostilePolicy {
    private static final List<TagKey<net.minecraft.world.entity.EntityType<?>>> FRIENDLY_GROUP_TAGS = List.of(
        tag("forge", "group_1"),
        tag("forge", "group_2"),
        tag("forge", "group_3"),
        tag("forge", "group_4"),
        tag("forge", "group_5")
    );
    private static final TagKey<net.minecraft.world.entity.EntityType<?>> NOT_LIVING_TAG = tag("forge", "not_living");
    private static final TagKey<net.minecraft.world.entity.EntityType<?>> RANGED_AMMO_TAG = tag("forge", "ranged_ammo");
    private static final TagKey<net.minecraft.world.entity.EntityType<?>> TEN_SHADOWS_TECHNIQUE_TAG = tag("jujutsucraft", "ten_shadows_technique");

    private AttackNonHostilePolicy() {
    }

    public static boolean resolve(boolean original, LevelAccessor world, Entity entity, Entity target) {
        if (entity == null || target == null) {
            return original;
        }
        AttackEntitySnapshot attacker = AttackEntitySnapshot.capture(entity);
        AttackEntitySnapshot targetSnapshot = AttackEntitySnapshot.capture(target);
        AttackEntitySnapshot attackerOwner = AttackEntitySnapshot.capture(resolveOwner(world, entity));
        AttackEntitySnapshot targetOwner = AttackEntitySnapshot.capture(resolveOwner(world, target));
        if (AttackNonHostileOverrideRules.shouldOverride(
            new AttackNonHostileOverrideRules.OverrideState(
                original,
                JjaCommonConfig.ATTACK_NONHOSTILE.get(),
                hasRelaxableRelation(attackerOwner, attacker, targetOwner, targetSnapshot),
                isCombatTarget(targetSnapshot),
                canAttackIgnoringProfessionAndGroup(world, attacker, targetSnapshot, attackerOwner, targetOwner)
            )
        )) {
            return true;
        }
        return original;
    }

    private static Player resolvePlayerOwnedSummonOwner(LevelAccessor world, Entity attacker) {
        if (attacker == null) {
            return null;
        }
        String ownerUuid = JjaJujutsucraftDataAccess.jjaGetOwnerUuid(attacker);
        if (ownerUuid.isEmpty()) {
            return null;
        }
        Entity owner = resolveOwner(world, attacker);
        if (!(owner instanceof Player playerOwner)) {
            return null;
        }
        double friendNum = JjaJujutsucraftDataAccess.jjaGetFriendNum(attacker);
        double friendNumWorker = JjaJujutsucraftDataAccess.jjaGetFriendNumWorker(attacker);
        double ownerFriendNum = JjaJujutsucraftDataAccess.jjaGetFriendNum(playerOwner);
        return AttackTargetRestrictionRules.isPlayerOwnedWorkerSummon(friendNum, friendNumWorker, ownerFriendNum) ? playerOwner : null;
    }

    private static boolean hasRegisteredAttackTarget(Player owner, java.util.UUID targetId) {
        PlayerRctState rctState = PlayerStateAccess.rct(owner);
        return AttackTargetRestrictionRules.hasRegisteredAttackTarget(rctState, targetId);
    }

    static boolean canAttackIgnoringProfessionAndGroup(
        LevelAccessor world,
        AttackEntitySnapshot attacker,
        AttackEntitySnapshot target,
        AttackEntitySnapshot attackerOwner,
        AttackEntitySnapshot targetOwner
    ) {
        if (attacker.entity() == null || target.entity() == null || attacker.entity() == target.entity()) {
            return false;
        }
        if (!(target.entity() instanceof LivingEntity livingTarget)) {
            return false;
        }
        if (attacker.onlyLiving() && target.notLivingType()) {
            return false;
        }
        if (attacker.onlyRanged() && !target.rangedAmmoType()) {
            return false;
        }
        if (target.removed()) {
            return false;
        }
        if (target.instabuildOrSpectatorPlayer()) {
            return false;
        }
        if ((!attacker.notLivingType() || attacker.vehicle() != target.entity())
            && (!target.notLivingType() || target.vehicle() != attacker.entity())) {
            boolean logicAttack = true;
            boolean playerAttacker = attackerOwner.player();
            boolean playerTarget = targetOwner.player();
            if (playerAttacker && playerTarget && !world.getLevelData().getGameRules().getBoolean(JujutsucraftModGameRules.JUJUTSUPVP)) {
                logicAttack = false;
            }

            if (attackerOwner.hasSukuna() || attacker.hasSukuna() || targetOwner.hasSukuna() || target.hasSukuna()) {
                logicAttack = true;
            }

            if (logicAttack && (isUraumeBlocked(attackerOwner, targetOwner) || isUraumeBlocked(targetOwner, attackerOwner))) {
                logicAttack = false;
            }

            if (!logicAttack) {
                if (playerAttacker && target.mobTarget() instanceof LivingEntity && target.cntTarget() > 6.0 && isPlayerLike(target.mobTarget())) {
                    logicAttack = true;
                }

                if (playerTarget && attacker.mobTarget() instanceof LivingEntity && attacker.cntTarget() > 6.0 && isPlayerLike(attacker.mobTarget())) {
                    logicAttack = true;
                }
            }

            if (attacker.crow() && target.crow()) {
                logicAttack = false;
            }

            if (isTargetingRelation(attacker, target, attackerOwner, targetOwner)) {
                logicAttack = true;
            }

            if (attackerOwner.entity() == targetOwner.entity()) {
                logicAttack = false;
            }

            if ((attacker.tenShadowsTechnique() && !attacker.ambush()) || (target.tenShadowsTechnique() && !target.ambush())) {
                logicAttack = true;
            }

            if (AttackNonHostileSnapshotRules.hasMatchingFriendNum(attacker.friendNum(), target.friendNum())) {
                logicAttack = false;
            }

            double myRanged = attacker.nameRangedRanged();
            double myName = attacker.nameRanged();
            double targetName = target.nameRanged();
            double targetRanged = target.nameRangedRanged();
            if (myRanged != 0.0D) {
                if (myRanged == targetName || myRanged == targetRanged) {
                    logicAttack = false;
                }

                if (attacker.betrayal()) {
                    if (AttackNonHostileSnapshotRules.shouldForceBetrayalAttack(myRanged, targetRanged, attacker.stringUuid(), targetOwner.stringUuid())) {
                        return true;
                    }
                    if (AttackNonHostileSnapshotRules.isSameOwner(targetOwner.stringUuid(), attacker.stringUuid())) {
                        logicAttack = false;
                    }
                }
            }

            if (myName != 0.0D && (myName == targetName || myName == targetRanged)) {
                logicAttack = false;
            }

            if (livingTarget.hasEffect((MobEffect) JujutsucraftModMobEffects.PRAYER_SONG.get())
                && livingTarget.hasEffect((MobEffect) JujutsucraftModMobEffects.GUARD.get())
                && livingTarget.getEffect((MobEffect) JujutsucraftModMobEffects.GUARD.get()) != null
                && livingTarget.getEffect((MobEffect) JujutsucraftModMobEffects.GUARD.get()).getAmplifier() > 0) {
                double skill = attacker.skill();
                if ((skill < 305.0D || skill > 310.0D) && skill != 205.0D && skill != 705.0D) {
                    logicAttack = false;
                }
            }

            if (!logicAttack) {
                return false;
            }
            if (attacker.domainAttack() && !LogicAttackDomainProcedure.execute(world, attacker.entity(), target.entity())) {
                return false;
            }
            return AttackNonHostileSnapshotRules.passesTargetType(
                attacker.targetType(),
                target.entity().getDeltaMovement().lengthSqr(),
                target.entity().onGround()
            );
        }
        return false;
    }

    private static Entity resolveOwner(LevelAccessor world, Entity entity) {
        return JjaJujutsucraftDataAccess.jjaResolveRootLivingOwner(world, entity);
    }

    private static boolean hasRelaxableRelation(
        AttackEntitySnapshot attackerOwner,
        AttackEntitySnapshot attacker,
        AttackEntitySnapshot targetOwner,
        AttackEntitySnapshot target
    ) {
        return AttackNonHostileOverrideRules.hasProfessionRelation(
            attackerOwner.jujutsuSorcerer() || attacker.jujutsuSorcerer(),
            targetOwner.jujutsuSorcerer() || target.jujutsuSorcerer(),
            attackerOwner.cursedSpirit() || attacker.cursedSpirit(),
            targetOwner.cursedSpirit() || target.cursedSpirit(),
            attackerOwner.curseUser() || attacker.curseUser(),
            targetOwner.curseUser() || target.curseUser()
        ) || shareFriendlyGroup(attackerOwner, targetOwner);
    }

    private static boolean isCombatTarget(AttackEntitySnapshot target) {
        return AttackNonHostileOverrideRules.isCombatTarget(target.living(), target.invulnerable(), target.noAiMob(), target.domainEntity());
    }

    private static boolean isUraumeBlocked(AttackEntitySnapshot uraumeCandidate, AttackEntitySnapshot other) {
        if (!uraumeCandidate.uraume()) {
            return false;
        }
        return ReturnInsideItemProcedure.execute(other.entity()).getItem() == JujutsucraftModItems.SUKUNA_FINGER.get() || other.hasSukuna();
    }

    private static boolean shareFriendlyGroup(AttackEntitySnapshot attackerOwner, AttackEntitySnapshot iteratorOwner) {
        for (TagKey<net.minecraft.world.entity.EntityType<?>> tag : FRIENDLY_GROUP_TAGS) {
            if (matchesFriendlyGroup(attackerOwner, tag) && matchesFriendlyGroup(iteratorOwner, tag)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesFriendlyGroup(AttackEntitySnapshot entity, TagKey<net.minecraft.world.entity.EntityType<?>> tag) {
        if (entity.entity() == null) {
            return false;
        }
        if (entity.entity().getType().is(tag)) {
            return true;
        }
        return tag.location().getPath().equals("group_1") && entity.player() && entity.curseUser();
    }

    private static boolean isTargetingRelation(
        AttackEntitySnapshot attacker,
        AttackEntitySnapshot target,
        AttackEntitySnapshot attackerOwner,
        AttackEntitySnapshot iteratorOwner
    ) {
        if (attacker.mobTarget() == target.entity() && attacker.cntTarget() > 6.0D) {
            return true;
        }
        if (target.mobTarget() == attacker.entity() && target.cntTarget() > 6.0D) {
            return true;
        }
        if (attackerOwner.mobTarget() == target.entity() && attackerOwner.cntTarget() > 6.0D) {
            return true;
        }
        if (iteratorOwner.mobTarget() == attacker.entity() && iteratorOwner.cntTarget() > 6.0D) {
            return true;
        }
        if (attackerOwner.mobTarget() == iteratorOwner.entity() && attackerOwner.cntTarget() > 6.0D) {
            return true;
        }
        return iteratorOwner.mobTarget() == attackerOwner.entity() && iteratorOwner.cntTarget() > 6.0D;
    }

    private static boolean isPlayerLike(Entity entity) {
        return entity instanceof Player || (entity != null && entity.getPersistentData().getBoolean("Player"));
    }

    private static TagKey<net.minecraft.world.entity.EntityType<?>> tag(String namespace, String path) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private record AttackEntitySnapshot(
        Entity entity,
        Entity vehicle,
        Entity mobTarget,
        boolean living,
        boolean player,
        boolean instabuildOrSpectatorPlayer,
        boolean noAiMob,
        boolean removed,
        boolean invulnerable,
        boolean notLivingType,
        boolean rangedAmmoType,
        boolean tenShadowsTechnique,
        boolean crow,
        boolean uraume,
        boolean jujutsuSorcerer,
        boolean cursedSpirit,
        boolean curseUser,
        boolean domainEntity,
        boolean domainAttack,
        boolean onlyLiving,
        boolean onlyRanged,
        boolean ambush,
        boolean betrayal,
        boolean hasSukuna,
        double cntTarget,
        double friendNum,
        double targetType,
        double skill,
        double nameRanged,
        double nameRangedRanged,
        String stringUuid
    ) {
        static AttackEntitySnapshot capture(Entity entity) {
            Mob mob = entity instanceof Mob mobEntity ? mobEntity : null;
            return new AttackEntitySnapshot(
                entity,
                entity == null ? null : entity.getVehicle(),
                mob == null ? null : mob.getTarget(),
                entity instanceof LivingEntity,
                entity instanceof Player,
                entity instanceof Player player && (player.getAbilities().instabuild || player.isSpectator()),
                mob != null && mob.isNoAi(),
                entity != null && entity.isRemoved(),
                entity != null && entity.isInvulnerable(),
                entity != null && entity.getType().is(NOT_LIVING_TAG),
                entity != null && entity.getType().is(RANGED_AMMO_TAG),
                entity != null && entity.getType().is(TEN_SHADOWS_TECHNIQUE_TAG),
                entity instanceof CrowEntity,
                entity instanceof UraumeEntity,
                hasMarker(entity, "JujutsuSorcerer"),
                hasMarker(entity, "CursedSpirit"),
                hasMarker(entity, "CurseUser"),
                hasMarker(entity, "domain_entity"),
                hasMarker(entity, "DomainAttack"),
                hasMarker(entity, "onlyLiving"),
                hasMarker(entity, "onlyRanged"),
                hasMarker(entity, "Ambush"),
                hasMarker(entity, "betrayal"),
                entity instanceof LivingEntity living && living.hasEffect((MobEffect) JujutsucraftModMobEffects.SUKUNA_EFFECT.get()),
                getDouble(entity, "cnt_target"),
                getDouble(entity, "friend_num"),
                getDouble(entity, "target_type"),
                getDouble(entity, "skill"),
                getDouble(entity, "NameRanged"),
                getDouble(entity, "NameRanged_ranged"),
                entity == null ? "" : entity.getStringUUID()
            );
        }

        private static boolean hasMarker(Entity entity, String key) {
            return entity != null && entity.getPersistentData().getBoolean(key);
        }

        private static double getDouble(Entity entity, String key) {
            return entity == null ? 0.0D : entity.getPersistentData().getDouble(key);
        }
    }
}
