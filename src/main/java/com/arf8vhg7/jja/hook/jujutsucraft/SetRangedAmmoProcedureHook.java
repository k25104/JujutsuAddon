package com.arf8vhg7.jja.hook.jujutsucraft;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftDataAccess;
import com.arf8vhg7.jja.feature.combat.zone.ZoneEffectOverrides;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class SetRangedAmmoProcedureHook {
    private SetRangedAmmoProcedureHook() {
    }

    public static MobEffectInstance adjustEffectInstance(Entity source, MobEffectInstance effectInstance) {
        return ZoneEffectOverrides.adjustRangedStrengthEffect(source, effectInstance);
    }

    public static boolean shouldMarkManualTechniqueAttack(Entity source, Entity spawnedEntity, boolean sourceHasCursedTechnique) {
        return sourceHasCursedTechnique
            && JjaJujutsucraftDataAccess.jjaGetCurrentSkillValue(source) >= 100.0D
            && spawnedEntity != null;
    }

    static boolean shouldMarkManualTechniqueAttack(boolean sourceHasCursedTechnique, double sourceSkillValue) {
        return sourceHasCursedTechnique && sourceSkillValue >= 100.0D;
    }

    public static void propagateManualTechniqueAttack(Entity source, Entity spawnedEntity) {
        JjaJujutsucraftDataAccess.jjaSetManualTechniqueAttack(
            spawnedEntity,
            shouldMarkManualTechniqueAttack(
                source,
                spawnedEntity,
                source instanceof LivingEntity livingEntity && livingEntity.hasEffect(JujutsucraftModMobEffects.CURSED_TECHNIQUE.get())
            )
        );
    }
}
