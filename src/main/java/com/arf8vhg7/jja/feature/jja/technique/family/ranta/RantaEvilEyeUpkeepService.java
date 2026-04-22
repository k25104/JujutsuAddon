package com.arf8vhg7.jja.feature.jja.technique.family.ranta;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class RantaEvilEyeUpkeepService {
    private static final double TICK_UPKEEP = 1.0D;

    private RantaEvilEyeUpkeepService() {
    }

    public static void tick(@Nullable Entity eyeEntity) {
        if (eyeEntity == null || eyeEntity.level().isClientSide()) {
            return;
        }

        Entity owner = JjaJujutsucraftDataAccess.jjaResolveDirectOwner(eyeEntity.level(), eyeEntity);
        if (!shouldDrain(false, owner instanceof ServerPlayer)) {
            return;
        }

        ServerPlayer player = (ServerPlayer) owner;
        JjaCursePowerAccountingService.queueSpentPower(JjaJujutsucraftCompat.jjaGetPlayerVariables(player), TICK_UPKEEP);
    }

    static boolean shouldDrain(boolean clientSide, boolean ownerIsServerPlayer) {
        return !clientSide && ownerIsServerPlayer;
    }
}
