package com.arf8vhg7.jja.feature.jja.domain.sd;

import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.resource.ce.JjaCursePowerAccountingService;
import net.mcreator.jujutsucraft.init.JujutsucraftModMobEffects;
import net.mcreator.jujutsucraft.network.JujutsucraftModVariables;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

final class AntiDomainEffectService {
    private static final double HOLD_EXTENSION_COST = 1.0D;

    private AntiDomainEffectService() {
    }

    static int resolveCurrentSimpleDomainAmplifier(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 0;
        }
        MobEffectInstance simpleDomain = getSimpleDomainEffect(livingEntity);
        return simpleDomain != null ? simpleDomain.getAmplifier() : 0;
    }

    static MobEffectInstance getSimpleDomainEffect(LivingEntity livingEntity) {
        return livingEntity.getEffect(JujutsucraftModMobEffects.SIMPLE_DOMAIN.get());
    }

    static void queueSimpleDomainExtensionCost(Entity entity) {
        JujutsucraftModVariables.PlayerVariables playerVariables = JjaJujutsucraftCompat.jjaGetPlayerVariables(entity);
        JjaCursePowerAccountingService.queueSpentPower(playerVariables, HOLD_EXTENSION_COST);
    }
}
