package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.feature.jja.technique.shared.summon.SummonEnhancementService;
import java.util.Objects;
import net.minecraft.world.entity.Entity;

public final class Test1ProcedureHook {
    private Test1ProcedureHook() {
    }

    public static void onGetoCursedSpiritRecovered(Entity sourceEntity, Entity target) {
        if (sourceEntity == null || target == null) {
            return;
        }
        String sourceUuid = Objects.requireNonNull(sourceEntity.getStringUUID());
        target.getPersistentData().putString("OWNER_UUID", sourceUuid);
        if (!shouldClearRecoveredEnhancement(sourceEntity, target)) {
            return;
        }
        SummonEnhancementService.clearEnhancement(target);
    }

    static boolean shouldClearRecoveredEnhancement(Entity sourceEntity, Entity target) {
        if (sourceEntity == null || target == null) {
            return false;
        }
        return shouldClearRecoveredEnhancement(
            sourceEntity.getStringUUID(),
            JjaJujutsucraftDataAccess.jjaGetOwnerUuid(target),
            JjaJujutsucraftDataAccess.jjaGetFriendNum(sourceEntity),
            JjaJujutsucraftDataAccess.jjaGetFriendNumWorker(target)
        );
    }

    static boolean shouldClearRecoveredEnhancement(String sourceUuid, String ownerUuid, double sourceFriendNum, double targetFriendNumWorker) {
        return sourceUuid != null
            && !sourceUuid.isBlank()
            && sourceUuid.equals(ownerUuid)
            && sourceFriendNum != 0.0D
            && sourceFriendNum == targetFriendNumWorker;
    }
}
