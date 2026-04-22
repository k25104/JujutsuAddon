package com.arf8vhg7.jja.feature.jja.technique.shared.summon;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.feature.player.revive.ReviveFlowService;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.procedures.ShikigamiRightClickedOnEntityProcedure;
import net.mcreator.jujutsucraft.procedures.TenShadowsTechniqueProcedure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class SummonLifecycleService {
    private static final String KEY_SHIKIGAMI = "Shikigami";
    private static final String KEY_AMBUSH = "Ambush";
    private static final int NATURAL_REGEN_INTERVAL_TICKS = 10;
    private static final float NATURAL_REGEN_AMOUNT = 1.0F;

    private SummonLifecycleService() {
    }

    public static void tick(LivingEntity summon) {
        if (summon == null || summon.level().isClientSide() || summon.isRemoved() || !summon.isAlive() || !isManagedSummon(summon)) {
            return;
        }

        ServerPlayer waitingOwner = resolveWaitingOwner(summon);
        if (waitingOwner != null) {
            releaseForWaitingOwner(summon, waitingOwner);
            return;
        }

        if (shouldNaturallyRegenerate(summon)) {
            summon.heal(NATURAL_REGEN_AMOUNT);
        }
    }

    static boolean isManagedSummon(@Nullable Entity entity) {
        return entity != null && (entity.getPersistentData().getBoolean(KEY_SHIKIGAMI) || entity.getPersistentData().getBoolean(KEY_AMBUSH));
    }

    static boolean shouldNaturallyRegenerate(LivingEntity summon) {
        return summon.tickCount > 0
            && summon.tickCount % NATURAL_REGEN_INTERVAL_TICKS == 0
            && summon.getHealth() > 0.0F
            && summon.getHealth() < summon.getMaxHealth();
    }

    @Nullable
    private static ServerPlayer resolveWaitingOwner(Entity summon) {
        Entity owner = JjaJujutsucraftDataAccess.jjaResolveOwnerByUuid(summon.level(), JjaJujutsucraftDataAccess.jjaGetOwnerUuid(summon));
        if (!(owner instanceof ServerPlayer serverPlayer) || !ReviveFlowService.isWaiting(serverPlayer)) {
            return null;
        }
        return serverPlayer;
    }

    private static void releaseForWaitingOwner(Entity summon, Entity owner) {
        if (summon.getPersistentData().getBoolean(KEY_AMBUSH)) {
            recallTenShadowsSummon(summon, owner);
            return;
        }
        releaseGenericShikigami(summon, owner);
    }

    private static void releaseGenericShikigami(Entity summon, Entity owner) {
        withTemporaryCrouch(owner, () -> ShikigamiReleaseKillSuppressionContext.run(
            () -> ShikigamiRightClickedOnEntityProcedure.execute(summon.level(), summon, owner)
        ));
    }

    private static void recallTenShadowsSummon(Entity summon, Entity owner) {
        withTemporaryCrouch(owner, () -> TenShadowsTechniqueProcedure.execute(
            summon.level(),
            summon.getX(),
            summon.getY(),
            summon.getZ(),
            summon,
            owner
        ));
    }

    private static void withTemporaryCrouch(Entity owner, Runnable action) {
        boolean wasCrouching = owner.isShiftKeyDown();
        owner.setShiftKeyDown(true);
        try {
            action.run();
        } finally {
            owner.setShiftKeyDown(wasCrouching);
        }
    }
}
