package com.arf8vhg7.jja.feature.jja.domain.de;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.feature.jja.traits.twinnedbody.TwinnedBodyCombatPassContext;
import java.util.Objects;
import javax.annotation.Nullable;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public final class DomainInfinityBypassService {
    private DomainInfinityBypassService() {
    }

    public static boolean allowsDomainSureHitInfinityBypass(
        boolean domainAttack,
        boolean attackerIsTechniqueAttack,
        boolean ownerHasActiveDomain,
        boolean ownerFailed,
        boolean targetInsideOwnerDomain
    ) {
        return domainAttack || attackerIsTechniqueAttack && ownerHasActiveDomain && !ownerFailed && targetInsideOwnerDomain;
    }

    public static boolean shouldBypassInfinity(@Nullable LevelAccessor world, @Nullable Entity attacker, @Nullable Entity target) {
        if (world == null || attacker == null || target == null) {
            return false;
        }

        if (TwinnedBodyCombatPassContext.hasContext() && !TwinnedBodyCombatPassContext.matches(attacker, target)) {
            return false;
        }

        Entity owner = JjaJujutsucraftDataAccess.jjaResolveRootLivingOwner(world, attacker);
        if (owner == null) {
            return false;
        }

        return allowsDomainSureHitInfinityBypass(
            attacker.getPersistentData().getBoolean("DomainAttack"),
            isTechniqueAttack(attacker),
            owner instanceof LivingEntity livingEntity && livingEntity.hasEffect(Objects.requireNonNull(JujutsucraftModMobEffects.DOMAIN_EXPANSION.get())),
            owner.getPersistentData().getBoolean("Failed"),
            isTargetInsideOwnerDomain(owner, target)
        );
    }

    private static boolean isTechniqueAttack(Entity attacker) {
        return JjaJujutsucraftDataAccess.jjaIsManualTechniqueAttack(attacker) || isDirectTechniqueAttack(attacker);
    }

    private static boolean isDirectTechniqueAttack(Entity attacker) {
        return attacker.getPersistentData().getBoolean("attack") && JjaJujutsucraftDataAccess.jjaGetCurrentSkillValue(attacker) >= 100.0D;
    }

    private static boolean isTargetInsideOwnerDomain(Entity owner, Entity target) {
        double radius = DomainExpansionHookSupport.resolveCurrentRadius(owner, DomainExpansionConfiguredRadiusSync.getConfiguredRadius());
        if (!(radius > 0.0D)) {
            return false;
        }

        return DomainExpansionContainmentHelper.isWithinOwnerRadius(
            owner,
            target,
            DomainExpansionRadiusRuntime.resolveBarrierEnvelopeRadius(radius)
        );
    }
}
