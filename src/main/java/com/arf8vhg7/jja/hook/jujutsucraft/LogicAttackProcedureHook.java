package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.feature.combat.targeting.AttackNonHostilePolicy;
import com.arf8vhg7.jja.feature.combat.targeting.AttackTargetSelectionRestrictionService;
import com.arf8vhg7.jja.feature.jja.technique.family.kugisaki.KugisakiHairpinTargetingContext;
import com.arf8vhg7.jja.feature.jja.technique.family.ranta.RantaEvilEyeBoundTargetService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public final class LogicAttackProcedureHook {
    private LogicAttackProcedureHook() {
    }

    public static boolean resolveAttackResult(boolean original, LevelAccessor world, Entity entity, Entity entityiterator) {
        if (KugisakiHairpinTargetingContext.shouldRejectCandidate(entity, entityiterator)) {
            return false;
        }

        if (RantaEvilEyeBoundTargetService.shouldRestrictCandidate(entityiterator)) {
            return false;
        }

        Boolean roundDeerRctAttackResult = AttackTargetSelectionRestrictionService.resolveRoundDeerPlayerRctAttackResult(
            world,
            entity,
            entityiterator
        );
        if (roundDeerRctAttackResult != null) {
            return roundDeerRctAttackResult;
        }

        if (AttackTargetSelectionRestrictionService.shouldRestrictTechniqueTarget(world, entity, entityiterator)) {
            return false;
        }

        return AttackNonHostilePolicy.resolve(original, world, entity, entityiterator);
    }
}
