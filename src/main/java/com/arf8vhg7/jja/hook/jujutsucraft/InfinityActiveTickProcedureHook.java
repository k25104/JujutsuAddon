package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.jja.domain.de.DomainInfinityBypassService;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class InfinityActiveTickProcedureHook {
    private InfinityActiveTickProcedureHook() {
    }

    public static boolean ignoreNeutralizationForInfinity(boolean original) {
        return false;
    }

    public static boolean shouldApplyInfinityStop(boolean shouldBypassDomainSureHit) {
        return !shouldBypassDomainSureHit;
    }

    public static boolean shouldApplyInfinityStop(@Nullable LevelAccessor world, @Nullable Entity infinityHolder, @Nullable Entity candidate) {
        return shouldApplyInfinityStop(shouldBypassInfinityStop(world, infinityHolder, candidate));
    }

    static boolean shouldBypassInfinityStop(
        boolean domainAttack,
        boolean attackerIsTechniqueAttack,
        boolean attackerCanUseHeldItemTechnique,
        boolean ownerHasActiveDomain,
        boolean ownerFailed,
        boolean targetInsideOwnerDomain
    ) {
        return DomainInfinityBypassService.allowsDomainSureHitInfinityBypass(
            domainAttack,
            attackerIsTechniqueAttack,
            ownerHasActiveDomain,
            ownerFailed,
            targetInsideOwnerDomain
        );
    }

    public static boolean shouldBypassInfinityStop(@Nullable LevelAccessor world, @Nullable Entity infinityHolder, @Nullable Entity candidate) {
        return DomainInfinityBypassService.shouldBypassInfinity(world, candidate, infinityHolder);
    }

    public static int modifyDrainInterval(int original) {
        return 1;
    }

    public static int modifyDrainOffset(int original) {
        return 0;
    }

    public static double modifyCursePowerDrain(double original) {
        return 0.5;
    }

    public static boolean disableSixEyesFreeDrain(boolean original) {
        return false;
    }
}
