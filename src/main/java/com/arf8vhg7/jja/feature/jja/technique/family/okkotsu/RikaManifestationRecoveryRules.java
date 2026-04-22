package com.arf8vhg7.jja.feature.jja.technique.family.okkotsu;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.entity.Rika2Entity;
import net.mcreator.jujutsucraft.entity.RikaEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class RikaManifestationRecoveryRules {
    private RikaManifestationRecoveryRules() {
    }

    public static double modifyHealCursePower(@Nullable Entity entity, double healCursePower) {
        if (!(entity instanceof Player player) || !(player.level() instanceof ServerLevel serverLevel)) {
            return healCursePower;
        }
        int ownedRikaCount = countOwnedRika(serverLevel, player);
        return healCursePower * resolveHealCursePowerMultiplier(ownedRikaCount);
    }

    static double resolveHealCursePowerMultiplier(int ownedRikaCount) {
        return 1.0D + Math.max(ownedRikaCount, 0);
    }

    static boolean shouldCountOwnedRikaCandidate(
        boolean rikaEntity,
        boolean alive,
        boolean removed,
        boolean domainEntity,
        boolean ownerMatches,
        boolean friendNumMatches
    ) {
        return rikaEntity && alive && !removed && !domainEntity && ownerMatches && friendNumMatches;
    }

    private static int countOwnedRika(ServerLevel serverLevel, Player owner) {
        String ownerUuid = owner.getStringUUID();
        double ownerFriendNum = JjaJujutsucraftDataAccess.jjaGetFriendNum(owner);
        int ownedRikaCount = 0;
        for (ServerLevel level : serverLevel.getServer().getAllLevels()) {
            for (Entity candidate : level.getAllEntities()) {
                if (!shouldCountOwnedRikaCandidate(
                    isRikaEntity(candidate),
                    candidate.isAlive(),
                    candidate.isRemoved(),
                    candidate.getPersistentData().getBoolean("domain_entity"),
                    ownerUuid.equals(JjaJujutsucraftDataAccess.jjaGetOwnerUuid(candidate)),
                    Double.compare(ownerFriendNum, JjaJujutsucraftDataAccess.jjaGetFriendNumWorker(candidate)) == 0
                )) {
                    continue;
                }
                ownedRikaCount++;
            }
        }
        return ownedRikaCount;
    }

    private static boolean isRikaEntity(Entity entity) {
        return entity instanceof RikaEntity || entity instanceof Rika2Entity;
    }
}
