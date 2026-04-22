package com.arf8vhg7.jja.feature.combat.targeting;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerRctState;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.entity.RoundDeerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public final class AttackTargetSelectionRestrictionService {
    private AttackTargetSelectionRestrictionService() {
    }

    public static boolean allowCurrentTarget(LevelAccessor world, Entity entity, boolean original) {
        if (world == null || !(entity instanceof Mob mob)) {
            return original;
        }
        Entity target = mob.getTarget();
        if (shouldAllowRoundDeerRecoveryTarget(world, entity, target)) {
            return true;
        }
        if (!original) {
            return false;
        }
        if (!shouldRestrictCurrentTarget(world, entity, target)) {
            return original;
        }
        clearCurrentTarget(mob);
        return false;
    }

    public static boolean shouldRestrictCurrentTarget(LevelAccessor world, Entity attacker, Entity target) {
        if (!(target instanceof Player playerTarget)) {
            return false;
        }
        Player owner = resolvePlayerOwnedSummonOwner(world, attacker);
        if (owner == null) {
            return false;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(owner);
        if (attacker instanceof RoundDeerEntity) {
            return AttackTargetSelectionRestrictionRules.shouldRestrictRoundDeerRecoveryTarget(
                true,
                true,
                rctState,
                playerTarget.getUUID(),
                playerTarget.getPersistentData().getBoolean("CursedSpirit")
            );
        }
        return AttackTargetSelectionRestrictionRules.shouldRestrictPlayerTarget(
            true,
            true,
            rctState,
            playerTarget.getUUID()
        );
    }

    @Nullable
    public static Boolean resolveRoundDeerPlayerRctAttackResult(LevelAccessor world, Entity attacker, Entity target) {
        if (!(attacker instanceof RoundDeerEntity) || !(target instanceof Player playerTarget)) {
            return null;
        }
        Player owner = resolvePlayerOwnedSummonOwner(world, attacker);
        if (owner == null) {
            return null;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(owner);
        return AttackTargetSelectionRestrictionRules.shouldRoundDeerTreatPlayerAsAttackTarget(
            true,
            true,
            rctState,
            playerTarget.getUUID()
        );
    }

    public static boolean hasRegisteredAttackTarget(LevelAccessor world, Entity source, Entity target) {
        if (world == null || source == null || target == null) {
            return false;
        }

        Player owner = resolveAttackTargetOwner(world, source);
        if (owner == null) {
            return false;
        }

        PlayerRctState rctState = PlayerStateAccess.rct(owner);
        return AttackTargetRestrictionRules.hasRegisteredAttackTarget(rctState, target.getUUID());
    }

    public static boolean shouldRestrictTechniqueTarget(LevelAccessor world, Entity source, Entity target) {
        if (world == null || source == null || target == null || !JjaAttackTargetSelectionContextService.jjaIsActive()) {
            return false;
        }

        Player techniqueOwner = resolveTechniqueOwner(world, source);
        if (target instanceof Player playerTarget) {
            return techniqueOwner != null && !hasRegisteredTechniqueAttackTarget(techniqueOwner, playerTarget.getUUID());
        }

        if (target instanceof Monster) {
            return false;
        }

        Entity hostileAnchor = techniqueOwner != null ? techniqueOwner : source;
        if (target instanceof Mob mobTarget) {
            Entity mobTargetFocus = mobTarget.getTarget();
            if (mobTargetFocus == source || mobTargetFocus == hostileAnchor) {
                return false;
            }
        }

        return true;
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

    @Nullable
    private static Player resolveAttackTargetOwner(LevelAccessor world, Entity source) {
        if (source instanceof Player playerSource) {
            return playerSource;
        }
        return resolvePlayerOwnedSummonOwner(world, source);
    }

    private static Entity resolveOwner(LevelAccessor world, Entity entity) {
        return JjaJujutsucraftDataAccess.jjaResolveRootOwner(world, entity);
    }

    private static boolean shouldAllowRoundDeerRecoveryTarget(LevelAccessor world, Entity attacker, Entity target) {
        if (!(attacker instanceof RoundDeerEntity) || !(target instanceof Player playerTarget)) {
            return false;
        }
        Player owner = resolvePlayerOwnedSummonOwner(world, attacker);
        if (owner == null) {
            return false;
        }
        PlayerRctState rctState = PlayerStateAccess.rct(owner);
        return !AttackTargetSelectionRestrictionRules.shouldRestrictRoundDeerRecoveryTarget(
            true,
            true,
            rctState,
            playerTarget.getUUID(),
            playerTarget.getPersistentData().getBoolean("CursedSpirit")
        );
    }

    @Nullable
    private static Player resolveTechniqueOwner(LevelAccessor world, Entity source) {
        Entity owner = JjaJujutsucraftDataAccess.jjaResolveRootLivingOwner(world, source);
        return owner instanceof Player playerOwner ? playerOwner : null;
    }

    private static boolean hasRegisteredTechniqueAttackTarget(Player owner, java.util.UUID targetId) {
        PlayerRctState rctState = PlayerStateAccess.rct(owner);
        return AttackTargetRestrictionRules.hasRegisteredAttackTarget(rctState, targetId);
    }

    private static void clearCurrentTarget(Mob mob) {
        JjaJujutsucraftDataAccess.jjaClearMobTarget(mob);
    }
}
